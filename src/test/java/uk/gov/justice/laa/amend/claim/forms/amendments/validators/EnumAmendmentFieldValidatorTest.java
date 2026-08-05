package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

class EnumAmendmentFieldValidatorTest {

  private final EnumAmendmentFieldValidator validator =
      new EnumAmendmentFieldValidator(TestMessageSources.real());

  @Test
  void acceptsEnumValueMatchingAnAllowedOption() {
    var errors = validate(ClaimDetailsViewField.GENDER, Map.of("GENDER", "M"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsEnumValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors = validate(ClaimDetailsViewField.GENDER, Map.of("GENDER", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[GENDER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Gender");
  }

  @Test
  void rejectsEnumValueNotMatchingAnAllowedOptionNamingDifferentField() {
    var errors = validate(ClaimDetailsViewField.ETHNICITY, Map.of("ETHNICITY", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[ETHNICITY]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Ethnicity");
  }

  @Test
  void acceptsBlankEnumValue() {
    var errors = validate(ClaimDetailsViewField.GENDER, Map.of("GENDER", ""));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void acceptsDisabilityValueMatchingAnAllowedOption() {
    var errors = validate(ClaimDetailsViewField.DISABILITY, Map.of("DISABILITY", "NCD"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsDisabilityValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors = validate(ClaimDetailsViewField.DISABILITY, Map.of("DISABILITY", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[DISABILITY]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Disability");
  }

  @Test
  void acceptsClientTypeValueMatchingAnAllowedOption() {
    var errors = validate(CivilClaimDetailsViewField.CLIENT_TYPE, Map.of("CLIENT_TYPE", "P"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsClientTypeValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors =
        validate(CivilClaimDetailsViewField.CLIENT_TYPE, Map.of("CLIENT_TYPE", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CLIENT_TYPE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Client type");
  }

  @Test
  void acceptsClient2GenderValueMatchingAnAllowedOption() {
    var errors =
        validate(MediationClaimDetailsViewField.CLIENT_2_GENDER, Map.of("CLIENT_2_GENDER", "M"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsClient2GenderValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors =
        validate(
            MediationClaimDetailsViewField.CLIENT_2_GENDER,
            Map.of("CLIENT_2_GENDER", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CLIENT_2_GENDER]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Gender");
  }

  @Test
  void acceptsClient2EthnicityValueMatchingAnAllowedOption() {
    var errors =
        validate(
            MediationClaimDetailsViewField.CLIENT_2_ETHNICITY, Map.of("CLIENT_2_ETHNICITY", "00"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsClient2EthnicityValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors =
        validate(
            MediationClaimDetailsViewField.CLIENT_2_ETHNICITY,
            Map.of("CLIENT_2_ETHNICITY", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CLIENT_2_ETHNICITY]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Ethnicity");
  }

  @Test
  void acceptsClient2DisabilityValueMatchingAnAllowedOption() {
    var errors =
        validate(
            MediationClaimDetailsViewField.CLIENT_2_DISABILITY,
            Map.of("CLIENT_2_DISABILITY", "NCD"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsClient2DisabilityValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors =
        validate(
            MediationClaimDetailsViewField.CLIENT_2_DISABILITY,
            Map.of("CLIENT_2_DISABILITY", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[CLIENT_2_DISABILITY]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Disability");
  }

  @Test
  void acceptsOutcomeValueMatchingAnAllowedOption() {
    var errors = validate(MediationClaimDetailsViewField.OUTCOME, Map.of("OUTCOME", "CN01"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsOutcomeValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors = validate(MediationClaimDetailsViewField.OUTCOME, Map.of("OUTCOME", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[OUTCOME]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Outcome");
  }

  @Test
  void acceptsReferralSourceValueMatchingAnAllowedOption() {
    var errors =
        validate(MediationClaimDetailsViewField.REFERRAL_SOURCE, Map.of("REFERRAL_SOURCE", "02"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsReferralSourceValueNotMatchingAnAllowedOptionNamingTheField() {
    var errors =
        validate(
            MediationClaimDetailsViewField.REFERRAL_SOURCE,
            Map.of("REFERRAL_SOURCE", "NOT_A_CODE"));

    assertThat(errors.hasErrors()).isTrue();
    var fieldError = errors.getFieldError("inputs[REFERRAL_SOURCE]");
    assertThat(fieldError.getCode()).isEqualTo("amendmentForm.enum.invalid");
    assertThat(fieldError.getArguments()[0]).isEqualTo("Referral");
  }

  private Errors validate(ClaimViewField<?> field, Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(field, form, errors);
    return errors;
  }
}
