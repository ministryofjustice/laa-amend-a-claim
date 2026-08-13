package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReference;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;

class RequestByFormValidatorTest {

  private RequestByFormValidator validator;
  private SystemReferenceService systemReferenceService;

  @BeforeEach
  void setUp() {
    systemReferenceService = mock(SystemReferenceService.class);
    validator = new RequestByFormValidator(systemReferenceService);
  }

  @Test
  void supportsRequestedByFormType() {
    assertThat(validator.supports(RequestedByForm.class)).isTrue();
    assertThat(validator.supports(Object.class)).isFalse();
  }

  @Test
  void rejectsNullRequestedBy() {
    var form = new RequestedByForm();
    form.setRequestedBy(null);

    var errors = validate(form);

    assertThat(errors.hasErrors()).isTrue();
    assertThat(Objects.requireNonNull(errors.getFieldError("requestedBy")).getCode())
        .isEqualTo("amendments.requestBy.required");
  }

  @Test
  void rejectsBlankRequestedBy() {
    var form = new RequestedByForm();
    form.setRequestedBy("");

    var errors = validate(form);

    assertThat(errors.hasErrors()).isTrue();
    assertThat(Objects.requireNonNull(errors.getFieldError("requestedBy")).getCode())
        .isEqualTo("amendments.requestBy.required");
  }

  @Test
  void rejectsWhitespaceOnlyRequestedBy() {
    var form = new RequestedByForm();
    form.setRequestedBy("   ");

    var errors = validate(form);

    assertThat(errors.hasErrors()).isTrue();
    assertThat(Objects.requireNonNull(errors.getFieldError("requestedBy")).getCode())
        .isEqualTo("amendments.requestBy.required");
  }

  @Test
  void rejectsInvalidRequestedByCode() {
    setupReferenceList("PROVIDER", "LAA");

    var form = new RequestedByForm();
    form.setRequestedBy("INVALID_CODE");

    var errors = validate(form);

    assertThat(errors.hasErrors()).isTrue();
    assertThat(Objects.requireNonNull(errors.getFieldError("requestedBy")).getCode())
        .isEqualTo("amendments.requestBy.invalid");
  }

  @Test
  void acceptsValidRequestedByCode() {
    setupReferenceList("PROVIDER", "LAA");

    var form = new RequestedByForm();
    form.setRequestedBy("PROVIDER");

    var errors = validate(form);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsAnyValidCodeFromReferenceList() {
    setupReferenceList("PROVIDER", "LAA", "COURT");

    var form = new RequestedByForm();
    form.setRequestedBy("LAA");

    var errors = validate(form);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsValidRequestedByCodeWhenReferenceListContainsNullCode() {
    setupReferenceList(null, "PROVIDER");

    var form = new RequestedByForm();
    form.setRequestedBy("PROVIDER");

    var errors = validate(form);

    assertThat(errors.hasErrors()).isFalse();
  }

  private void setupReferenceList(String... codes) {
    var referenceList = new AmendmentRequestedByReferenceList();
    var references =
        java.util.Arrays.stream(codes)
            .map(
                code -> {
                  var ref = new AmendmentRequestedByReference();
                  ref.setCode(code);
                  return ref;
                })
            .toList();
    referenceList.setRequestedBy(references);
    when(systemReferenceService.getAmendmentRequestedByReferenceList()).thenReturn(referenceList);
  }

  private Errors validate(RequestedByForm form) {
    var errors = new BeanPropertyBindingResult(form, "requestedByForm");
    validator.validate(form, errors);
    return errors;
  }
}
