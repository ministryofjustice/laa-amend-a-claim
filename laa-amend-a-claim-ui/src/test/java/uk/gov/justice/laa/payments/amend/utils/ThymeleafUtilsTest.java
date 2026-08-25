package uk.gov.justice.laa.payments.amend.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.payments.amend.forms.errors.AmendmentFormError;
import uk.gov.justice.laa.payments.amend.forms.errors.AssessedTotalFormError;
import uk.gov.justice.laa.payments.amend.forms.errors.AssessmentOutcomeFormError;
import uk.gov.justice.laa.payments.amend.forms.errors.SearchFormError;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafString;

public class ThymeleafUtilsTest {

  @Nested
  class ToSearchFormErrorsTests {

    @Test
    void sortErrorsByFieldOrder() {
      List<DetailedError> errors =
          List.of(
              new DetailedError(
                  "submissionDateYear",
                  null,
                  Stream.empty().toArray(),
                  "Submission date year error"),
              new DetailedError(
                  "uniqueFileNumber", null, Stream.empty().toArray(), "Unique file number error"),
              new DetailedError("officeCode", null, Stream.empty().toArray(), "Office code error"),
              new DetailedError(
                  "submissionDateMonth",
                  null,
                  Stream.empty().toArray(),
                  "Submission date month error"),
              new DetailedError(
                  "caseReferenceNumber",
                  null,
                  Stream.empty().toArray(),
                  "Case reference number error"));

      ThymeleafUtils sut = new ThymeleafUtils();

      List<SearchFormError> result = sut.toSearchFormErrors(errors);

      List<SearchFormError> expectedResult =
          List.of(
              new SearchFormError("officeCode", "Office code error"),
              new SearchFormError("submissionDateMonth", "Submission date month error"),
              new SearchFormError("submissionDateYear", "Submission date year error"),
              new SearchFormError("uniqueFileNumber", "Unique file number error"),
              new SearchFormError("caseReferenceNumber", "Case reference number error"));

      Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void filterOutDuplicateErrorMessages() {
      List<DetailedError> errors =
          List.of(
              new DetailedError(
                  "submissionDateYear",
                  null,
                  Stream.empty().toArray(),
                  "The submission date must be a real date"),
              new DetailedError(
                  "submissionDateMonth",
                  null,
                  Stream.empty().toArray(),
                  "The submission date must be a real date"));

      ThymeleafUtils sut = new ThymeleafUtils();

      List<SearchFormError> result = sut.toSearchFormErrors(errors);

      List<SearchFormError> expectedResult =
          List.of(
              new SearchFormError(
                  "submissionDateMonth", "The submission date must be a real date"));

      Assertions.assertEquals(expectedResult, result);
    }
  }

  @Nested
  class ToAssessmentOutcomeErrorsTests {

    @Test
    void sortErrorsByFieldOrder() {
      List<DetailedError> errors =
          List.of(
              new DetailedError(
                  "assessmentOutcome", null, Stream.empty().toArray(), "Assessment outcome error"),
              new DetailedError(
                  "contingencyAssessment",
                  null,
                  Stream.empty().toArray(),
                  "Contingency assessment error"));

      ThymeleafUtils sut = new ThymeleafUtils();

      List<AssessmentOutcomeFormError> result = sut.toAssessmentOutcomeErrors(errors);

      List<AssessmentOutcomeFormError> expectedResult =
          List.of(
              new AssessmentOutcomeFormError("assessmentOutcome", "Assessment outcome error"),
              new AssessmentOutcomeFormError(
                  "contingencyAssessment", "Contingency assessment error"));

      Assertions.assertEquals(expectedResult, result);
    }
  }

  @Nested
  class ToAssessedTotalFormErrorsTests {

    @Test
    void sortErrorsByFieldOrder() {
      List<DetailedError> errors =
          List.of(
              new DetailedError("assessedTotalInclVat", null, Stream.empty().toArray(), "foo"),
              new DetailedError("assessedTotalVat", null, Stream.empty().toArray(), "bar"));

      ThymeleafUtils sut = new ThymeleafUtils();

      List<AssessedTotalFormError> result = sut.toAssessedTotalFormErrors(errors);

      List<AssessedTotalFormError> expectedResult =
          List.of(
              new AssessedTotalFormError("assessedTotalVat", "bar"),
              new AssessedTotalFormError("assessedTotalInclVat", "foo"));

      Assertions.assertEquals(expectedResult, result);
    }
  }

  @Nested
  class ToAmendmentFormErrorsTests {

    @Test
    void extractsFieldNameFromInputsPathAcrossMultipleForms() {
      List<FieldError> errors =
          List.of(
              new FieldError(
                  "caseDetailsForm",
                  "inputs[CASE_REFERENCE_NUMBER]",
                  null,
                  false,
                  new String[] {"Value exceeds maximum length"},
                  new Object[] {},
                  null),
              new FieldError(
                  "caseTypeForm",
                  "inputs[MATTER_TYPE_CODE_1]",
                  null,
                  false,
                  new String[] {"Value is not a valid option"},
                  new Object[] {},
                  null));

      ThymeleafUtils sut = new ThymeleafUtils();

      List<AmendmentFormError> result = sut.toAmendmentFormErrors(errors);

      List<AmendmentFormError> expectedResult =
          List.of(
              new AmendmentFormError("CASE_REFERENCE_NUMBER", "Value exceeds maximum length"),
              new AmendmentFormError("MATTER_TYPE_CODE_1", "Value is not a valid option"));

      Assertions.assertEquals(expectedResult, result);
      Assertions.assertEquals("CASE_REFERENCE_NUMBER", result.get(0).getFieldId());
      Assertions.assertEquals("MATTER_TYPE_CODE_1", result.get(1).getFieldId());
    }

    @Test
    void filterOutDuplicateErrorMessages() {
      List<FieldError> errors =
          List.of(
              new FieldError(
                  "caseTypeForm",
                  "inputs[FEE_CODE]",
                  null,
                  false,
                  new String[] {"Value is required"},
                  new Object[] {},
                  null),
              new FieldError(
                  "caseTypeForm",
                  "inputs[MATTER_TYPE_CODE]",
                  null,
                  false,
                  new String[] {"Value is required"},
                  new Object[] {},
                  null));

      ThymeleafUtils sut = new ThymeleafUtils();

      List<AmendmentFormError> result = sut.toAmendmentFormErrors(errors);

      List<AmendmentFormError> expectedResult =
          List.of(new AmendmentFormError("FEE_CODE", "Value is required"));

      Assertions.assertEquals(expectedResult, result);
    }
  }

  @Nested
  class OrEmptyTests {

    @Test
    void returnsEmptyListWhenNull() {
      ThymeleafUtils sut = new ThymeleafUtils();

      Assertions.assertEquals(List.of(), sut.orEmpty(null));
    }

    @Test
    void returnsGivenListWhenNotNull() {
      ThymeleafUtils sut = new ThymeleafUtils();
      var errors = List.of(new AmendmentFormError("FEE_CODE", "Value is required"));

      Assertions.assertEquals(errors, sut.orEmpty(errors));
    }
  }

  @Nested
  class HasAmendmentFieldErrorTests {

    @Test
    void trueWhenFieldHasAnError() {
      ThymeleafUtils sut = new ThymeleafUtils();
      var errors = List.of(new AmendmentFormError("FEE_CODE", "Value is required"));

      Assertions.assertTrue(sut.hasAmendmentFieldError(errors, "FEE_CODE"));
    }

    @Test
    void falseWhenFieldHasNoError() {
      ThymeleafUtils sut = new ThymeleafUtils();
      var errors = List.of(new AmendmentFormError("FEE_CODE", "Value is required"));

      Assertions.assertFalse(sut.hasAmendmentFieldError(errors, "MATTER_TYPE_CODE"));
    }

    @Test
    void falseWhenErrorsIsNull() {
      ThymeleafUtils sut = new ThymeleafUtils();

      Assertions.assertFalse(sut.hasAmendmentFieldError(null, "FEE_CODE"));
    }
  }

  @Nested
  class AmendmentFieldErrorMessageTests {

    @Test
    void returnsMessageForField() {
      ThymeleafUtils sut = new ThymeleafUtils();
      var errors = List.of(new AmendmentFormError("FEE_CODE", "Value is required"));

      Assertions.assertEquals(
          new AmendmentFormError("FEE_CODE", "Value is required"),
          sut.amendmentFieldErrorMessage(errors, "FEE_CODE"));
    }

    @Test
    void returnsNullWhenFieldHasNoError() {
      ThymeleafUtils sut = new ThymeleafUtils();
      var errors = List.of(new AmendmentFormError("FEE_CODE", "Value is required"));

      Assertions.assertNull(sut.amendmentFieldErrorMessage(errors, "MATTER_TYPE_CODE"));
    }
  }

  @Nested
  class GetFormattedValueTests {

    @Test
    void formatsOffsetDateTimeInLondonTimeZoneDuringGmt() {
      // December is GMT (UTC+0), so London time == UTC time
      OffsetDateTime utcDateTime =
          OffsetDateTime.of(LocalDateTime.of(2025, 12, 18, 16, 11, 27), ZoneOffset.UTC);
      ThymeleafUtils sut = new ThymeleafUtils();

      ThymeleafString result = sut.getFormattedValue(utcDateTime);

      Assertions.assertInstanceOf(ThymeleafMessage.class, result);
      ThymeleafMessage message = (ThymeleafMessage) result;
      Assertions.assertEquals("fulldate.format", message.getKey());
      Assertions.assertEquals("18 December 2025", message.getParams()[0]);
      Assertions.assertEquals("4:11pm", message.getParams()[1]);
    }

    @Test
    void formatsOffsetDateTimeInLondonTimeZoneDuringBst() {
      // June is BST (UTC+1): UTC 14:30:00 = London 15:30:00
      OffsetDateTime utcDateTime =
          OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC);
      ThymeleafUtils sut = new ThymeleafUtils();

      ThymeleafString result = sut.getFormattedValue(utcDateTime);

      Assertions.assertInstanceOf(ThymeleafMessage.class, result);
      ThymeleafMessage message = (ThymeleafMessage) result;
      Assertions.assertEquals("fulldate.format", message.getKey());
      Assertions.assertEquals("15 June 2025", message.getParams()[0]);
      Assertions.assertEquals("3:30pm", message.getParams()[1]);
    }
  }
}
