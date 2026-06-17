package uk.gov.justice.laa.amend.claim.forms.amendments;

import lombok.Data;

@Data
public class AmendmentForms {
  private OriginalAndCurrent client1Form;

  public AmendmentForms(AmendmentForm client1Form) {
    this.client1Form = new OriginalAndCurrent(client1Form, client1Form);
  }

  public boolean hasAmendments() {
    return client1Form.hasAmendments();
  }
}
