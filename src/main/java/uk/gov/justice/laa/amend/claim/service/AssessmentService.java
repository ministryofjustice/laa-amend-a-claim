package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
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

import java.util.UUID;

/**
 * Service for amending claim values when the assessment outcome change.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AssessmentService {

    private final ClaimsApiClient claimsApiClient;
    private final AssessmentMapper assessmentMapper;
    private final ClaimStatusHandler claimStatusHandler;

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

            switch (newOutcome) {
                case NILLED -> claim.setNilledValues();
                case REDUCED -> claim.setReducedValues();
                case PAID_IN_FULL -> claim.setPaidInFullValues();
                case REDUCED_TO_FIXED_FEE -> claim.setReducedToFixedFeeValues();
                default -> log.warn("Unhandled outcome type: {}", newOutcome);
            }
        }
        //Update AssessedStatus values for each based on OutcomeType
        claimStatusHandler.updateFieldStatuses(claim, newOutcome);
    }

    public CreateAssessment201Response submitAssessment(ClaimDetails claim, String userId) {
        AssessmentPost assessment = claim.toAssessment(assessmentMapper, userId);
        ResponseEntity<CreateAssessment201Response> response = claimsApiClient.submitAssessment(claim.getClaimId(), assessment).block();
        if (response == null || response.getBody() == null) {
            throw new RuntimeException(String.format("Failed to submit assessment for claim ID: %s", claim.getClaimId()));
        }
        return response.getBody();
    }

    public ClaimDetails getLatestAssessmentByClaim(ClaimDetails claimDetails) {
        AssessmentResultSet assessmentResults = claimsApiClient.getAssessments(UUID.fromString(claimDetails.getClaimId())).block();
        if (assessmentResults == null || assessmentResults.getAssessments().isEmpty()) {
            throw new RuntimeException(String.format("Failed to get assessments for claim ID: %s", claimDetails.getClaimId()));
        }
        return assessmentMapper.mapAssessmentToClaimDetails(assessmentResults.getAssessments().getFirst(), claimDetails);
    }
}
