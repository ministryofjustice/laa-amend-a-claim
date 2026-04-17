package uk.gov.justice.laa.amend.claim.models;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

@Data
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class ClaimField {

  protected String key;
  protected Object submitted;
  protected Object calculated;
  protected Object assessed;
  protected boolean assessable;

  public ClaimField(String key, Object submitted, Object calculated, Object assessed) {
    this.key = key;
    this.submitted = submitted;
    this.calculated = calculated;
    this.assessed = assessed;
    setAssessableToDefault();
  }

  public boolean hasSubmittedValue() {
    return !hasNoSubmittedValue();
  }

  private boolean hasNoSubmittedValue() {
    return switch (this.getSubmitted()) {
      case null -> true;
      case BigDecimal bigDecimal -> BigDecimal.ZERO.compareTo(bigDecimal) == 0;
      case Integer i -> i == 0;
      case Boolean b -> !b;
      default -> false;
    };
  }

  public abstract void setAssessableToDefault();

  public boolean isNotAssessable() {
    return !isAssessable();
  }

  public abstract ClaimFieldRow toClaimFieldRow();

  public abstract void applyOutcome(OutcomeType outcome);

  protected void setNilled() {
    setAssessed(BigDecimal.ZERO);
  }

  protected void setAssessedToNull() {
    setAssessed(null);
  }

  protected void setAssessedToCalculated() {
    setAssessed(this.getCalculated());
  }

  protected void setAssessedToSubmitted() {
    setAssessed(this.getSubmitted());
  }
}
