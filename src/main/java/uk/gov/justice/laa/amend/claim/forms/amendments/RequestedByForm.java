package uk.gov.justice.laa.amend.claim.forms.amendments;

import lombok.Data;

@Data
public class RequestedByForm {
  private String requestedBy;

  public boolean isBlank() {
    return requestedBy == null || requestedBy.isBlank();
  }
}
