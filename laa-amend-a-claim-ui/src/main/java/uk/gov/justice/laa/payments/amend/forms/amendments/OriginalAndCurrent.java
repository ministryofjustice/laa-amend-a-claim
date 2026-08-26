package uk.gov.justice.laa.payments.amend.forms.amendments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OriginalAndCurrent {
  private AmendmentForm original;
  private AmendmentForm current;

  public boolean hasAmendments() {
    return current.hasAmendments(original);
  }
}
