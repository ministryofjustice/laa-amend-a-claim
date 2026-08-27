package uk.gov.justice.laa.payments.amend.forms.errors;

import java.util.Map;

public class ReviewAndAmendFormError extends FormError {

  public ReviewAndAmendFormError(String id, String message) {
    super(id, message);
  }

  @Override
  protected Map<String, Integer> getFieldOrderMap() {
    return Map.of();
  }
}
