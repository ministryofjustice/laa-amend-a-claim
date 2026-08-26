package uk.gov.justice.laa.payments.amend.models;

import static uk.gov.justice.laa.payments.amend.constants.AmendClaimConstants.Label.TOTAL;

import lombok.Builder;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimFieldRow;

@NoArgsConstructor
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
