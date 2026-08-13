package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.BigDecimalAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.BooleanAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.DateAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.EnumAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.GenericAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.NumberAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.TextAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.FieldType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

class AmendmentFormValidatorTest {

  @Test
  void supportsAmendmentFormType() {
    var validator =
        new AmendmentFormValidator(
            MockClaimsFunctions.createMockCrimeClaim(), List.of(), List.of());

    assertThat(validator.supports(AmendmentForm.class)).isTrue();
    assertThat(validator.supports(Object.class)).isFalse();
  }

  @Test
  void shouldRunGenericTextValidator() {
    var callCount = new AtomicInteger();

    var validator =
        new AmendmentFormValidator(
            MockClaimsFunctions.createMockCrimeClaim(),
            List.of(countingFieldValidator(FieldType.TEXT, callCount)),
            List.of());

    validate(validator, Map.of("UNIQUE_FILE_NUMBER", "value"));

    assertThat(callCount).hasValue(1);
  }

  @Test
  void throwsWhenNoGenericValidatorSupportsTheFieldType() {
    var validator =
        new AmendmentFormValidator(
            MockClaimsFunctions.createMockCrimeClaim(),
            List.of(countingFieldValidator(FieldType.ENUM, new AtomicInteger())),
            List.of());

    assertThatThrownBy(() -> validate(validator, Map.of("UNIQUE_FILE_NUMBER", "value")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Unsupported field type: TEXT");
  }

  @Test
  void collectsErrorsFromDifferentFieldTypesInOnePass() {
    var errors =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(), defaultFieldValidators(), List.of()),
            Map.of("UNIQUE_FILE_NUMBER", "a".repeat(51), "STAGE_REACHED", "NOT_A_CODE"));

    assertThat(errors.getFieldErrors()).hasSize(2);
    assertThat(errors.getFieldError("inputs[UNIQUE_FILE_NUMBER]").getCode())
        .isEqualTo("amendmentForm.text.tooLong");
    assertThat(errors.getFieldError("inputs[STAGE_REACHED]").getCode())
        .isEqualTo("amendmentForm.enum.invalid");
  }

  @Test
  void throwsForTamperedBooleanValueDuringFieldMapping() {
    var validator =
        new AmendmentFormValidator(
            MockClaimsFunctions.createMockCrimeClaim(), defaultFieldValidators(), List.of());

    assertThatThrownBy(() -> validate(validator, Map.of("IS_DUTY_SOLICITOR", "notABoolean")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Boolean value")
        .hasMessageContaining("IS_DUTY_SOLICITOR");
  }

  @Test
  void runsFieldSpecificValidatorOnlyForMatchingField() {
    var seenField = new AtomicReference<ClaimViewField<?>>(null);
    var validator =
        new AmendmentFormValidator(
            MockClaimsFunctions.createMockCrimeClaim(),
            defaultFieldValidators(),
            List.of(
                recordingFieldSpecificValidator(
                    CrimeClaimDetailsViewField.STAGE_REACHED, seenField)));

    validate(validator, Map.of("UNIQUE_FILE_NUMBER", "value"));

    assertThat(seenField.get()).isNull();
  }

  @Test
  void shouldPrioritiseGenericErrorWhenBothValidatorsThrowAndError() {
    var errors =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(),
                defaultFieldValidators(),
                List.of(
                    rejectingFieldSpecificValidator(
                        CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER))),
            Map.of("UNIQUE_FILE_NUMBER", "a".repeat(51)));

    assertThat(errors.getFieldErrors("inputs[UNIQUE_FILE_NUMBER]")).hasSize(1);
    assertThat(
            errors.getFieldErrors("inputs[UNIQUE_FILE_NUMBER]").stream()
                .map(DefaultMessageSourceResolvable::getCode))
        .containsExactlyInAnyOrder("amendmentForm.text.tooLong");
  }

  @Test
  void appliesFieldRulesUsingTheCurrentClaimType() {
    var validForCrime =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(), defaultFieldValidators(), List.of()),
            Map.of("STANDARD_FEE_CATEGORY", "1A"));
    assertThat(validForCrime.hasErrors()).isFalse();

    var invalidForCrime =
        validate(
            new AmendmentFormValidator(
                MockClaimsFunctions.createMockCrimeClaim(), defaultFieldValidators(), List.of()),
            Map.of("STANDARD_FEE_CATEGORY", "NOPE"));
    assertThat(invalidForCrime.getFieldError("inputs[STANDARD_FEE_CATEGORY]").getCode())
        .isEqualTo("amendmentForm.enum.invalid");

    assertThatThrownBy(
            () ->
                validate(
                    new AmendmentFormValidator(
                        MockClaimsFunctions.createMockCivilClaim(),
                        defaultFieldValidators(),
                        List.of()),
                    Map.of("STANDARD_FEE_CATEGORY", "1A")))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private static List<GenericAmendmentFieldValidator> defaultFieldValidators() {
    var messageSource = TestMessageSources.real();
    return List.of(
        new TextAmendmentFieldValidator(messageSource),
        new EnumAmendmentFieldValidator(messageSource),
        new NumberAmendmentFieldValidator(messageSource),
        new BigDecimalAmendmentFieldValidator(messageSource),
        new BooleanAmendmentFieldValidator(messageSource),
        new DateAmendmentFieldValidator(messageSource));
  }

  private static FieldSpecificAmendmentValidator rejectingFieldSpecificValidator(
      ClaimViewField<?> targetField) {
    return new FieldSpecificAmendmentValidator() {
      @Override
      public boolean appliesTo(ClaimViewField<?> field) {
        return field == targetField;
      }

      @Override
      public void validate(
          ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
        addUniqueFieldError(field, "test.fieldSpecific.invalid", new String[] {}, errors);
      }
    };
  }

  private static FieldSpecificAmendmentValidator recordingFieldSpecificValidator(
      ClaimViewField<?> targetField, AtomicReference<ClaimViewField<?>> seenField) {
    return new FieldSpecificAmendmentValidator() {
      @Override
      public boolean appliesTo(ClaimViewField<?> field) {
        return field == targetField;
      }

      @Override
      public void validate(
          ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
        seenField.set(field);
      }
    };
  }

  private static GenericAmendmentFieldValidator countingFieldValidator(
      FieldType type, AtomicInteger calls) {
    return new GenericAmendmentFieldValidator() {
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
