package uk.gov.justice.laa.payments.amend.forms.validators;

import jakarta.validation.ConstraintValidatorContext;

public class Validator {

  protected void addViolation(
      ConstraintValidatorContext context, String fieldName, String message) {
    context
        .buildConstraintViolationWithTemplate(message)
        .addPropertyNode(fieldName)
        .addConstraintViolation();
  }
}
