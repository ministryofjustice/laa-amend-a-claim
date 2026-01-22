package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;

public class FixedFeeClaimField extends ClaimField {

    @Builder
    public FixedFeeClaimField(Object submitted, Object calculated, Object assessed) {
        super(FIXED_FEE, submitted, calculated, assessed);
        this.assessable = false;
    }

    public FixedFeeClaimField(Object calculated) {
        this(null, calculated, null);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> setNilled();
            case REDUCED_TO_FIXED_FEE -> setAssessedToCalculated();
            case REDUCED, PAID_IN_FULL -> setAssessedToNull();
        }
    }
}
