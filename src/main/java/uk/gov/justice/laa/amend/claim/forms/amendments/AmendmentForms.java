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
  private OriginalAndCurrent caseDetailsForm;

  public AmendmentForms(
      AmendmentForm client1Form, AmendmentForm caseType, AmendmentForm caseDetailsForm) {
    // Create a new copy of each form to ensure changes to one aren't propagated to the other
    var currentClient1Form = new AmendmentForm(client1Form);
    var currentCaseTypeForm = new AmendmentForm(caseType);
    var currentCaseDetailsForm = new AmendmentForm(caseDetailsForm);

    this.client1Form = new OriginalAndCurrent(client1Form, currentClient1Form);
    this.caseTypeForm = new OriginalAndCurrent(caseType, currentCaseTypeForm);
    this.caseDetailsForm = new OriginalAndCurrent(caseDetailsForm, currentCaseDetailsForm);
  }

  public boolean hasAmendments() {
    return client1Form.hasAmendments()
        || caseTypeForm.hasAmendments()
        || caseDetailsForm.hasAmendments();
  }
}
