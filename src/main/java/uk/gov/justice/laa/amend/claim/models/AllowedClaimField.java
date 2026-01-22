package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;

public class AllowedClaimField extends ClaimField {

    @Builder
    public AllowedClaimField(String key, Object submitted, Object calculated, Object assessed) {
        super(key, submitted, calculated, assessed);
        this.assessable = true;
    }

    public AllowedClaimField(String key, Object calculated) {
        this(key, null, calculated, null);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> setNilled();
            case REDUCED, PAID_IN_FULL, REDUCED_TO_FIXED_FEE -> setAssessedToNull();
        }
    }
}
