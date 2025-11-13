package uk.gov.justice.laa.amend.claim.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimSummary;

import java.math.BigDecimal;

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
     * @param claimSummary the claim summary to update
     * @param newOutcome the new assessment outcome
     */
    public void applyAssessmentOutcome(ClaimSummary claimSummary, OutcomeType newOutcome) {
        if (claimSummary == null || newOutcome == null) {
            return;
        }

        OutcomeType previousOutcome = claimSummary.getAssessmentOutcome();

        // Only apply logic if outcome has changed
        if (previousOutcome == newOutcome) {
            return;
        }

        log.info("Applying assessment outcome logic: {} -> {} for claim {}",
            previousOutcome, newOutcome, claimSummary.getClaimId());

        switch (newOutcome) {
            case NILLED -> applyNilledOutcome(claimSummary);
            case PAID_IN_FULL -> applyPaidInFullOutcome(claimSummary);
            case REDUCED -> applyReducedOutcome(claimSummary);
            case REDUCED_TO_FIXED_FEE -> applyReducedToFixedFeeOutcome(claimSummary);
        }
    }

    /**
     * Applies NILLED outcome logic: sets all amendable values based on their type.
     * - Monetary fields (BigDecimal) → £0.00
     * - Count fields (Integer) → 0
     * - Boolean fields (Boolean) → false (No)
     * - VAT and Total are NOT changed (calculated fields)
     *
     * @param claimSummary the claim summary to update
     */
    private void applyNilledOutcome(ClaimSummary claimSummary) {
        log.debug("Applying NILLED outcome: setting all amendable values appropriately");

        // Set common monetary fields (BigDecimal) to £0.00
        setAmendedValue(claimSummary.getNetProfitCost(), BigDecimal.ZERO);
        setAmendedValue(claimSummary.getNetDisbursementAmount(), BigDecimal.ZERO);
        setAmendedValue(claimSummary.getDisbursementVatAmount(), BigDecimal.ZERO);

        // Apply Civil specific fields
        if (claimSummary instanceof CivilClaimSummary civilClaim) {
            log.debug("Applying NILLED to Civil claim specific fields");
            // Monetary fields (BigDecimal) → £0.00
            setAmendedValue(civilClaim.getCounselsCost(), BigDecimal.ZERO);
            setAmendedValue(civilClaim.getDetentionTravelWaitingCosts(), BigDecimal.ZERO);
            setAmendedValue(civilClaim.getJrFormFillingCost(), BigDecimal.ZERO);
            // Boolean field → false (No)
            setAmendedValue(civilClaim.getAdjournedHearing(), false);
            // Integer fields (counts) → 0
            setAmendedValue(civilClaim.getCmrhTelephone(), 0);
            setAmendedValue(civilClaim.getCmrhOral(), 0);
            setAmendedValue(civilClaim.getHoInterview(), 0);
            setAmendedValue(civilClaim.getSubstantiveHearing(), 0);
        }

        // Apply Crime specific fields
        if (claimSummary instanceof CrimeClaimSummary crimeClaim) {
            log.debug("Applying NILLED to Crime claim specific fields");
            // Monetary fields (BigDecimal) → £0.00I
            setAmendedValue(crimeClaim.getTravelCosts(), BigDecimal.ZERO);
            setAmendedValue(crimeClaim.getWaitingCosts(), BigDecimal.ZERO);
        }
    }


    /**
     * Applies PAID_IN_FULL outcome logic.
     * TODO: Implement business rules for paid in full outcome
     *
     * @param claimSummary the claim summary to update
     */
    private void applyPaidInFullOutcome(ClaimSummary claimSummary) {
        log.debug("Applying PAID_IN_FULL outcome");
        // TODO: Implement business logic
    }

    /**
     * Applies REDUCED outcome logic.
     * TODO: Implement business rules for reduced outcome
     *
     * @param claimSummary the claim summary to update
     */
    private void applyReducedOutcome(ClaimSummary claimSummary) {
        log.debug("Applying REDUCED outcome");
        // TODO: Implement business logic
    }

    /**
     * Applies REDUCED_TO_FIXED_FEE outcome logic.
     * TODO: Implement business rules for reduced to fixed fee outcome
     *
     * @param claimSummary the claim summary to update
     */
    private void applyReducedToFixedFeeOutcome(ClaimSummary claimSummary) {
        log.debug("Applying REDUCED_TO_FIXED_FEE outcome");
        // TODO: Implement business logic
    }

    /**
     * Helper method to safely set amended value on a ClaimFieldRow.
     *
     * @param fieldRow the field row to update
     * @param value the value to set
     */
    private void setAmendedValue(ClaimFieldRow fieldRow, Object value) {
        if (fieldRow != null) {
            fieldRow.setAmended(value);
        }
    }
}
