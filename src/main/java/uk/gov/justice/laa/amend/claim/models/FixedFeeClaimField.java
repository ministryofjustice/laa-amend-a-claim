package uk.gov.justice.laa.amend.claim.models;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;

import lombok.Builder;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

public class FixedFeeClaimField extends ClaimField {

    @Builder
    public FixedFeeClaimField(Object calculated, Object assessed) {
        super(FIXED_FEE, null, calculated, assessed);
    }

    public FixedFeeClaimField(Object calculated) {
        this(calculated, null);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> setNilled();
            case REDUCED_TO_FIXED_FEE -> setAssessedToCalculated();
            case REDUCED, PAID_IN_FULL -> setAssessedToNull();
            default -> {}
        }
    }

    @Override
    public void setAssessableToDefault() {
        this.assessable = false;
    }

    @Override
    public ClaimFieldRow toClaimFieldRow() {
        return ClaimFieldRow.from(this);
    }
}
