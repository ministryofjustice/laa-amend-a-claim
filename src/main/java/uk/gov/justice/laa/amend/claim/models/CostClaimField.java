package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CostClaimField extends ClaimField {

    protected Cost cost;

    @Builder
    public CostClaimField(String key, Object submitted, Object calculated, Object assessed, Cost cost) {
        super(key, submitted, calculated, assessed);
        this.assessable = true;
        this.cost = cost;
    }

    public CostClaimField(String key, Object submitted, Object calculated, Cost cost) {
        this(key, submitted, calculated, submitted, cost);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> setNilled();
            case REDUCED_TO_FIXED_FEE -> {
                switch (cost) {
                    case PROFIT_COSTS -> setAssessedToNull();
                    default -> setAssessedToCalculated();
                }
            }
            case REDUCED -> {
                switch (cost) {
                    case PROFIT_COSTS -> setAssessedToNull();
                    default -> setAssessedToSubmitted();
                }
            }
            case PAID_IN_FULL -> setAssessedToSubmitted();
            default -> { }
        }
    }
}
