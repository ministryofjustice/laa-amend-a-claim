package uk.gov.justice.laa.amend.claim.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

import java.util.Objects;
import java.util.UUID;

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

    public AssessmentService(ClaimsApiClient claimsApiClient,
                             AssessmentMapper assessmentMapper,
                             ClaimStatusHandler claimStatusHandler,
                             MeterRegistry meterRegistry) {
        this.claimsApiClient = claimsApiClient;
        this.assessmentMapper = assessmentMapper;
        this.claimStatusHandler = claimStatusHandler;
        this.assessmentSubmissionCounter = Counter.builder("assessment.submissions")
                .description("Total number of successful assessment submissions")
                .register(meterRegistry);
        this.assessmentSubmissionFailureCounter = Counter.builder("assessment.submissions.failed")
                .description("Total number of failed assessment submissions")
                .register(meterRegistry);
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
            log.info("Applying assessment outcome logic: {} -> {} for claim {}",
                    previousOutcome, newOutcome, claim.getClaimId());

            claim.applyOutcome(newOutcome);
        }
        if (shouldReapplyAssessment(claim, newOutcome)) {
            assessmentMapper.mapAssessmentToClaimDetails(claim);
        }
        //Update AssessedStatus values for each based on OutcomeType
        claimStatusHandler.updateFieldStatuses(claim, newOutcome);
    }

    public CreateAssessment201Response submitAssessment(ClaimDetails claim, String userId) {
        AssessmentPost assessment = claim.toAssessment(assessmentMapper, userId);

        try {
            ResponseEntity<CreateAssessment201Response> response = claimsApiClient.submitAssessment(claim.getClaimId(), assessment).block();

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(String.format("Failed to submit assessment for claim ID: %s", claim.getClaimId()));
            }
            assessmentSubmissionCounter.increment();
            return response.getBody();
        } catch (Exception e) {
            assessmentSubmissionFailureCounter.increment();
            throw e;
        }
    }

    public ClaimDetails getLatestAssessmentByClaim(ClaimDetails claimDetails) {
        AssessmentResultSet assessmentResults = claimsApiClient.getAssessments(
            UUID.fromString(claimDetails.getClaimId()),
            0,
            1,
            "createdOn,desc"
        ).block();
        if (assessmentResults == null || assessmentResults.getAssessments().isEmpty()) {
            throw new RuntimeException(String.format("Failed to get assessments for claim ID: %s", claimDetails.getClaimId()));
        }
        var assessment = assessmentResults.getAssessments().getFirst();
        return assessmentMapper.mapAssessmentToClaimDetails(assessmentMapper.updateClaim(assessment, claimDetails));
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

}
