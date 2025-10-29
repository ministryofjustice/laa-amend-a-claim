package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.SortDirection;

import java.util.Set;

public class SearchFormTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testMissingProviderAccountNumber() {
        SearchForm form = new SearchForm();

        form.setProviderAccountNumber("");
        form.setSubmissionDateMonth("");
        form.setSubmissionDateYear("");
        form.setUniqueFileNumber("");
        form.setCaseReferenceNumber("");

        Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
        ConstraintViolation<SearchForm> violation = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("providerAccountNumber"))
            .findFirst()
            .orElse(null);

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{index.providerAccountNumber.error.required}", violation.getMessage());
    }

    @Test
    void testInvalidProviderAccountNumber() {
        SearchForm form = new SearchForm();

        form.setProviderAccountNumber("!");
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear("2025");
        form.setUniqueFileNumber("ABC 123");
        form.setCaseReferenceNumber("789");

        Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
        ConstraintViolation<SearchForm> violation = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("providerAccountNumber"))
            .findFirst()
            .orElse(null);

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{index.providerAccountNumber.error.invalid}", violation.getMessage());
    }

    @Test
    void testInvalidMonthAndYear() {
        SearchForm form = new SearchForm();

        form.setProviderAccountNumber("ABC 123");
        form.setSubmissionDateMonth("!");
        form.setSubmissionDateYear("!");
        form.setUniqueFileNumber("ABC 123");
        form.setCaseReferenceNumber("789");

        Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
        ConstraintViolation<SearchForm> monthViolation = getViolation(violations, "submissionDateMonth");
        ConstraintViolation<SearchForm> yearViolation = getViolation(violations, "submissionDateYear");

        Assertions.assertEquals("{index.submissionDate.error.invalid}", monthViolation.getMessage());
        Assertions.assertEquals("{index.submissionDate.error.invalid}", yearViolation.getMessage());
    }

    @Test
    void testInvalidUniqueFileNumber() {
        SearchForm form = new SearchForm();

        form.setProviderAccountNumber("ABC 123");
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear("2025");
        form.setUniqueFileNumber("!");
        form.setCaseReferenceNumber("789");

        Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
        ConstraintViolation<SearchForm> violation = getViolation(violations, "uniqueFileNumber");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{index.uniqueFileNumber.error.invalid}", violation.getMessage());
    }

    @Test
    void testInvalidCaseReferenceNumber() {
        SearchForm form = new SearchForm();

        form.setProviderAccountNumber("ABC 123");
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear("2025");
        form.setUniqueFileNumber("456");
        form.setCaseReferenceNumber("!");

        Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
        ConstraintViolation<SearchForm> violation = getViolation(violations, "caseReferenceNumber");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{index.caseReferenceNumber.error.invalid}", violation.getMessage());
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

            form.setProviderAccountNumber("");
            form.setSubmissionDateMonth("");
            form.setSubmissionDateYear("");
            form.setUniqueFileNumber("");
            form.setCaseReferenceNumber("");

            Assertions.assertFalse(form.anyNonEmpty());
        }

        @Test
        void testAllEmptyReturnsFalseWhenAllValuesAreBlank() {
            SearchForm form = new SearchForm();

            form.setProviderAccountNumber(" ");
            form.setSubmissionDateMonth(" ");
            form.setSubmissionDateYear(" ");
            form.setUniqueFileNumber(" ");
            form.setCaseReferenceNumber(" ");

            Assertions.assertFalse(form.anyNonEmpty());
        }

        @Test
        void testAllEmptyReturnsTrueWhenOneValueIsNotNull() {
            SearchForm form = new SearchForm();

            form.setProviderAccountNumber("123");

            Assertions.assertTrue(form.anyNonEmpty());
        }

        @Test
        void testAllEmptyReturnsTrueWhenAllValuesAreNotNull() {
            SearchForm form = new SearchForm();

            form.setProviderAccountNumber("123");
            form.setSubmissionDateMonth("3");
            form.setSubmissionDateYear("2007");
            form.setUniqueFileNumber("456");
            form.setCaseReferenceNumber("789");

            Assertions.assertTrue(form.anyNonEmpty());
        }
    }

    @Nested
    class RedirectUrlTests {
        @Test
        void createRedirectUrlWhenAccountNumberPresent() {
            SearchForm form = new SearchForm();

            form.setProviderAccountNumber("123");

            String result = form.getRedirectUrl(1, new Sort("uniqueFileNumber", SortDirection.ASCENDING));

            Assertions.assertEquals("/?providerAccountNumber=123&page=1&sort=uniqueFileNumber,asc", result);
        }

        @Test
        void createRedirectUrlWhenAccountNumberAndDatePresent() {
            SearchForm form = new SearchForm();

            form.setProviderAccountNumber("123");
            form.setSubmissionDateMonth("3");
            form.setSubmissionDateYear("2007");

            String result = form.getRedirectUrl(2, new Sort("uniqueFileNumber", SortDirection.ASCENDING));

            Assertions.assertEquals("/?providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007&page=2&sort=uniqueFileNumber,asc", result);
        }

        @Test
        void createRedirectUrlWhenAllPresent() {
            SearchForm form = new SearchForm();

            form.setProviderAccountNumber("123");
            form.setSubmissionDateMonth("3");
            form.setSubmissionDateYear("2007");
            form.setUniqueFileNumber("456");
            form.setCaseReferenceNumber("789");

            String result = form.getRedirectUrl(3, new Sort("uniqueFileNumber", SortDirection.ASCENDING));

            Assertions.assertEquals("/?providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007&uniqueFileNumber=456&caseReferenceNumber=789&page=3&sort=uniqueFileNumber,asc", result);
        }
    }

    private ConstraintViolation<SearchForm> getViolation(Set<ConstraintViolation<SearchForm>> violations, String field) {
        ConstraintViolation<SearchForm> violation = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals(field))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(violation);
        return violation;
    }
}
