package uk.gov.justice.laa.payments.amend.forms.amendments;

import lombok.Data;

@Data
public class RequestedReasonForm {
  private String requestedReason;

  public boolean isBlank() {
    return requestedReason == null || requestedReason.isBlank();
  }
}
