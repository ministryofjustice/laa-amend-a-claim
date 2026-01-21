package uk.gov.justice.laa.amend.claim.models;

public class FixedFeeClaimField extends ClaimField {

    public FixedFeeClaimField(String key, Object calculated) {
        super(key, null, calculated, null);
        this.assessable = false;
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
