package uk.gov.justice.laa.amend.claim.forms.errors;

import java.util.Map;
import org.thymeleaf.spring6.util.DetailedError;

public class InquestFormError extends FormError {

  public InquestFormError(DetailedError error) {
    super(error);
  }

  public InquestFormError(String fieldName, String message) {
    super(fieldName, message);
  }

  @Override
  protected Map<String, Integer> getFieldOrderMap() {
    return Map.of(
        "deceasedForename", 1,
        "deceasedSurname", 2,
        "deceasedDateOfBirth", 3,
        "deceasedDateOfDeath", 4,
        "coronersInquestReference", 5,
        "interestedDepartmentCodes", 6);
  }
}
