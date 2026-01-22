package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.AllowedClaimField;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CostClaimField;
import uk.gov.justice.laa.amend.claim.models.VatLiabilityClaimField;
import uk.gov.justice.laa.amend.claim.utils.FormUtils;

import static uk.gov.justice.laa.amend.claim.utils.NumberUtils.getOrElseZero;

@Getter
public class ClaimFieldRow {

    private final String key;
    private Object submitted;
    private Object calculated;
    private Object assessed;
    private final boolean assessable;
    private final String changeUrl;

    public ClaimFieldRow(ClaimField claimField) {
        this.key = claimField.getKey();
        this.submitted = claimField.getSubmitted();
        this.calculated = claimField.getCalculated();
        this.assessed = claimField.getAssessed();
        this.assessable = claimField.isAssessable();

        switch (claimField) {
            case CostClaimField x -> {
                switch (x.getCost()) {
                    case PROFIT_COSTS, DISBURSEMENTS, DISBURSEMENTS_VAT -> { }
                    default -> {
                        this.submitted = getOrElseZero(submitted);
                        this.calculated = getOrElseZero(calculated);
                        this.assessed = getOrElseZero(assessed);
                    }
                }
                this.changeUrl = x.getCost().getChangeUrl();
            }
            case AllowedClaimField x -> {
                this.calculated = getOrElseZero(calculated);
                this.changeUrl = "/submissions/%s/claims/%s/allowed-totals";
            }
            case AssessedClaimField x -> this.changeUrl = "/submissions/%s/claims/%s/assessed-totals";
            case VatLiabilityClaimField x -> this.changeUrl = "/submissions/%s/claims/%s/assessment-outcome";
            default -> this.changeUrl = null;
        }
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
