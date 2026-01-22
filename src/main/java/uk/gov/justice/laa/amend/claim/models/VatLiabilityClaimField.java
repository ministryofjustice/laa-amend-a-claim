package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.VAT;

public class VatLiabilityClaimField extends ClaimField {

    @Builder
    public VatLiabilityClaimField(Object submitted, Object calculated, Object assessed) {
        super(VAT, submitted, calculated, assessed);
        this.assessable = true;
    }

    public VatLiabilityClaimField(Object submitted, Object calculated) {
        this(submitted, calculated, submitted);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case REDUCED_TO_FIXED_FEE -> setAssessedToCalculated();
            case REDUCED, PAID_IN_FULL -> setAssessedToSubmitted();
            default -> { }
        }
    }
}
