package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;

@AllArgsConstructor
@Component
public class RequestByFormValidator implements Validator {

  private final SystemReferenceService systemReferenceService;

  @Override
  public boolean supports(Class<?> clazz) {
    return RequestedByForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    var form = (RequestedByForm) target;

    var value = form.getRequestedBy();
    if (isBlank(value)) {
      errors.rejectValue("requestedBy", "amendments.requestBy.required");
      return;
    }
    if (systemReferenceService.getAmendmentRequestedByReferenceList().getRequestedBy().stream()
        .noneMatch(item -> value.equals(item.getCode()))) {
      errors.rejectValue("requestedBy", "amendments.requestBy.invalid");
    }
  }
}
