package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;

@AllArgsConstructor
@Component
public class RequestedReasonFormValidator implements Validator {

  private final SystemReferenceService systemReferenceService;

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return RequestedReasonForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    throw new UnsupportedOperationException("Not supported as request reason must be supplied.");
  }

  public void validate(
      @NonNull Object target, @NonNull Errors errors, @NonNull String requestedBy) {
    var form = (RequestedReasonForm) target;

    var value = form.getRequestedReason();
    if (isBlank(value)) {
      errors.rejectValue("requestedReason", "amendments.requestReason.required");
      return;
    }

    if (!systemReferenceService
        .getAmendmentRequestReason(requestedBy)
        .containsKey(form.getRequestedReason())) {
      errors.rejectValue("requestedReason", "amendments.requestReason.invalid");
    }
  }
}
