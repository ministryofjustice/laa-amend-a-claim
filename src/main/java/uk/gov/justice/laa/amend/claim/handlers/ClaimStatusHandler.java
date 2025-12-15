package uk.gov.justice.laa.amend.claim.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;
import static uk.gov.justice.laa.amend.claim.validators.FeeCodeValidator.isNotValidFeeCode;

/**
 * Handles the mapping between different outcome types and their corresponding field assessment statuses.
 */
@Component
@RequiredArgsConstructor
public class ClaimStatusHandler {


    private static final Set<String> NON_ASSESSABLE_KEYS = Set.of(
            ADJOURNED_FEE, CMRH_ORAL, CMRH_TELEPHONE,
            HO_INTERVIEW, SUBSTANTIVE_HEARING, FIXED_FEE, TOTAL
    );

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
            case PAID_IN_FULL -> handleAssessmentInFull(field, claim);
            case REDUCED -> handleReducedStatus(field, claim);
            case REDUCED_TO_FIXED_FEE -> handleReducedToFixedFeeStatus(field);
        };
    }

    private ClaimFieldStatus handleNilledStatus(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim)) {
            return ClaimFieldStatus.DO_NOT_DISPLAY;
        } else if (field == claim.getVatClaimed()) {
            return ClaimFieldStatus.MODIFIABLE;
        }
        return ClaimFieldStatus.NOT_MODIFIABLE;
    }

    /**
     * Set the field status for REDUCED outcome status.
     */
    private ClaimFieldStatus handleReducedStatus(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim) && isNotValidFeeCode(claim)) {
            return ClaimFieldStatus.DO_NOT_DISPLAY;
        }
        return checkAssessableFields(field);
    }

    private ClaimFieldStatus checkAssessableFields(ClaimField field) {
        return NON_ASSESSABLE_KEYS.contains(field.getKey())
                ? ClaimFieldStatus.NOT_MODIFIABLE
                : ClaimFieldStatus.MODIFIABLE;
    }

    /**
     * Set the field status for REDUCED_TO_FIXED_FEE outcome status.
     */
    private ClaimFieldStatus handleReducedToFixedFeeStatus(ClaimField field) {
        return checkAssessableFields(field);
    }

    /**
     * Set the field status for PAID_IN_FULL outcome status.
     */
    private ClaimFieldStatus handleAssessmentInFull(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim) && isNotValidFeeCode(claim)) {
            return ClaimFieldStatus.DO_NOT_DISPLAY;
        } else if (field == claim.getFixedFee() || field == claim.getTotalAmount()) {
            return  ClaimFieldStatus.NOT_MODIFIABLE;
        }
        return ClaimFieldStatus.MODIFIABLE;
    }

    /**
     * Check if the field is Assessed Total
     */
    private boolean isAssessedTotalFields(ClaimField field, ClaimDetails claim) {
        return field == claim.getAssessedTotalVat() || field == claim.getAssessedTotalInclVat();
    }
}