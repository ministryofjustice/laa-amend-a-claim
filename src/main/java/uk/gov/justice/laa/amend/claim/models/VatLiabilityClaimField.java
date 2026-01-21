package uk.gov.justice.laa.amend.claim.models;

public class VatLiabilityClaimField extends ClaimField {

    public VatLiabilityClaimField(String key, Object submitted, Object calculated) {
        super(key, submitted, calculated, submitted);
        this.assessable = true;
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> {}
            case REDUCED_TO_FIXED_FEE -> setAssessedToCalculated();
            case REDUCED, PAID_IN_FULL -> setAssessedToSubmitted();
        }
    }
}
