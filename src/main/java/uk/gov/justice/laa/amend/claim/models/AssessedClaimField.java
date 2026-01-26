package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

@Getter
public class AssessedClaimField extends ClaimField {

    protected final TotalType type;

    @Builder
    public AssessedClaimField(String key, Object submitted, Object calculated, Object assessed, TotalType type) {
        super(key, submitted, calculated, assessed);
        this.assessable = true;
        this.type = type;
    }

    public AssessedClaimField(String key, TotalType type) {
        this(key, null, null, null, type);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        setAssessedToNull();
    }

    @Override
    public ClaimFieldRow toClaimFieldRow(ClaimDetails claim) {
        return ClaimFieldRow.from(this, claim);
    }
}
