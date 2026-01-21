package uk.gov.justice.laa.amend.claim.models;

public class AllowedClaimField extends ClaimField {

    public AllowedClaimField(String key, Object calculated) {
        super(key, null, calculated, null);
        this.assessable = true;
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> setNilled();
            case REDUCED, PAID_IN_FULL, REDUCED_TO_FIXED_FEE -> setAssessedToNull();
        }
    }
}
