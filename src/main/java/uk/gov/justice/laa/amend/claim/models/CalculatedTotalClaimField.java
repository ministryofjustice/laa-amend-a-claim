package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;

public class CalculatedTotalClaimField extends ClaimField {

    @Builder
    public CalculatedTotalClaimField(Object submitted, Object calculated, Object assessed) {
        super(TOTAL, submitted, calculated, assessed);
        this.assessable = false;
    }

    public CalculatedTotalClaimField(Object submitted, Object calculated) {
        this(submitted, calculated, submitted);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) { }

    @Override
    public ClaimFieldRow toClaimFieldRow(ClaimDetails claim) {
        return ClaimFieldRow.from(this);
    }
}
