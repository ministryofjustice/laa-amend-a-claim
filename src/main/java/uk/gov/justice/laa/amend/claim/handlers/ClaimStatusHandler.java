package uk.gov.justice.laa.amend.claim.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.models.VatLiabilityClaimField;

import java.util.Objects;

/**
 * Handles the mapping between different outcome types and their corresponding field assessment statuses.
 */
@Component
@RequiredArgsConstructor
public class ClaimStatusHandler {

    /**
     * Updates all claim field statuses based on the outcome type
     *
     * @param claim   Claim details containing the fields to update
     * @param outcome Outcome type determining the status changes
     */
    public void updateFieldStatuses(ClaimDetails claim, OutcomeType outcome) {
        claim
            .getClaimFields()
            .forEach(field -> updateFieldStatus(field, outcome, claim));
    }

    private void updateFieldStatus(ClaimField field, OutcomeType outcome, ClaimDetails claim) {
        boolean status = determineFieldStatus(field, outcome, claim);
        field.setAssessable(status);
    }

    private boolean determineFieldStatus(ClaimField field, OutcomeType outcome, ClaimDetails claim) {
        return switch (outcome) {
            case NILLED -> handleNilledStatus(field);
            case PAID_IN_FULL -> handleAssessmentInFullStatus(field, claim);
            case REDUCED -> handleReducedStatus(field, claim);
            case REDUCED_TO_FIXED_FEE -> handleReducedToFixedFeeStatus(field);
        };
    }

    private boolean handleNilledStatus(ClaimField field) {
        return field instanceof VatLiabilityClaimField;
    }

    /**
     * Set the field status for REDUCED outcome status.
     */
    private boolean handleReducedStatus(ClaimField field, ClaimDetails claim) {
        if (field instanceof AssessedClaimField) {
            return claim.isAssessedTotalFieldAssessable();
        }
        return field.isAssessable();
    }

    /**
     * Set the field status for REDUCED_TO_FIXED_FEE outcome status.
     */
    private boolean handleReducedToFixedFeeStatus(ClaimField field) {
        return field.isAssessable();
    }

    /**
     * Set the field status for PAID_IN_FULL outcome status.
     */
    private boolean handleAssessmentInFullStatus(ClaimField field, ClaimDetails claim) {
        if (field instanceof AssessedClaimField) {
            return claim.isAssessedTotalFieldAssessable();
        }
        return field.isAssessable();
    }
}