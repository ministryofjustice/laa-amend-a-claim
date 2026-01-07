package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.amend.claim.utils.FormUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.utils.NumberUtils.getOrElseZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimField implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String key;
    private Object submitted;
    private Object calculated;
    private Object assessed;
    private String changeUrl;
    private ClaimFieldStatus status;

    public ClaimField(String key, Object submitted, Object calculated) {
        this(key, submitted, calculated, (String) null);
    }

    public ClaimField(String key, Object submitted, Object calculated, Object assessed) {
        this(key, submitted, calculated);
        this.assessed = assessed;
    }

    public ClaimField(String key, Object submitted, Object calculated, Cost cost) {
        this(key, submitted, calculated, cost.getChangeUrl());
    }

    public ClaimField(String key, Object submitted, Object calculated, String changeUrl) {
        this.key = key;
        this.submitted = submitted;
        this.calculated = calculated;
        this.assessed = submitted;
        this.changeUrl = changeUrl;
    }

    public ClaimField(String key, Object submitted, Object calculated, Object amended, Object assessed) {
        this(key, submitted, calculated, amended);
        this.assessed = assessed;
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

    /**
     * Returns the change URL for a given submission ID and claim ID
     *
     * @param submissionId the submission ID
     * @param claimId the claim ID
     * @return the change URL, or null if not editable
     */
    public String getChangeUrl(String submissionId, String claimId) {
        if (changeUrl == null) {
            return null;
        }

        return String.format(changeUrl, submissionId, claimId);
    }

    protected void setNilled() {
        setAssessed(BigDecimal.ZERO);
    }

    protected void setToNeedsAssessing() {
        setAssessed(null);
    }

    protected void setAssessedToCalculated() {
        setAssessedToValue(this.getCalculated());
    }

    protected void setAssessedToSubmitted() {
        setAssessedToValue(this.getSubmitted());
    }

    protected void setToNotApplicable() {
        setAssessed(null);
    }


    protected void setToDoNotDisplay() {
        setAssessed(null);
    }

    public void setAssessedToValue(Object value) {
        setAssessed(value);
    }

    public boolean isAssessableAndUnassessed() {
        return status == ClaimFieldStatus.MODIFIABLE && assessed == null;
    }

    public boolean isAssessableAndAssessed() {
        return status == ClaimFieldStatus.MODIFIABLE && assessed != null;
    }

    public boolean isNotAssessable() {
        return status != ClaimFieldStatus.MODIFIABLE;
    }

    private static final List<String> HIDDEN_FIELDS = List.of(
        CMRH_TELEPHONE,
        CMRH_ORAL,
        JR_FORM_FILLING,
        ADJOURNED_FEE,
        HO_INTERVIEW,
        SUBSTANTIVE_HEARING
    );

    public boolean display() {
        if (!isEmptyValue(this.getSubmitted())) {
            return true;
        }
        return !HIDDEN_FIELDS.contains(this.getKey());
    }

    private boolean isEmptyValue(Object value) {
        return switch (value) {
            case null -> true;
            case BigDecimal bigDecimal -> BigDecimal.ZERO.compareTo(bigDecimal) == 0;
            case Integer i -> i == 0;
            default -> false;
        };
    }

    public void setSubmittedForDisplay() {
        setSubmitted(getOrElseZero(submitted));
    }

    public void setCalculatedForDisplay() {
        setCalculated(getOrElseZero(calculated));
    }

    public void setAssessedForDisplay() {
        setAssessed(getOrElseZero(assessed));
    }
}
