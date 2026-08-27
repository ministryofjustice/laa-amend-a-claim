package uk.gov.justice.laa.payments.amend.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.payments.amend.service.SystemReferenceService;

@AllArgsConstructor
@Component
public class RequestedReasonFormValidator {

  private final SystemReferenceService systemReferenceService;

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
