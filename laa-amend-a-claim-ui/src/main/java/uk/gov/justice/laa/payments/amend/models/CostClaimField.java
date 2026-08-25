package uk.gov.justice.laa.payments.amend.models;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.payments.amend.models.enums.Cost;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimFieldRow;

@Getter
@NoArgsConstructor
public class CostClaimField extends ClaimField {

  protected Cost cost;

  @Builder
  public CostClaimField(
      String key, Object submitted, Object calculated, Object assessed, Cost cost) {
    super(key, submitted, calculated, assessed);
    this.cost = Objects.requireNonNull(cost, "Cost must not be null for CostClaimField");
  }

  public CostClaimField(String key, Object submitted, Object calculated, Cost cost) {
    this(key, submitted, calculated, submitted, cost);
  }

  @Override
  public void applyOutcome(OutcomeType outcome) {
    switch (outcome) {
      case NILLED -> setNilled();
      case REDUCED_TO_FIXED_FEE -> {
        if (cost == Cost.PROFIT_COSTS) {
          setAssessedToNull();
        } else {
          setAssessedToCalculated();
        }
      }
      case REDUCED -> {
        if (cost == Cost.PROFIT_COSTS) {
          setAssessedToNull();
        } else {
          setAssessedToSubmitted();
        }
      }
      case PAID_IN_FULL -> setAssessedToSubmitted();
      default -> {}
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
