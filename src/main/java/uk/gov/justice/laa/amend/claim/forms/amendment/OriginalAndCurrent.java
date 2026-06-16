package uk.gov.justice.laa.amend.claim.forms.amendment;

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
