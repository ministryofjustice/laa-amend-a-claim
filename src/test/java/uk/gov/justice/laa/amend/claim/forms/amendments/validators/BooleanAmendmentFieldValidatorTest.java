package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

class BooleanAmendmentFieldValidatorTest {

  private final BooleanAmendmentFieldValidator validator =
      new BooleanAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsWellFormedBooleanValue() {
    var errors = validate(CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR, "yes");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedBooleanValueNamingTheField() {
    var errors = validate(CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_DUTY_SOLICITOR]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Duty solicitor");
  }

  @Test
  void rejectsMalformedBooleanValueNamingDifferentField() {
    var errors = validate(CrimeClaimDetailsViewField.IS_YOUTH_COURT, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_YOUTH_COURT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Youth court");
  }

  @Test
  void acceptsBlankBooleanValue() {
    var errors = validate(CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR, "");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsWellFormedCivilIsEligibleClientValue() {
    var errors = validate(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, "yes");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedCivilIsEligibleClientValueNamingTheField() {
    var errors = validate(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_ELIGIBLE_CLIENT]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Eligible client");
  }

  @Test
  void acceptsWellFormedCivilIsPostalApplicationAcceptedValue() {
    var errors = validate(CivilClaimDetailsViewField.IS_POSTAL_APPLICATION_ACCEPTED, "no");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedCivilIsPostalApplicationAcceptedValueNamingTheField() {
    var errors = validate(CivilClaimDetailsViewField.IS_POSTAL_APPLICATION_ACCEPTED, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_POSTAL_APPLICATION_ACCEPTED]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Postal application accepted");
  }

  @Test
  void acceptsWellFormedMediationIsLegallyAidedValue() {
    var errors = validate(MediationClaimDetailsViewField.IS_LEGALLY_AIDED, "yes");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedMediationIsLegallyAidedValueNamingTheField() {
    var errors = validate(MediationClaimDetailsViewField.IS_LEGALLY_AIDED, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_LEGALLY_AIDED]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Legally aided");
  }

  @Test
  void acceptsWellFormedMediationIsPostalApplicationAcceptedValue() {
    var errors = validate(MediationClaimDetailsViewField.IS_POSTAL_APPLICATION_ACCEPTED, "no");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedMediationIsPostalApplicationAcceptedValueNamingTheField() {
    var errors = validate(MediationClaimDetailsViewField.IS_POSTAL_APPLICATION_ACCEPTED, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_POSTAL_APPLICATION_ACCEPTED]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Postal application accepted");
  }

  @Test
  void acceptsWellFormedClient2IsLegallyAidedValue() {
    var errors = validate(MediationClaimDetailsViewField.IS_CLIENT_2_LEGALLY_AIDED, "yes");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedClient2IsLegallyAidedValueNamingTheField() {
    var errors = validate(MediationClaimDetailsViewField.IS_CLIENT_2_LEGALLY_AIDED, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_CLIENT_2_LEGALLY_AIDED]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Legally aided");
  }

  @Test
  void acceptsWellFormedClient2IsPostalApplicationAcceptedValue() {
    var errors =
        validate(MediationClaimDetailsViewField.IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED, "no");

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsMalformedClient2IsPostalApplicationAcceptedValueNamingTheField() {
    var errors =
        validate(MediationClaimDetailsViewField.IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED, "banana");

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.boolean.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Postal application accepted");
  }

  private Errors validate(ClaimViewField<?> field, String value) {
    var form = new AmendmentForm();
    form.setInputs(Map.of(field.name(), value));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(field, form, errors);
    return errors;
  }
}
