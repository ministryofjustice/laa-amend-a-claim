package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.AllowedClaimField;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.BoltOnClaimField;
import uk.gov.justice.laa.amend.claim.models.CalculatedTotalClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CostClaimField;
import uk.gov.justice.laa.amend.claim.models.FixedFeeClaimField;
import uk.gov.justice.laa.amend.claim.models.VatLiabilityClaimField;
import uk.gov.justice.laa.amend.claim.utils.FormUtils;

import static uk.gov.justice.laa.amend.claim.utils.NumberUtils.getOrElseZero;

@Getter
@AllArgsConstructor
public class ClaimFieldRow {

    private final String key;
    private Object submitted;
    private Object calculated;
    private Object assessed;
    private final boolean assessable;
    private final String changeUrl;

    public static ClaimFieldRow from(AllowedClaimField claimField) {
        return new ClaimFieldRow(
            claimField.getKey(),
            claimField.getSubmitted(),
            getOrElseZero(claimField.getCalculated()),
            claimField.getAssessed(),
            claimField.isAssessable(),
            "/submissions/%s/claims/%s/allowed-totals"
        );
    }

    public static ClaimFieldRow from(AssessedClaimField claimField, ClaimDetails claim) {
        Object assessed;
        if (claimField.getAssessed() == null) {
            switch (claimField.getType()) {
                case TOTAL_VAT -> assessed = claim.getAllowedTotalVat().getAssessed();
                case TOTAL_INCL_VAT -> assessed = claim.getAllowedTotalInclVat().getAssessed();
                default -> assessed = null;
            }
        } else {
            assessed = claimField.getAssessed();
        }
        return new ClaimFieldRow(
            claimField.getKey(),
            claimField.getSubmitted(),
            claimField.getCalculated(),
            assessed,
            claimField.isAssessable(),
            "/submissions/%s/claims/%s/assessed-totals"
        );
    }

    public static ClaimFieldRow from(BoltOnClaimField claimField) {
        if (claimField.hasSubmittedValue()) {
            return new ClaimFieldRow(
                claimField.getKey(),
                claimField.getSubmitted(),
                claimField.getCalculated(),
                claimField.getAssessed(),
                claimField.isAssessable(),
                null
            );
        }
        return null;
    }

    public static ClaimFieldRow from(CostClaimField claimField) {
        Object submitted;
        Object calculated;
        Object assessed;
        switch (claimField.getCost()) {
            case PROFIT_COSTS, DISBURSEMENTS, DISBURSEMENTS_VAT -> {
                submitted = claimField.getSubmitted();
                calculated = claimField.getCalculated();
                assessed = claimField.getAssessed();
            }
            default -> {
                submitted = getOrElseZero(claimField.getSubmitted());
                calculated = getOrElseZero(claimField.getCalculated());
                assessed = getOrElseZero(claimField.getAssessed());
            }
        }
        return new ClaimFieldRow(
            claimField.getKey(),
            submitted,
            calculated,
            assessed,
            claimField.isAssessable(),
            claimField.getCost().getChangeUrl()
        );
    }

    public static ClaimFieldRow from(CalculatedTotalClaimField claimField) {
        return new ClaimFieldRow(
            claimField.getKey(),
            claimField.getSubmitted(),
            claimField.getCalculated(),
            claimField.getAssessed(),
            claimField.isAssessable(),
            null
        );
    }

    public static ClaimFieldRow from(FixedFeeClaimField claimField) {
        return new ClaimFieldRow(
            claimField.getKey(),
            claimField.getSubmitted(),
            claimField.getCalculated(),
            claimField.getAssessed(),
            claimField.isAssessable(),
            null
        );
    }

    public static ClaimFieldRow from(VatLiabilityClaimField claimField) {
        return new ClaimFieldRow(
            claimField.getKey(),
            claimField.getSubmitted(),
            claimField.getCalculated(),
            claimField.getAssessed(),
            claimField.isAssessable(),
            "/submissions/%s/claims/%s/assessment-outcome"
        );
    }

    public String getLabel() {
        return String.format("claimSummary.rows.%s", key);
    }

    public String getId() {
        return FormUtils.toFieldId(key);
    }

    public String getErrorKey() {
        return String.format("claimSummary.rows.%s.error", key);
    }

    public String getChangeUrl(String submissionId, String claimId) {
        if (changeUrl == null) {
            return null;
        }

        return String.format(changeUrl, submissionId, claimId);
    }

    public boolean isAssessableAndUnassessed() {
        return isAssessable() && !isAssessed();
    }

    public boolean isAssessableAndAssessed() {
        return isAssessable() && isAssessed();
    }

    public boolean isNotAssessable() {
        return !isAssessable();
    }

    public boolean isAssessed() {
        return assessed != null;
    }
}
