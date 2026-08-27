package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.support.TestMessageSources;
import uk.gov.justice.laa.payments.amend.utils.DateWrapperUtil;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;

@ExtendWith(MockitoExtension.class)
class UniqueFileNumberDateValidatorTest {

  private static final String FIELD_PATH = "inputs[UNIQUE_FILE_NUMBER]";
  private static final String LABEL = "Unique file number (UFN)";

  UniqueFileNumberDateValidator validator;

  @Mock DateWrapperUtil dateWrapperUtil;

  @BeforeEach
  void beforeEach() {
    validator = new UniqueFileNumberDateValidator(TestMessageSources.real(), dateWrapperUtil);
  }

  @Test
  void appliesToUniqueFileNumberFieldsOnly() {
    assertThat(validator.appliesTo(CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER)).isTrue();
    assertThat(validator.appliesTo(CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER)).isTrue();
    assertThat(validator.appliesTo(ClaimDetailsViewField.CASE_START_DATE)).isFalse();
    assertThat(validator.appliesTo(MediationClaimDetailsViewField.SCHEDULE_REFERENCE)).isFalse();
  }

  @Nested
  class Civil {

    @Test
    void acceptsRealPastDate() {
      var errors = validateCivil("120223/001");

      assertThat(errors.hasFieldErrors()).isFalse();
    }

    @Test
    void acceptsLeapDayInLeapYear() {
      var errors = validateCivil("290224/001");

      assertThat(errors.hasFieldErrors()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"310223/001", "290223/001", "000223/001", "120023/001", "121323/001"})
    void rejectsDatesThatDoNotExist(String uniqueFileNumber) {
      var errors = validateCivil(uniqueFileNumber);

      assertThat(errors.hasFieldErrors()).isTrue();
      assertThat(Objects.requireNonNull(errors.getFieldError(FIELD_PATH)).getCode())
          .isEqualTo(UniqueFileNumberDateValidator.INVALID_DATE);
      assertThat(
              Objects.requireNonNull(
                  Objects.requireNonNull(errors.getFieldError(FIELD_PATH)).getArguments())[0])
          .isEqualTo(LABEL);
    }

    @Test
    void rejectsDateInFuture() {
      when(dateWrapperUtil.isFutureDate(any())).thenReturn(true);

      var errors = validateCivil("120245/001");

      assertThat(errors.hasFieldErrors()).isTrue();
      assertThat(Objects.requireNonNull(errors.getFieldError(FIELD_PATH)).getCode())
          .isEqualTo(UniqueFileNumberDateValidator.DATE_IN_FUTURE);
      assertThat(
              Objects.requireNonNull(
                  Objects.requireNonNull(errors.getFieldError(FIELD_PATH)).getArguments())[0])
          .isEqualTo(LABEL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "12022/001", "120223/1", "1202233001", "abcdef/001"})
    void ignoresValuesTheFormatRuleAlreadyRejects(String uniqueFileNumber) {
      var errors = validateCivil(uniqueFileNumber);

      assertThat(errors.hasFieldErrors()).isFalse();
    }

    @Test
    void ignoresMissingValue() {
      var form = new AmendmentForm();
      form.setInputs(Map.of());
      var errors = new BeanPropertyBindingResult(form, "amendmentForm");

      validator.validate(
          MockClaimsFunctions.createMockCivilClaim(),
          CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER,
          form,
          errors);

      assertThat(errors.hasFieldErrors()).isFalse();
    }
  }

  @Nested
  class Crime {

    @Test
    void acceptsRealPastDate() {
      var errors = validateCrime("120223/001");

      assertThat(errors.hasFieldErrors()).isFalse();
    }

    @Test
    void rejectsDateThatDoesNotExist() {
      var errors = validateCrime("310223/001");

      assertThat(errors.hasFieldErrors()).isTrue();
      assertThat(Objects.requireNonNull(errors.getFieldError(FIELD_PATH)).getCode())
          .isEqualTo(UniqueFileNumberDateValidator.INVALID_DATE);
    }

    @Test
    void rejectsDateInFuture() {
      when(dateWrapperUtil.isFutureDate(any())).thenReturn(true);

      var errors = validateCrime("120245/001");

      assertThat(errors.hasFieldErrors()).isTrue();
      assertThat(Objects.requireNonNull(errors.getFieldError(FIELD_PATH)).getCode())
          .isEqualTo(UniqueFileNumberDateValidator.DATE_IN_FUTURE);
    }
  }

  @Nested
  class TwoDigitYear {

    @ParameterizedTest
    @CsvSource({
      "120200/001, 2000-02-12",
      "120223/001, 2023-02-12",
      "120250/001, 2050-02-12",
      "120251/001, 1951-02-12",
      "120299/001, 1999-02-12"
    })
    void resolvesCenturyAtTheCutoffUsedByTheValidationLibrary(
        String uniqueFileNumber, LocalDate expected) {
      validateCivil(uniqueFileNumber);

      var captor = ArgumentCaptor.forClass(LocalDate.class);
      verify(dateWrapperUtil).isFutureDate(captor.capture());
      assertThat(captor.getValue()).isEqualTo(expected);
    }
  }

  private Errors validateCivil(String uniqueFileNumber) {
    return validate(
        MockClaimsFunctions.createMockCivilClaim(),
        CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER,
        uniqueFileNumber);
  }

  private Errors validateCrime(String uniqueFileNumber) {
    return validate(
        MockClaimsFunctions.createMockCrimeClaim(),
        CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER,
        uniqueFileNumber);
  }

  private Errors validate(ClaimDetails claim, ClaimViewField<?> field, String uniqueFileNumber) {
    var form = new AmendmentForm();
    form.setInputs(Map.of(field.name(), uniqueFileNumber));

    var errors = new BeanPropertyBindingResult(form, "amendmentForm");
    validator.validate(claim, field, form, errors);
    return errors;
  }
}
