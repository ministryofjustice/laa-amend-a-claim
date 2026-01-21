package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.amend.claim.utils.FormUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ClaimField implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String key;
    private Object submitted;
    private Object calculated;
    private Object assessed;
    private String changeUrl;
    private ClaimFieldStatus status;
    private final ClaimFieldType type;

    public ClaimField(String key, Object submitted, Object calculated, ClaimFieldType type) {
        this(key, submitted, calculated, (String) null, type);
    }

    public ClaimField(String key, Object submitted, Object calculated, Object assessed, ClaimFieldType type) {
        this(key, submitted, calculated, type);
        this.assessed = assessed;
    }

    public ClaimField(String key, Object submitted, Object calculated, Cost cost, ClaimFieldType type) {
        this(key, submitted, calculated, cost.getChangeUrl(), type);
    }

    public ClaimField(String key, Object submitted, Object calculated, String changeUrl, ClaimFieldType type) {
        this.key = key;
        this.submitted = submitted;
        this.calculated = calculated;
        this.assessed = submitted;
        this.changeUrl = changeUrl;
        this.type = type;
    }

    public ClaimField(String key, Object submitted, Object calculated, Object amended, Object assessed, ClaimFieldType type) {
        this(key, submitted, calculated, amended, type);
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

    protected void setAssessedToNull() {
        setAssessed(null);
    }

    protected void setAssessedToCalculated() {
        setAssessedToValue(this.getCalculated());
    }

    protected void setAssessedToSubmitted() {
        setAssessedToValue(this.getSubmitted());
    }

    public void setAssessedToValue(Object value) {
        setAssessed(value);
    }

    public boolean isAssessableAndUnassessed() {
        return isAssessable() && !isAssessed();
    }

    public boolean isAssessableAndAssessed() {
        return isAssessable() && isAssessed();
    }

    public boolean isAssessable() {
        return status == ClaimFieldStatus.MODIFIABLE;
    }

    public boolean isNotAssessable() {
        return !isAssessable();
    }

    public boolean isAssessed() {
        return assessed != null;
    }

    private boolean hasNoSubmittedValue() {
        return switch (this.getSubmitted()) {
            case null -> true;
            case BigDecimal bigDecimal -> BigDecimal.ZERO.compareTo(bigDecimal) == 0;
            case Integer i -> i == 0;
            default -> false;
        };
    }

    public boolean hasSubmittedValue() {
        return !hasNoSubmittedValue();
    }
}
