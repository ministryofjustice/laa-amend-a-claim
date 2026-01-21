package uk.gov.justice.laa.amend.claim.models;

public class CalculatedTotalClaimField extends ClaimField {

    public CalculatedTotalClaimField(String key, Object submitted, Object calculated) {
        super(key, submitted, calculated, submitted);
        this.assessable = false;
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
    }
}
