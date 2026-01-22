package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;

public class AssessedClaimField extends ClaimField {

    @Builder
    public AssessedClaimField(String key, Object submitted, Object calculated, Object assessed) {
        super(key, submitted, calculated, assessed);
        this.assessable = true;
    }

    public AssessedClaimField(String key) {
        this(key, null, null, null);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        setAssessedToNull();
    }
}
