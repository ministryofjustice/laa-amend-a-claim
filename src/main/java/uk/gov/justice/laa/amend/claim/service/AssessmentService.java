package uk.gov.justice.laa.amend.claim.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

/**
 * Service for amending claim values when the assessment outcome change.
 */
@Service
@Slf4j
public class AssessmentService {

    /**
     * Applies business logic based on the assessment outcome.
     * This method should be called whenever the assessment outcome changes.
     *
     * @param claim the claim summary to update
     * @param newOutcome the new assessment outcome
     */
    public <T extends Claim> void applyAssessmentOutcome(T claim, OutcomeType newOutcome) {
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
            case PAID_IN_FULL -> { /* implement business rules */ }
            case REDUCED -> { /* implement business rules */ }
            case REDUCED_TO_FIXED_FEE -> { /* implement business rules */ }
            default -> log.warn("Unhandled outcome type: {}", newOutcome);
        }
    }
}
