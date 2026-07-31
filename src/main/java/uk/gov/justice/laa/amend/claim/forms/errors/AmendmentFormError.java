package uk.gov.justice.laa.amend.claim.forms.errors;

import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.validation.FieldError;

public class AmendmentFormError extends FormError {

  private static final Pattern INPUTS_FIELD_PATTERN = Pattern.compile("^inputs\\[(.+)]$");

  public AmendmentFormError() {}

  public AmendmentFormError(FieldError fieldError) {
    this(fieldError.getField(), fieldError.getDefaultMessage());
  }

  public AmendmentFormError(String fieldName, String message) {
    super(extractFieldName(fieldName), message);
  }

  @Override
  public String getFieldId() {
    return getFieldName();
  }

  private static String extractFieldName(String fieldName) {
    var matcher = INPUTS_FIELD_PATTERN.matcher(fieldName);
    return matcher.matches() ? matcher.group(1) : fieldName;
  }

  @Override
  protected Map<String, Integer> getFieldOrderMap() {
    return Map.of();
  }
}
