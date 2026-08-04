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
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

class AmendmentFormValidatorTest {

  private static final MessageSource MESSAGE_SOURCE = TestMessageSources.real();

  @Test
  void dispatchesToTheUnitDeclaringTheFieldsType() {
    var textCalls = new AtomicInteger();
    var enumCalls = new AtomicInteger();

    var validator =
        new AmendmentFormValidator(
            MockClaimsFunctions.createMockCrimeClaim(),
            List.of(
                countingFieldValidator(FieldType.TEXT, textCalls),
                countingFieldValidator(FieldType.ENUM, enumCalls)),
            List.of());

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
            MockClaimsFunctions.createMockCrimeClaim(),
            List.of(
                countingFieldValidator(FieldType.TEXT, firstCalls),
                countingFieldValidator(FieldType.TEXT, secondCalls)),
            List.of());

    validate(validator, Map.of("UNIQUE_FILE_NUMBER", "value"));

    assertThat(firstCalls).hasValue(1);
    assertThat(secondCalls).hasValue(1);
  }

  @Test
  void aggregatesRejectionsAcrossMultipleFieldsOnTheSameForm() {
    var value = "a".repeat(51);
    var errors =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(), MESSAGE_SOURCE, List.of()),
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
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(), MESSAGE_SOURCE, List.of()),
            Map.of("IS_DUTY_SOLICITOR", "notABoolean"));

    assertThat(errors.getFieldError("inputs[IS_DUTY_SOLICITOR]").getCode())
        .isEqualTo("amendmentForm.boolean.invalid");
  }

  @Test
  void resolvesFieldRulesAccordingToTheClaimsAreaOfLaw() {
    var inputs = Map.of("STANDARD_FEE_CATEGORY", "1A");

    var validForCrime =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(), MESSAGE_SOURCE, List.of()), inputs);
    assertThat(validForCrime.hasErrors()).isFalse();

    var invalidCodeForCrime =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(), MESSAGE_SOURCE, List.of()),
            Map.of("STANDARD_FEE_CATEGORY", "NOPE"));
    assertThat(invalidCodeForCrime.hasErrors()).isTrue();

    assertThatThrownBy(
        () ->
            validate(
                new AmendmentFormValidator(
                    MockClaimsFunctions.createMockCivilClaim(), MESSAGE_SOURCE, List.of()),
                inputs))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void fieldWithNoMatchingFieldSpecificValidatorOnlyRunsItsFieldTypeValidator() {
    var errors =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(),
                MESSAGE_SOURCE,
                List.of(rejectingFieldSpecificValidator(CrimeClaimDetailsViewField.STAGE_REACHED))),
            Map.of("UNIQUE_FILE_NUMBER", "value"));

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void fieldWithMatchingFieldSpecificValidatorAggregatesRejectionsFromBoth() {
    var errors =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(),
                MESSAGE_SOURCE,
                List.of(rejectingFieldSpecificValidator(ClaimDetailsViewField.UNIQUE_FILE_NUMBER))),
            Map.of("UNIQUE_FILE_NUMBER", "a".repeat(51)));

    assertThat(errors.getFieldErrors("inputs[UNIQUE_FILE_NUMBER]")).hasSize(2);
    assertThat(
        errors.getFieldErrors("inputs[UNIQUE_FILE_NUMBER]").stream()
            .map(fieldError -> fieldError.getCode()))
        .containsExactlyInAnyOrder("amendmentForm.text.tooLong", "test.fieldSpecific.invalid");
  }

  private static FieldSpecificAmendmentValidator rejectingFieldSpecificValidator(
      ClaimViewField<?> targetField) {
    return new FieldSpecificAmendmentValidator() {
      @Override
      public boolean appliesTo(ClaimViewField<?> field) {
        return field == targetField;
      }

      @Override
      public void validate(ClaimViewField<?> field, AmendmentForm form, Errors errors) {
        errors.rejectValue(
            String.format(AmendmentFieldValidator.FIELD_PATH, field.name()),
            "test.fieldSpecific.invalid");
      }
    };
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
