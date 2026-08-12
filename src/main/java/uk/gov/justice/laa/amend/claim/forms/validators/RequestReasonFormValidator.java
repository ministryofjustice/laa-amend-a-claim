package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentReasonReference;

@AllArgsConstructor
@Component
public class RequestReasonFormValidator implements Validator {

  private final SystemReferenceService systemReferenceService;

  @Override
  public boolean supports(Class<?> clazz) {
    return RequestedReasonForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    var form = (RequestedReasonForm) target;

    var value = form.getRequestedReason();
    if (isBlank(value)) {
      errors.rejectValue("requestedReason", "amendments.requestReason.required");
      return;
    }

    if (!getAmendmentRequestReason(form.getRequestedBy()).containsKey(form.getRequestedReason())) {
      errors.rejectValue("requestedReason", "amendments.requestReason.invalid");
    }
  }

  public Map<String, String> getAmendmentRequestReason(String requestBy) {
    var amendmentReasons = systemReferenceService.getAmendmentReasonByProvider(requestBy);

    Map<String, String> codeToLabelMap = new LinkedHashMap<>();
    if (amendmentReasons != null && !amendmentReasons.isEmpty()) {
      codeToLabelMap =
          amendmentReasons.stream()
              .filter(
                  item -> item != null && item.getCode() != null && item.getDisplayLabel() != null)
              .collect(
                  Collectors.toMap(
                      AmendmentReasonReference::getCode,
                      AmendmentReasonReference::getDisplayLabel,
                      (existing, replacement) -> existing,
                      LinkedHashMap::new));
    }
    return codeToLabelMap;
  }
}
