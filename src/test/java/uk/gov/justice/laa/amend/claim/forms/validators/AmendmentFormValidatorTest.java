package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.AmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

class AmendmentFormValidatorTest {

  private static final MessageSource MESSAGE_SOURCE = TestMessageSources.real();

  @Test
  void dispatchesToTheUnitDeclaringTheFieldsType() {
    var textCalls = new AtomicInteger();
    var enumCalls = new AtomicInteger();

    var validator =
        new AmendmentFormValidator(
            CrimeClaimDetails.class,
            List.of(
                countingFieldValidator(FieldType.TEXT, textCalls),
                countingFieldValidator(FieldType.ENUM, enumCalls)));

    validate(validator, Map.of("UNIQUE_FILE_NUMBER", "value"));

    assertThat(textCalls).hasValue(1);
    assertThat(enumCalls).hasValue(0);
  }

  @Test
  void supportsMoreThanOneUnitForTheSameFieldType() {
    var firstCalls = new AtomicInteger();
    var secondCalls = new AtomicInteger();

    var validator =
        new AmendmentFormValidator(
            CrimeClaimDetails.class,
            List.of(
                countingFieldValidator(FieldType.TEXT, firstCalls),
                countingFieldValidator(FieldType.TEXT, secondCalls)));

    validate(validator, Map.of("UNIQUE_FILE_NUMBER", "value"));

    assertThat(firstCalls).hasValue(1);
    assertThat(secondCalls).hasValue(1);
  }

  @Test
  void aggregatesRejectionsAcrossMultipleFieldsOnTheSameForm() {
    var value = "a".repeat(256);
    var errors =
        validate(
            new AmendmentFormValidator(CrimeClaimDetails.class, MESSAGE_SOURCE),
            Map.of("UNIQUE_FILE_NUMBER", value, "STAGE_REACHED", "NOT_A_CODE"));

    assertThat(errors.getFieldErrors()).hasSize(2);
    assertThat(errors.getFieldError("inputs[UNIQUE_FILE_NUMBER]").getCode())
        .isEqualTo("amendmentForm.text.tooLong");
    assertThat(errors.getFieldError("inputs[STAGE_REACHED]").getCode())
        .isEqualTo("amendmentForm.enum.invalid");
  }

  @Test
  void rejectsTamperedBooleanValueInsteadOfThrowing() {
    var errors =
        validate(
            new AmendmentFormValidator(CrimeClaimDetails.class, MESSAGE_SOURCE),
            Map.of("IS_DUTY_SOLICITOR", "notABoolean"));

    assertThat(errors.getFieldError("inputs[IS_DUTY_SOLICITOR]").getCode())
        .isEqualTo("amendmentForm.boolean.invalid");
  }

  @Test
  void resolvesFieldRulesAccordingToTheClaimsAreaOfLaw() {
    var inputs = Map.of("STANDARD_FEE_CATEGORY", "1A");

    var validForCrime =
        validate(new AmendmentFormValidator(CrimeClaimDetails.class, MESSAGE_SOURCE), inputs);
    assertThat(validForCrime.hasErrors()).isFalse();

    var invalidCodeForCrime =
        validate(
            new AmendmentFormValidator(CrimeClaimDetails.class, MESSAGE_SOURCE),
            Map.of("STANDARD_FEE_CATEGORY", "NOPE"));
    assertThat(invalidCodeForCrime.hasErrors()).isTrue();

    assertThatThrownBy(
            () ->
                validate(
                    new AmendmentFormValidator(CivilClaimDetails.class, MESSAGE_SOURCE), inputs))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private static AmendmentFieldValidator countingFieldValidator(
      FieldType type, AtomicInteger calls) {
    return new AmendmentFieldValidator() {
      @Override
      public FieldType supportedType() {
        return type;
      }

      @Override
      public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
        calls.incrementAndGet();
      }
    };
  }

  private Errors validate(AmendmentFormValidator validator, Map<String, String> inputs) {
    var form = new AmendmentForm();
    form.setInputs(inputs);

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(form, errors);
    return errors;
  }
}
