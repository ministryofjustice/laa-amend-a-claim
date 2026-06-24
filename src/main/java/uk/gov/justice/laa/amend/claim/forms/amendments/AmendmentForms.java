package uk.gov.justice.laa.amend.claim.forms.amendments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AmendmentForms {

  private OriginalAndCurrent client1Form;
  private OriginalAndCurrent caseTypeForm;

  public AmendmentForms(AmendmentForm client1Form, AmendmentForm caseType) {
    // Create a new copy of each form to ensure changes to one aren't propagated to the other
    var currentClient1Form = new AmendmentForm(client1Form);
    var currentCaseTypeForm = new AmendmentForm(caseType);

    this.client1Form = new OriginalAndCurrent(client1Form, currentClient1Form);
    this.caseTypeForm = new OriginalAndCurrent(caseType, currentCaseTypeForm);
  }

  public boolean hasAmendments() {
    return client1Form.hasAmendments() || caseTypeForm.hasAmendments();
  }
}
