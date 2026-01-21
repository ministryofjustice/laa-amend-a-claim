package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public abstract class ClaimField implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected String key;
    protected Object submitted;
    protected Object calculated;
    protected Object assessed;
    protected boolean assessable;

    public ClaimField(String key, Object submitted, Object calculated, Object assessed) {
        this.key = key;
        this.submitted = submitted;
        this.calculated = calculated;
        this.assessed = assessed;
    }

    public boolean hasSubmittedValue() {
        return !hasNoSubmittedValue();
    }

    private boolean hasNoSubmittedValue() {
        return switch (this.getSubmitted()) {
            case null -> true;
            case BigDecimal bigDecimal -> BigDecimal.ZERO.compareTo(bigDecimal) == 0;
            case Integer i -> i == 0;
            default -> false;
        };
    }

    public boolean isNotAssessable() {
        return !isAssessable();
    }

    public ClaimFieldRow toClaimFieldRow() {
        return switch (this) {
            case BoltOnClaimField x -> hasSubmittedValue() ? new ClaimFieldRow(x) : null;
            default -> new ClaimFieldRow(this);
        };
    }

    public abstract void applyOutcome(OutcomeType outcome);

    protected void setNilled() {
        setAssessed(BigDecimal.ZERO);
    }

    protected void setAssessedToNull() {
        setAssessed(null);
    }

    protected void setAssessedToCalculated() {
        setAssessed(this.getCalculated());
    }

    protected void setAssessedToSubmitted() {
        setAssessed(this.getSubmitted());
    }
}
