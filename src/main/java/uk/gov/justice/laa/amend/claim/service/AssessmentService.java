package uk.gov.justice.laa.amend.claim.service;

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
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentGet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

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
    this.assessmentSubmissionCounter =
        Counter.builder("assessment.submissions")
            .description("Total number of successful assessment submissions")
            .register(meterRegistry);
    this.assessmentSubmissionFailureCounter =
        Counter.builder("assessment.submissions.failed")
            .description("Total number of failed assessment submissions")
            .register(meterRegistry);
    this.highValueAssessmentLimit = highValueAssessmentLimit;
  }

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
      ResponseEntity<CreateAssessment201Response> response =
          claimsApiClient.submitAssessment(claim.getClaimId(), assessment).block();

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
   * Retrieves the latest assessment for a claim and updates {@link ClaimDetails}. VOID: the latest
   * assessment must be VOID, and previous non-VOID values are applied if available. Non-VOID: the
   * latest assessment must not be VOID.
   *
   * @param claimDetails the claim for which assessments should be retrieved
   * @return the updated {@link ClaimDetails} populated with values from the latest relevant
   *     assessment
   */
  public ClaimDetails getLatestAssessmentByClaim(ClaimDetails claimDetails) {
    List<AssessmentGet> assessments =
        getValidatedAssessments(claimDetails.getClaimId(), claimDetails.isVoided());

    setLastUpdatedDateTime(claimDetails, assessments.getFirst());

    var latestNonVoidAssessment =
        assessments.stream()
            .filter(Objects::nonNull)
            .filter(a -> a.getAssessmentType() != AssessmentType.VOID)
            .findFirst()
            .orElse(null);

    if (latestNonVoidAssessment == null) {
      log.info(
          "No non-VOID assessments found for claim ID: {} claimStatus: {}",
          claimDetails.getClaimId(),
          claimDetails.getStatus());
      return claimDetails;
    }

    return assessmentMapper.mapAssessmentToClaimDetails(
        assessmentMapper.updateClaim(latestNonVoidAssessment, claimDetails));
  }

  public void populateClaimValues(List<Claim> claims) {
    claims.stream().filter(Objects::nonNull).forEach(this::populateClaimValue);
  }

  public List<AssessmentInfo> getLatestAssessmentsByClaim(UUID claimId, int limit) {
    var response = claimsApiClient.getAssessments(claimId, 0, limit, "createdOn,desc").block();

    Objects.requireNonNull(response);
    Objects.requireNonNull(response.getAssessments());

    return response.getAssessments().stream().map(assessmentMapper::mapAssessment).toList();
  }

  private void populateClaimValue(Claim claim) {
    if (!(claim.isVoided() || Boolean.TRUE.equals(claim.getHasAssessment()))) {
      return;
    }
    List<AssessmentGet> assessments = getValidatedAssessments(claim.getClaimId(), claim.isVoided());
    claim.setClaimValue(assessments.getFirst().getAssessedTotalInclVat());
  }

  private List<AssessmentGet> getValidatedAssessments(UUID claimId, boolean isVoided) {
    AssessmentResultSet assessmentResults =
        claimsApiClient.getAssessments(claimId, 0, 5, "createdOn,desc").block();
    return validateAssessmentResults(assessmentResults, claimId, isVoided);
  }

  private List<AssessmentGet> validateAssessmentResults(
      AssessmentResultSet assessmentResults, UUID claimId, boolean isVoided) {
    if (assessmentResults == null || assessmentResults.getAssessments().isEmpty()) {
      throw new RuntimeException(
          String.format("Failed to get assessments for claim ID: %s", claimId));
    }

    log.info(
        "Number of total assessments found: {} for claim Id: {}",
        assessmentResults.getTotalElements(),
        claimId);

    List<AssessmentGet> assessments =
        assessmentResults.getAssessments().stream().filter(Objects::nonNull).toList();

    AssessmentGet firstAssessment = assessments.getFirst();

    if (isVoided && firstAssessment.getAssessmentType() != AssessmentType.VOID) {
      throw new InvalidAssessmentException(
          String.format(
              "VOID Assessment state failed, latest Assessment must be VOID for claim ID: %s",
              claimId));
    }

    if (!isVoided && firstAssessment.getAssessmentType() == AssessmentType.VOID) {
      throw new InvalidAssessmentException(
          String.format(
              "Assessment state failed, latest Assessment must not be VOID for claim ID: %s",
              claimId));
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
    return Objects.equals(newOutcome, last.lastAssessmentOutcome());
  }

  private void checkForHighValueAssessment(
      ClaimDetails claim, AssessmentPost assessment, UUID assessmentId) {
    if (assessment.getAssessedTotalInclVat() != null
        && assessment.getAssessedTotalInclVat().compareTo(highValueAssessmentLimit) >= 0) {
      log.warn(
          "HIGH_VALUE_ASSESSMENT: detected for claimId {}, officeCode {}, uniqueFileNumber {},"
              + " assessmentId {}, assessmentOutcome {}, assessedTotalInclVat {}, allowedTotalInclVat"
              + " {}",
          claim.getClaimId(),
          claim.getOfficeCode(),
          claim.getUniqueFileNumber(),
          assessmentId,
          claim.getAssessmentOutcome(),
          assessment.getAssessedTotalInclVat(),
          assessment.getAllowedTotalInclVat());
    }
  }
}
