package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.client.config.SearchProperties;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

/**
 * Service for amending claim values when the assessment outcome change.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AssessmentService {

    private final ClaimsApiClient claimsApiClient;
    private final AssessmentMapper assessmentMapper;

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
        if (previousOutcome == newOutcome) {
            return;
        }

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

    public ResponseEntity<CreateAssessment201Response> submitAssessment(ClaimDetails claim, String userId) {
        AssessmentPost assessment = claim.toAssessment(assessmentMapper, userId);
        return claimsApiClient.submitAssessment(claim.getClaimId(), assessment).block();
    }
}
