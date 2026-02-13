package uk.gov.justice.laa.amend.claim.models;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;

import lombok.Builder;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

public class CalculatedTotalClaimField extends ClaimField {

    @Builder
    public CalculatedTotalClaimField(Object calculated, Object assessed) {
        super(TOTAL, null, calculated, assessed);
    }

    public CalculatedTotalClaimField(Object calculated) {
        this(calculated, null);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {}

    @Override
    public void setAssessableToDefault() {
        this.assessable = false;
    }

    @Override
    public ClaimFieldRow toClaimFieldRow() {
        return ClaimFieldRow.from(this);
    }
}
