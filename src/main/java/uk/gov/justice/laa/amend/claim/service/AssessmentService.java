package uk.gov.justice.laa.amend.claim.service;

import static uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentType.ESCAPE_CASE_ASSESSMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VOID;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.exceptions.InvalidAssessmentException;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentGet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

/**
 * Service for amending claim values when the assessment outcome change.
 */
@Service
@Slf4j
public class AssessmentService {

    private final ClaimsApiClient claimsApiClient;
    private final AssessmentMapper assessmentMapper;
    private final ClaimStatusHandler claimStatusHandler;
    private final Counter assessmentSubmissionCounter;
    private final Counter assessmentSubmissionFailureCounter;
    private final BigDecimal highValueAssessmentLimit;

    public AssessmentService(
            ClaimsApiClient claimsApiClient,
            AssessmentMapper assessmentMapper,
            ClaimStatusHandler claimStatusHandler,
            MeterRegistry meterRegistry,
            @Value("${submission.high-value-assessment-limit}") BigDecimal highValueAssessmentLimit) {
        this.claimsApiClient = claimsApiClient;
        this.assessmentMapper = assessmentMapper;
        this.claimStatusHandler = claimStatusHandler;
        this.assessmentSubmissionCounter = Counter.builder("assessment.submissions")
                .description("Total number of successful assessment submissions")
                .register(meterRegistry);
        this.assessmentSubmissionFailureCounter = Counter.builder("assessment.submissions.failed")
                .description("Total number of failed assessment submissions")
                .register(meterRegistry);
        this.highValueAssessmentLimit = highValueAssessmentLimit;
    }

    /**
     * Applies business logic based on the assessment outcome.
     * This method should be called whenever the assessment outcome changes.
     *
     * @param claim the claim summary to update
     * @param newOutcome the new assessment outcome
     */
    public <T extends ClaimDetails> void applyAssessmentOutcome(T claim, OutcomeType newOutcome) {
        if (claim == null || newOutcome == null) {
            return;
        }

        OutcomeType previousOutcome = claim.getAssessmentOutcome();

        // Only apply logic if outcome has changed
        if (previousOutcome != newOutcome) {
            log.info(
                    "Applying assessment outcome logic: {} -> {} for claim {}",
                    previousOutcome,
                    newOutcome,
                    claim.getClaimId());

            claim.applyOutcome(newOutcome);
        }
        if (shouldReapplyAssessment(claim, newOutcome)) {
            assessmentMapper.mapAssessmentToClaimDetails(claim);
        }
        // Update AssessedStatus values for each based on OutcomeType
        claimStatusHandler.updateFieldStatuses(claim, newOutcome);
    }

    public CreateAssessment201Response submitAssessment(ClaimDetails claim, String userId) {
        AssessmentPost assessment = claim.toAssessment(assessmentMapper, userId);

        try {
            ResponseEntity<CreateAssessment201Response> response = claimsApiClient
                    .submitAssessment(claim.getClaimId(), assessment)
                    .block();

            if (response == null
                    || response.getBody() == null
                    || !response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(
                        String.format("Failed to submit assessment for claim ID: %s", claim.getClaimId()));
            }

            checkForHighValueAssessment(claim, assessment, response.getBody().getId());

            assessmentSubmissionCounter.increment();
            return response.getBody();
        } catch (Exception e) {
            assessmentSubmissionFailureCounter.increment();
            throw e;
        }
    }

    /**
     *
     * Retrieves the latest assessment for a claim and updates {@link ClaimDetails}
     * based on VOID or ESCAPE_CASE_ASSESSMENT rules.
     * VOID: the latest assessment must be VOID, and previous escape‑case values are applied if available.
     * Non‑VOID: the latest assessment must be ESCAPE_CASE_ASSESSMENT; null types are treated as escape‑case during migration.
     * @param claimDetails the claim for which assessments should be retrieved
     * @return the updated {@link ClaimDetails} populated with values from the latest relevant assessment
     */
    public ClaimDetails getLatestAssessmentByClaim(ClaimDetails claimDetails) {
        // Fetch the latest 5 records to find the most recent escape case
        AssessmentResultSet assessmentResults = claimsApiClient
                .getAssessments(UUID.fromString(claimDetails.getClaimId()), 0, 5, "createdOn,desc")
                .block();

        List<AssessmentGet> assessments = validateAndGetAssessments(assessmentResults, claimDetails);

        // Use first assessment to update datetime (as per requirement)
        setLastUpdatedDateTime(claimDetails, assessments.getFirst());

        var latestEscapeCaseAssessment = assessments.stream()
                .filter(Objects::nonNull)
                .filter(a -> a.getAssessmentType() == ESCAPE_CASE_ASSESSMENT)
                .findFirst()
                .orElse(null);

        if (latestEscapeCaseAssessment == null) {
            log.info(
                    "No escape case assessments found for claim ID: {} claimStatus: {}",
                    claimDetails.getClaimId(),
                    claimDetails.getStatus());
            return claimDetails;
        }

        return assessmentMapper.mapAssessmentToClaimDetails(
                assessmentMapper.updateClaim(latestEscapeCaseAssessment, claimDetails));
    }

    private AssessmentGet treatNullAsEscapeCase(AssessmentGet a) {
        if (a.getAssessmentType() == null) {
            a.setAssessmentType(ESCAPE_CASE_ASSESSMENT);
        }
        return a;
    }

    /**
     * Validates the latest assessment for VOID and non‑VOID claims.
     * - VOID - latest assessment must be VOID
     * - Non-VOID - latest assessment must ESCAPE_CASE_ASSESSMENT
     * Returns the original assessment list if validation succeeds.
     *
     * @param assessmentResults the result set returned from the assessments API
     * @param claimDetails the claim used to determine validation rules (VOID vs non‑VOID)
     * @throws RuntimeException if the result set is null or contains no assessments
     * @throws InvalidAssessmentException if the first assessment violates required business rules
     */
    private List<AssessmentGet> validateAndGetAssessments(
            AssessmentResultSet assessmentResults, ClaimDetails claimDetails) {
        if (assessmentResults == null || assessmentResults.getAssessments().isEmpty()) {
            throw new RuntimeException(
                    String.format("Failed to get assessments for claim ID: %s", claimDetails.getClaimId()));
        }

        // Currently treats null as Escape case assessments until the migration completes for VOID assessments.
        // This should be to just retrieve assessments from AssessmentResults after migration.
        List<AssessmentGet> assessments = assessmentResults.getAssessments().stream()
                .filter(Objects::nonNull)
                .map(this::treatNullAsEscapeCase)
                .toList();

        AssessmentGet firstAssessment = assessments.getFirst();

        // Validate first assessment based on claim status
        if (claimDetails.getStatus() == VOID) {
            if (firstAssessment.getAssessmentType() != AssessmentType.VOID) {
                throw new InvalidAssessmentException(String.format(
                        "VOID Assessment state failed, latest Assessment must be VOID for claim ID: %s",
                        claimDetails.getClaimId()));
            }

        } else {
            if (firstAssessment.getAssessmentType() != ESCAPE_CASE_ASSESSMENT) {
                throw new InvalidAssessmentException(String.format(
                        "Assessment state failed, latest Assessment must be ESCAPE_CASE_ASSESSMENT for claim ID: %s",
                        claimDetails.getClaimId()));
            }
        }
        return assessments;
    }

    private void setLastUpdatedDateTime(ClaimDetails claimDetails, AssessmentGet latestAssessment) {
        claimDetails.setLastUpdatedUser(latestAssessment.getCreatedByUserId());
        claimDetails.setLastUpdatedDateTime(latestAssessment.getCreatedOn());
    }

    private boolean shouldReapplyAssessment(ClaimDetails claim, OutcomeType newOutcome) {
        if (claim == null || !claim.isHasAssessment()) {
            return false;
        }
        var last = claim.getLastAssessment();
        if (last == null) {
            return false;
        }
        return Objects.equals(newOutcome, last.getLastAssessmentOutcome());
    }

    private void checkForHighValueAssessment(ClaimDetails claim, AssessmentPost assessment, UUID assessmentId) {
        if (assessment.getAssessedTotalInclVat() != null
                && assessment.getAssessedTotalInclVat().compareTo(highValueAssessmentLimit) >= 0) {
            log.warn(
                    "HIGH_VALUE_ASSESSMENT: detected for claimId {}, providerAccountNumber {}, uniqueFileNumber {},"
                            + " assessmentId {}, assessmentOutcome {}, assessedTotalInclVat {}, allowedTotalInclVat"
                            + " {}",
                    claim.getClaimId(),
                    claim.getProviderAccountNumber(),
                    claim.getUniqueFileNumber(),
                    assessmentId,
                    claim.getAssessmentOutcome(),
                    assessment.getAssessedTotalInclVat(),
                    assessment.getAllowedTotalInclVat());
        }
    }
}
