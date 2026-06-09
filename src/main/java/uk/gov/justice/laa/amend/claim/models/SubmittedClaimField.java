package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

@NoArgsConstructor
public class SubmittedClaimField extends ClaimField {

    @Builder
    public SubmittedClaimField(String key, Object submitted) {
        super(key, submitted, null, null);
    }

    @Override
    public void setAssessableToDefault() {
        this.assessable = false;
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        throw new UnsupportedOperationException("SubmittedClaimField does not support outcome application");
    }

    @Override
    public ClaimFieldRow toClaimFieldRow() {
        return ClaimFieldRow.from(this);
    }
}
