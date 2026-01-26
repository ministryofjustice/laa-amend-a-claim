package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

import java.util.Objects;

@Getter
public class CostClaimField extends ClaimField {

    protected final Cost cost;

    @Builder
    public CostClaimField(String key, Object submitted, Object calculated, Object assessed, Cost cost) {
        super(key, submitted, calculated, assessed);
        this.assessable = true;
        this.cost = Objects.requireNonNull(cost, "Cost must not be null for CostClaimField");
    }

    public CostClaimField(String key, Object submitted, Object calculated, Cost cost) {
        this(key, submitted, calculated, submitted, cost);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> setNilled();
            case REDUCED_TO_FIXED_FEE -> {
                if (cost == Cost.PROFIT_COSTS) {
                    setAssessedToNull();
                } else {
                    setAssessedToCalculated();
                }
            }
            case REDUCED -> {
                if (cost == Cost.PROFIT_COSTS) {
                    setAssessedToNull();
                } else {
                    setAssessedToSubmitted();
                }
            }
            case PAID_IN_FULL -> setAssessedToSubmitted();
            default -> { }
        }
    }

    @Override
    public ClaimFieldRow toClaimFieldRow(ClaimDetails claim) {
        return ClaimFieldRow.from(this);
    }
}
