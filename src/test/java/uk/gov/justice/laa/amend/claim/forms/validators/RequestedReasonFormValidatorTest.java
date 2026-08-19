package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentReasonReference;

class RequestedReasonFormValidatorTest {

  private RequestedReasonFormValidator validator;
  private SystemReferenceService systemReferenceService;

  @BeforeEach
  void setUp() {
    systemReferenceService = mock(SystemReferenceService.class);
    validator = new RequestedReasonFormValidator(systemReferenceService);
  }

  @Test
  void supportsRequestedReasonFormType() {
    assertThat(validator.supports(RequestedReasonForm.class)).isTrue();
    assertThat(validator.supports(Object.class)).isFalse();
  }

  @Test
  void rejectsNullRequestedReason() {
    var form = new RequestedReasonForm();
    form.setRequestedReason(null);

    var errors = validate(form, null);

    assertThat(errors.hasErrors()).isTrue();
    assertThat(Objects.requireNonNull(errors.getFieldError("requestedReason")).getCode())
        .isEqualTo("amendments.requestReason.required");
  }

  @Test
  void rejectsBlankRequestedReason() {
    var form = new RequestedReasonForm();
    form.setRequestedReason("");

    var errors = validate(form, "");

    assertThat(errors.hasErrors()).isTrue();
    assertThat(Objects.requireNonNull(errors.getFieldError("requestedReason")).getCode())
        .isEqualTo("amendments.requestReason.required");
  }

  @Test
  void rejectsInvalidRequestedReasonCode() {
    setupAmendmentReasons(
        "PROVIDER", createReason("REASON1", "Reason 1"), createReason("REASON2", "Reason 2"));

    var form = new RequestedReasonForm();
    form.setRequestedReason("INVALID_CODE");

    var errors = validate(form, "PROVIDER");

    assertThat(errors.hasErrors()).isTrue();
    assertThat(Objects.requireNonNull(errors.getFieldError("requestedReason")).getCode())
        .isEqualTo("amendments.requestReason.invalid");
  }

  @Test
  void acceptsAnyValidCodeFromReasonList() {
    setupAmendmentReasons(
        "PROVIDER",
        createReason("REASON1", "Reason 1"),
        createReason("REASON2", "Reason 2"),
        createReason("REASON3", "Reason 3"));

    var form = new RequestedReasonForm();
    form.setRequestedReason("REASON2");

    var errors = validate(form, "PROVIDER");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsValidReasonCodeForLaaRequestedBy() {
    setupAmendmentReasons(
        "LAA",
        createReason("LAA_REASON1", "LAA Reason 1"),
        createReason("LAA_REASON2", "LAA Reason 2"));

    var form = new RequestedReasonForm();
    form.setRequestedReason("LAA_REASON1");

    var errors = validate(form, "LAA");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsReasonCodeNotValidForRequestedBy() {
    setupAmendmentReasons("PROVIDER", createReason("PROVIDER_REASON", "Provider Reason"));

    var form = new RequestedReasonForm();
    form.setRequestedReason("LAA_REASON");

    var errors = validate(form, "PROVIDER");

    assertThat(errors.hasErrors()).isTrue();
    assertThat(Objects.requireNonNull(errors.getFieldError("requestedReason")).getCode())
        .isEqualTo("amendments.requestReason.invalid");
  }

  private void setupAmendmentReasons(String requestedBy, AmendmentReasonReference... reasons) {
    when(systemReferenceService.getAmendmentRequestReason(requestedBy))
        .thenReturn(
            java.util.Arrays.stream(reasons)
                .collect(
                    java.util.stream.Collectors.toMap(
                        AmendmentReasonReference::getCode,
                        AmendmentReasonReference::getDisplayLabel,
                        (existing, replacement) -> existing,
                        java.util.LinkedHashMap::new)));
  }

  private AmendmentReasonReference createReason(String code, String displayLabel) {
    var reason = new AmendmentReasonReference();
    reason.setCode(code);
    reason.setDisplayLabel(displayLabel);
    return reason;
  }

  private Errors validate(RequestedReasonForm form, String requestedBy) {
    var errors = new BeanPropertyBindingResult(form, "requestedReasonForm");
    validator.validate(form, errors, requestedBy);
    return errors;
  }
}
