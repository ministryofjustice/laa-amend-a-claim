package uk.gov.justice.laa.payments.amend.forms.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldError {
  private FieldErrorType type;
  private String message;
}
