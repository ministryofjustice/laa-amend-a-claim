package uk.gov.justice.laa.amend.claim.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.AssessmentStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.util.List;
import java.util.Objects;

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
        AssessmentStatus status = determineFieldStatus(field, outcome, claim);
        field.setStatus(status);
    }

    private AssessmentStatus determineFieldStatus(ClaimField field, OutcomeType outcome, ClaimDetails claim) {
        return switch (outcome) {
            case NILLED -> handleNilledStatus(field, claim);
            case PAID_IN_FULL -> handleAssessmentInFull(field, claim);
            case REDUCED -> handleReducedStatus(field, claim);
            case REDUCED_TO_FIXED_FEE -> handleReducedToFixedFeeStatus(field, claim);
        };
    }

    private AssessmentStatus handleNilledStatus(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim)) {
            return AssessmentStatus.DO_NOT_DISPLAY;
        } else if (field == claim.getVatClaimed()) {
            return AssessmentStatus.ASSESSABLE;
        }
        return AssessmentStatus.NOT_ASSESSABLE;
    }

    /**
     * Set the field status for REDUCED outcome status.
     */
    private AssessmentStatus handleReducedStatus(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim) && isNotValidFeeCode(claim)) {
            return AssessmentStatus.DO_NOT_DISPLAY;
        }
        return switch (field.getKey()) {
            // Bolt-ons are set to not Assessable
            case ADJOURNED_FEE, CMRH_ORAL, CMRH_TELEPHONE,
                 HO_INTERVIEW, SUBSTANTIVE_HEARING, FIXED_FEE, TOTAL -> AssessmentStatus.NOT_ASSESSABLE;
            default -> AssessmentStatus.ASSESSABLE;
        };
    }

    /**
     * Set the field status for REDUCED_TO_FIXED_FEE outcome status.
     */
    private AssessmentStatus handleReducedToFixedFeeStatus(ClaimField field, ClaimDetails claim) {
        if (field == claim.getTotalAmount() || field == claim.getFixedFee()) {
            return AssessmentStatus.NOT_ASSESSABLE;
        }
        return AssessmentStatus.ASSESSABLE;
    }

    /**
     * Set the field status for PAID_IN_FULL outcome status.
     */
    private AssessmentStatus handleAssessmentInFull(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim) && isNotValidFeeCode(claim)) {
            return AssessmentStatus.DO_NOT_DISPLAY;
        } else if (field == claim.getFixedFee() || field == claim.getTotalAmount()) {
            return  AssessmentStatus.NOT_ASSESSABLE;
        }
        return AssessmentStatus.ASSESSABLE;
    }

    /**
     * Check if the field is Assessed Total
     */
    private boolean isAssessedTotalFields(ClaimField field, ClaimDetails claim) {
        return field == claim.getAssessedTotalVat() || field == claim.getAssessedTotalInclVat();
    }
}