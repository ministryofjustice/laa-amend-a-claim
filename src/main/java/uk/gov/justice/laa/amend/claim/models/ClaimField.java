package uk.gov.justice.laa.amend.claim.models;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.amend.claim.utils.FormUtils;

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
    private AssessStatus status;

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

    public String getChangeAllowedTotalUrl(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, "allowed-totals");
    }

    public String getChangeAssessedTotalUrl(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, "assessed-totals");
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

    public boolean needsAssessing() {
        return status == AssessStatus.NEEDS_ASSESSING;
    }
}
