package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SearchFormTest extends FormTest {

  @Test
  void testMissingOfficeCode() {
    SearchForm form = new SearchForm();

    form.setOfficeCode("");
    form.setSubmissionDateMonth("");
    form.setSubmissionDateYear("");
    form.setUniqueFileNumber("");
    form.setCaseReferenceNumber("");

    Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
    ConstraintViolation<SearchForm> violation = getViolation(violations, "officeCode");

    Assertions.assertNotNull(violation);
    Assertions.assertEquals("{index.officeCode.error.required}", violation.getMessage());
  }

  @Test
  void testInvalidOfficeCode() {
    SearchForm form = new SearchForm();

    form.setOfficeCode("!!!!!!");
    form.setSubmissionDateMonth("5");
    form.setSubmissionDateYear("2025");
    form.setUniqueFileNumber("120223/001");
    form.setCaseReferenceNumber("789");

    Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
    ConstraintViolation<SearchForm> violation = getViolation(violations, "officeCode");

    Assertions.assertNotNull(violation);
    Assertions.assertEquals("{index.officeCode.error.invalid}", violation.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"12345", "1234567"})
  void testMalformattedOfficeCode(String officeCode) {
    SearchForm form = new SearchForm();

    form.setOfficeCode(officeCode);
    form.setSubmissionDateMonth("5");
    form.setSubmissionDateYear("2025");
    form.setUniqueFileNumber("120223/001");
    form.setCaseReferenceNumber("789");

    Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
    ConstraintViolation<SearchForm> violation = getViolation(violations, "officeCode");

    Assertions.assertNotNull(violation);
    Assertions.assertEquals("{index.officeCode.error.format}", violation.getMessage());
  }

  @Test
  void testInvalidMonthAndYear() {
    SearchForm form = new SearchForm();

    form.setOfficeCode("0P322F");
    form.setSubmissionDateMonth("!");
    form.setSubmissionDateYear("!");
    form.setUniqueFileNumber("120223/001");
    form.setCaseReferenceNumber("789");

    Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
    ConstraintViolation<SearchForm> monthViolation =
        getViolation(violations, "submissionDateMonth");
    ConstraintViolation<SearchForm> yearViolation = getViolation(violations, "submissionDateYear");

    Assertions.assertEquals("{index.submissionDate.error.invalid}", monthViolation.getMessage());
    Assertions.assertEquals("{index.submissionDate.error.invalid}", yearViolation.getMessage());
  }

  @Test
  void testInvalidUniqueFileNumber() {
    SearchForm form = new SearchForm();

    form.setOfficeCode("0P322F");
    form.setSubmissionDateMonth("5");
    form.setSubmissionDateYear("2025");
    form.setUniqueFileNumber("!!!!!!/!!!");
    form.setCaseReferenceNumber("789");

    Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
    ConstraintViolation<SearchForm> violation = getViolation(violations, "uniqueFileNumber");

    Assertions.assertNotNull(violation);
    Assertions.assertEquals("{index.uniqueFileNumber.error.invalid}", violation.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"120/223001", "123456789", "1234567890"})
  void testMalformattedUniqueFileNumber(String uniqueFileNumber) {
    SearchForm form = new SearchForm();

    form.setOfficeCode("0P322F");
    form.setSubmissionDateMonth("5");
    form.setSubmissionDateYear("2025");
    form.setUniqueFileNumber(uniqueFileNumber);
    form.setCaseReferenceNumber("789");

    Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
    ConstraintViolation<SearchForm> violation = getViolation(violations, "uniqueFileNumber");

    Assertions.assertNotNull(violation);
    Assertions.assertEquals("{index.uniqueFileNumber.error.format}", violation.getMessage());
  }

  @Test
  void testInvalidCaseReferenceNumber() {
    SearchForm form = new SearchForm();

    form.setOfficeCode("0P322F");
    form.setSubmissionDateMonth("5");
    form.setSubmissionDateYear("2025");
    form.setUniqueFileNumber("120223/001");
    form.setCaseReferenceNumber("!!!");

    Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
    ConstraintViolation<SearchForm> violation = getViolation(violations, "caseReferenceNumber");

    Assertions.assertNotNull(violation);
    Assertions.assertEquals("{index.caseReferenceNumber.error.invalid}", violation.getMessage());
  }

  @Test
  void testMalformattedCaseReferenceNumber() {
    SearchForm form = new SearchForm();

    form.setOfficeCode("0P322F");
    form.setSubmissionDateMonth("5");
    form.setSubmissionDateYear("2025");
    form.setUniqueFileNumber("120223/001");
    form.setCaseReferenceNumber("1234567890123456789012345678901");

    Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
    ConstraintViolation<SearchForm> violation = getViolation(violations, "caseReferenceNumber");

    Assertions.assertNotNull(violation);
    Assertions.assertEquals("{index.caseReferenceNumber.error.format}", violation.getMessage());
  }

  @Nested
  class SearchFormAnyNonEmptyTest {

    @Test
    void testAnyNonEmptyReturnsFalseWhenAllValuesAreNull() {
      SearchForm form = new SearchForm();
      Assertions.assertFalse(form.anyNonEmpty());
    }

    @Test
    void testAllEmptyReturnsFalseWhenAllValuesAreEmpty() {
      SearchForm form = new SearchForm();

      form.setOfficeCode("");
      form.setSubmissionDateMonth("");
      form.setSubmissionDateYear("");
      form.setUniqueFileNumber("");
      form.setCaseReferenceNumber("");

      Assertions.assertFalse(form.anyNonEmpty());
    }

    @Test
    void testAllEmptyReturnsFalseWhenAllValuesAreBlank() {
      SearchForm form = new SearchForm();

      form.setOfficeCode(" ");
      form.setSubmissionDateMonth(" ");
      form.setSubmissionDateYear(" ");
      form.setUniqueFileNumber(" ");
      form.setCaseReferenceNumber(" ");

      Assertions.assertFalse(form.anyNonEmpty());
    }

    @Test
    void testAllEmptyReturnsTrueWhenOneValueIsNotNull() {
      SearchForm form = new SearchForm();

      form.setOfficeCode("123");

      Assertions.assertTrue(form.anyNonEmpty());
    }

    @Test
    void testAllEmptyReturnsTrueWhenAllValuesAreNotNull() {
      SearchForm form = new SearchForm();

      form.setOfficeCode("123");
      form.setSubmissionDateMonth("3");
      form.setSubmissionDateYear("2007");
      form.setUniqueFileNumber("456");
      form.setCaseReferenceNumber("789");

      Assertions.assertTrue(form.anyNonEmpty());
    }
  }
}
