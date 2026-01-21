package uk.gov.justice.laa.amend.claim.models;

public class AssessedClaimField extends ClaimField {

    public AssessedClaimField(String key) {
        super(key, null, null, null);
        this.assessable = true;
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        setAssessedToNull();
    }
}
