package uk.gov.justice.laa.payments.amend.models;

import lombok.Builder;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimFieldRow;

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
  public void applyOutcome(OutcomeType outcome) {}

  @Override
  public ClaimFieldRow toClaimFieldRow() {
    return ClaimFieldRow.from(this);
  }
}
