package uk.gov.justice.laa.amend.claim.forms.amendments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AmendmentForms {
  private OriginalAndCurrent client1Form;

  public AmendmentForms(AmendmentForm client1Form) {
    // Create a new copy of each form to ensure changes to one aren't propagated to the other
    var currentClient1Form = new AmendmentForm(client1Form);

    this.client1Form = new OriginalAndCurrent(client1Form, currentClient1Form);
  }

  public boolean hasAmendments() {
    return client1Form.hasAmendments();
  }
}
