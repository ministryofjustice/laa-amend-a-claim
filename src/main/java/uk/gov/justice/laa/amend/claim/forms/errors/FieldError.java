package uk.gov.justice.laa.amend.claim.forms.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldError {
    private FieldErrorType type;
    private String message;
}
