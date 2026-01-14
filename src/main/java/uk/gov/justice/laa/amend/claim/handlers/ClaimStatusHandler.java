package uk.gov.justice.laa.amend.claim.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldType;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.util.List;
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
        List<ClaimField> fields = claim.getClaimFields();
        fields.stream()
                .filter(Objects::nonNull)
                .forEach(field -> updateFieldStatus(field, outcome, claim));
    }

    private void updateFieldStatus(ClaimField field, OutcomeType outcome, ClaimDetails claim) {
        ClaimFieldStatus status = determineFieldStatus(field, outcome, claim);
        field.setStatus(status);
    }

    private ClaimFieldStatus determineFieldStatus(ClaimField field, OutcomeType outcome, ClaimDetails claim) {
        return switch (outcome) {
            case NILLED -> handleNilledStatus(field, claim);
            case PAID_IN_FULL -> handleAssessmentInFullStatus(field, claim);
            case REDUCED -> handleReducedStatus(field, claim);
            case REDUCED_TO_FIXED_FEE -> handleReducedToFixedFeeStatus(field);
        };
    }

    private ClaimFieldStatus handleNilledStatus(ClaimField field, ClaimDetails claim) {
        return field == claim.getVatClaimed() ? ClaimFieldStatus.MODIFIABLE : ClaimFieldStatus.NOT_MODIFIABLE;
    }

    /**
     * Set the field status for REDUCED outcome status.
     */
    private ClaimFieldStatus handleReducedStatus(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalField(field)) {
            return claim.isAssessedTotalFieldModifiable() ? ClaimFieldStatus.MODIFIABLE : ClaimFieldStatus.NOT_MODIFIABLE;
        }
        return isFieldModifiable(field);
    }

    /**
     * Set the field status for REDUCED_TO_FIXED_FEE outcome status.
     */
    private ClaimFieldStatus handleReducedToFixedFeeStatus(ClaimField field) {
        return isFieldModifiable(field);
    }

    /**
     * Set the field status for PAID_IN_FULL outcome status.
     */
    private ClaimFieldStatus handleAssessmentInFullStatus(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalField(field)) {
            return claim.isAssessedTotalFieldModifiable() ? ClaimFieldStatus.MODIFIABLE : ClaimFieldStatus.NOT_MODIFIABLE;
        }
        return isFieldModifiable(field);
    }

    /**
     * Check if the field is Assessed Total
     */
    private boolean isAssessedTotalField(ClaimField field) {
        return field.getType() == ClaimFieldType.ASSESSED;
    }

    private ClaimFieldStatus isFieldModifiable(ClaimField field) {
        return field.getType().isNotAssessable() ? ClaimFieldStatus.NOT_MODIFIABLE : ClaimFieldStatus.MODIFIABLE;
    }
}