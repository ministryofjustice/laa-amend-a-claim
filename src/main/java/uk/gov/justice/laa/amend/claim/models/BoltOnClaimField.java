package uk.gov.justice.laa.amend.claim.models;

public class BoltOnClaimField extends ClaimField {

    public BoltOnClaimField(String key, Object submitted, Object calculated) {
        super(key, submitted, calculated, submitted);
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
