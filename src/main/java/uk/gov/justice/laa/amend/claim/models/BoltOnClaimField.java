package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;

public class BoltOnClaimField extends ClaimField {

    @Builder
    public BoltOnClaimField(String key, Object submitted, Object calculated, Object assessed) {
        super(key, submitted, calculated, assessed);
        this.assessable = false;
    }

    public BoltOnClaimField(String key, Object submitted, Object calculated) {
        this(key, submitted, calculated, submitted);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> setNilled();
            case REDUCED_TO_FIXED_FEE -> setAssessedToCalculated();
            case REDUCED, PAID_IN_FULL -> setAssessedToNull();
            default -> { }
        }
    }
}
