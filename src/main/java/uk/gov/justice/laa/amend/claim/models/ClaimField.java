package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.amend.claim.utils.FormUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimField implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String key;
    private Object submitted;
    private Object calculated;
    private Object amended;
    private Cost cost;
    private AmendStatus status;

    public ClaimField(String key, Object submitted, Object calculated) {
        this(key, submitted, calculated, null);
    }

    public ClaimField(String key, Object submitted, Object calculated, Object amended) {
        this(key, submitted, calculated);
        this.amended = amended;
    }

    public ClaimField(String key, Object submitted, Object calculated, Cost cost) {
        this.key = key;
        this.submitted = submitted;
        this.calculated = calculated;
        this.amended = submitted;
        this.cost = cost;
        this.status = AmendStatus.NOT_AMENDABLE;
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
        if (cost == null) {
            return null;
        }

        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost.getPath());
    }

    public String getChangeUrlAllowedTotal(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, "allowed-totals");
    }

    protected void setNilled() {
        setAmended(BigDecimal.ZERO, AmendStatus.NOT_AMENDABLE);
    }

    protected void setToNeedsAmending() {
        setAmended(null, AmendStatus.NEEDS_AMENDING);
    }

    protected void setAmendedToCalculated() {
        setAmendedToValue(this.getCalculated());
    }

    protected void setAmendedToSubmitted() {
        setAmendedToValue(this.getSubmitted());
    }

    private void setAmendedToValue(Object value) {
        setAmended(value, AmendStatus.AMENDABLE);
    }

    private void setAmended(Object value, AmendStatus status) {
        this.setAmended(value);
        this.setStatus(status);
    }

    public boolean needsAmending() {
        return status == AmendStatus.NEEDS_AMENDING;
    }
}
