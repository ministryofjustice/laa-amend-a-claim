package uk.gov.justice.laa.amend.claim.forms.amendments;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmendmentForms {

  @NotNull private OriginalAndCurrent client1Form;
  @NotNull private OriginalAndCurrent caseTypeForm;
  @NotNull private OriginalAndCurrent caseDetailsForm;
  private OriginalAndCurrent client2Form;
  @NotNull private OriginalAndCurrent costsForm;
  @NotNull private RequestedByForm requestedByForm;
  @NotNull private RequestedReasonForm requestedReasonForm;

  @Builder
  private AmendmentForms(
      AmendmentForm client1,
      AmendmentForm client2,
      AmendmentForm caseType,
      AmendmentForm caseDetails,
      AmendmentForm costs,
      RequestedByForm requestedBy,
      RequestedReasonForm requestedReason) {
    this.client1Form = originalAndCurrent(client1);
    this.caseTypeForm = originalAndCurrent(caseType);
    this.caseDetailsForm = originalAndCurrent(caseDetails);
    this.costsForm = originalAndCurrent(costs == null ? new AmendmentForm() : costs);
    this.requestedByForm = requestedBy == null ? new RequestedByForm() : requestedBy;
    this.requestedReasonForm =
        requestedReason == null ? new RequestedReasonForm() : requestedReason;

    if (client2 != null) {
      this.client2Form = originalAndCurrent(client2);
    }
  }

  private static OriginalAndCurrent originalAndCurrent(AmendmentForm original) {
    return new OriginalAndCurrent(original, new AmendmentForm(original));
  }

  public boolean isClientPageAmended() {
    return hasAmendments(client1Form) || hasAmendments(client2Form);
  }

  public boolean isCasePageAmended() {
    return hasAmendments(caseTypeForm) || hasAmendments(caseDetailsForm);
  }

  public boolean hasAmendments() {
    return client1Form.hasAmendments()
        || caseTypeForm.hasAmendments()
        || caseDetailsForm.hasAmendments()
        || (client2Form != null && client2Form.hasAmendments())
        || costsForm.hasAmendments();
  }

  private static boolean hasAmendments(OriginalAndCurrent form) {
    return form != null && form.hasAmendments();
  }
}
