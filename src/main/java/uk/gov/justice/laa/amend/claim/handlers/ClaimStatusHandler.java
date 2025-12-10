package uk.gov.justice.laa.amend.claim.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.AssessStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
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
        List<ClaimField> fields = extractClaimFields(claim);
        fields.stream()
                .filter(Objects::nonNull)
                .forEach(field -> updateFieldStatus(field, outcome, claim));
    }

    private void updateFieldStatus(ClaimField field, OutcomeType outcome, ClaimDetails claim) {
        AssessStatus status = determineFieldStatus(field, outcome, claim);
        field.setStatus(status);
    }

    private AssessStatus determineFieldStatus(ClaimField field, OutcomeType outcome, ClaimDetails claim) {
        return switch (outcome) {
            case NILLED -> handleNilledStatus(field, claim);
            case PAID_IN_FULL -> handleAssessmentInFull(field, claim);
            case REDUCED -> handleReducedStatus(field, claim);
            case REDUCED_TO_FIXED_FEE -> handleReducedToFixedFeeStatus(field, claim);
        };
    }

    private AssessStatus handleNilledStatus(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim)) {
            return AssessStatus.DO_NOT_DISPLAY;
        } else if (isNeedsAssessing(field, claim)) {
            return AssessStatus.NEEDS_ASSESSING;
        }
        return field == claim.getVatClaimed() ? AssessStatus.ASSESSABLE : AssessStatus.NOT_ASSESSABLE;
    }

    /**
     * Set the field status for REDUCED outcome status.
     */
    private AssessStatus handleReducedStatus(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim) && isNotValidFeeCode(claim)) {
            return AssessStatus.DO_NOT_DISPLAY;
        } else if (isNeedsAssessing(field, claim)) {
            return AssessStatus.NEEDS_ASSESSING;
        }
        return switch (field.getKey()) {
            // Bolt-ons are set to not Assessable
            case ADJOURNED_FEE, CMRH_ORAL, CMRH_TELEPHONE,
                 HO_INTERVIEW, SUBSTANTIVE_HEARING, FIXED_FEE -> AssessStatus.NOT_ASSESSABLE;
            default -> AssessStatus.ASSESSABLE;
        };
    }

    /**
     * Set the field status for REDUCED_TO_FIXED_FEE outcome status.
     */
    private AssessStatus handleReducedToFixedFeeStatus(ClaimField field, ClaimDetails claim) {
        if (isNeedsAssessing(field, claim)) {
            return AssessStatus.NEEDS_ASSESSING;
        }
        return AssessStatus.ASSESSABLE;
    }

    /**
     * Set the field status for PAID_IN_FULL outcome status.
     */
    private AssessStatus handleAssessmentInFull(ClaimField field, ClaimDetails claim) {
        if (isAssessedTotalFields(field, claim) && isNotValidFeeCode(claim)) {
            return AssessStatus.DO_NOT_DISPLAY;
        } else if (isNeedsAssessing(field, claim)) {
            return AssessStatus.NEEDS_ASSESSING;
        } else if (field == claim.getFixedFee()) {
            return  AssessStatus.NOT_ASSESSABLE;
        }
        return AssessStatus.ASSESSABLE;
    }

    /**
     * NEEDS_ASSESSMENT decides to display add link or not.
     */
    private boolean isNeedsAssessing(ClaimField field, ClaimDetails claim) {
        return (field == claim.getNetProfitCost() || isTotalFields(field, claim)) && field.getAssessed() == null;
    }

    /**
     * Check if the field is Assessed Total
     */
    private boolean isAssessedTotalFields(ClaimField field, ClaimDetails claim) {
        return field == claim.getAssessedTotalVat() || field == claim.getAssessedTotalInclVat();
    }

    private boolean isTotalFields(ClaimField field, ClaimDetails claim) {
        return field == claim.getAssessedTotalVat() || field == claim.getAssessedTotalInclVat() || field == claim.getAllowedTotalVat() || field == claim.getAllowedTotalInclVat();
    }

    private List<ClaimField> getCivilFields(CivilClaimDetails civil) {
        return Arrays.asList(
                civil.getHoInterview(),
                civil.getSubstantiveHearing(),
                civil.getCounselsCost(),
                civil.getJrFormFillingCost(),
                civil.getAdjournedHearing(),
                civil.getCmrhOral(),
                civil.getCmrhTelephone(),
                civil.getDetentionTravelWaitingCosts()
        );
    }

    private List<ClaimField> getCrimeFields(CrimeClaimDetails crime) {
        return Arrays.asList(crime.getTravelCosts(), crime.getWaitingCosts());
    }

    private List<ClaimField> extractClaimFields(ClaimDetails claim) {
        List<ClaimField> commonFields = Arrays.asList(
                claim.getVatClaimed(), claim.getFixedFee(),
                claim.getNetProfitCost(),
                claim.getNetDisbursementAmount(),
                claim.getDisbursementVatAmount(),
                claim.getTotalAmount(),
                claim.getAssessedTotalVat(),
                claim.getAssessedTotalInclVat(),
                claim.getAllowedTotalVat(),
                claim.getAllowedTotalInclVat()
        );

        List<ClaimField> specificFields = switch (claim) {
            case CivilClaimDetails civil -> getCivilFields(civil);
            case CrimeClaimDetails crime -> getCrimeFields(crime);
            default -> List.of();
        };
        return Stream.concat(commonFields.stream(), specificFields.stream()).filter(Objects::nonNull).toList();
    }
}