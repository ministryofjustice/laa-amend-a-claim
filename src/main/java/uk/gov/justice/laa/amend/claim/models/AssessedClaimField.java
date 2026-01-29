package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

@Getter
public class AssessedClaimField extends ClaimField {

    @Builder
    public AssessedClaimField(String key, Object submitted, Object calculated, Object assessed) {
        super(key, submitted, calculated, assessed);
    }

    public AssessedClaimField(String key) {
        this(key, null, null, null);
    }

    @Override
    public void applyOutcome(OutcomeType outcome) {
        switch (outcome) {
            case NILLED -> setNilled();
            case REDUCED, PAID_IN_FULL, REDUCED_TO_FIXED_FEE -> setAssessedToNull();
            default -> { }
        }
    }

    @Override
    public void setAssessableToDefault() {
        this.assessable = true;
    }

    @Override
    public ClaimFieldRow toClaimFieldRow() {
        return ClaimFieldRow.from(this);
    }
}
