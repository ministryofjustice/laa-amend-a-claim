package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
        SearchForm form = new SearchForm(
            "",
            "",
            "",
            ""
        );

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
        SearchForm form = new SearchForm(
            "!",
            "5",
            "2025",
            "ABC 123"
        );

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
        SearchForm form = new SearchForm(
            "ABC 123",
            "!",
            "!",
            "ABC 123"
        );

        Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
        ConstraintViolation<SearchForm> monthViolation = getViolation(violations, "submissionDateMonth");
        ConstraintViolation<SearchForm> yearViolation = getViolation(violations, "submissionDateYear");

        Assertions.assertEquals("{index.submissionDate.error.invalid}", monthViolation.getMessage());
        Assertions.assertEquals("{index.submissionDate.error.invalid}", yearViolation.getMessage());
    }

    @Test
    void testInvalidReferenceNumber() {
        SearchForm form = new SearchForm(
            "ABC 123",
            "5",
            "2025",
            "!"
        );

        Set<ConstraintViolation<SearchForm>> violations = validator.validate(form);
        ConstraintViolation<SearchForm> violation = getViolation(violations, "referenceNumber");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{index.referenceNumber.error.invalid}", violation.getMessage());
    }

    @Nested
    class SearchFormAnyNonEmptyTest {

        @Test
        void testAnyNonEmptyReturnsFalseWhenAllValuesAreNull() {
            SearchForm form = new SearchForm(
                null,
                null,
                null,
                null
            );
            Assertions.assertFalse(form.anyNonEmpty());
        }

        @Test
        void testAllEmptyReturnsFalseWhenAllValuesAreEmpty() {
            SearchForm form = new SearchForm(
                "",
                "",
                "",
                ""
            );

            Assertions.assertFalse(form.anyNonEmpty());
        }

        @Test
        void testAllEmptyReturnsFalseWhenAllValuesAreBlank() {
            SearchForm form = new SearchForm(
                " ",
                " ",
                " ",
                " "
            );

            Assertions.assertFalse(form.anyNonEmpty());
        }

        @Test
        void testAllEmptyReturnsTrueWhenOneValueIsNotNull() {
            SearchForm form = new SearchForm(
                "123",
                null,
                null,
                null
            );

            Assertions.assertTrue(form.anyNonEmpty());
        }

        @Test
        void testAllEmptyReturnsTrueWhenAllValuesAreNotNull() {
            SearchForm form = new SearchForm(
                "123",
                "3",
                "2007",
                "456"
            );

            Assertions.assertTrue(form.anyNonEmpty());
        }
    }

    @Nested
    class RedirectUrlTests {
        @Test
        void createRedirectUrlWhenAccountNumberPresent() {
            SearchForm form = new SearchForm(
                "123",
                null,
                null,
                null
            );

            String result = form.getRedirectUrl(1);

            Assertions.assertEquals("/?page=1&providerAccountNumber=123", result);
        }

        @Test
        void createRedirectUrlWhenAccountNumberAndDatePresent() {
            SearchForm form = new SearchForm(
                "123",
                "3",
                "2007",
                null
            );

            String result = form.getRedirectUrl(2);

            Assertions.assertEquals("/?page=2&providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007", result);
        }

        @Test
        void createRedirectUrlWhenAllPresent() {
            SearchForm form = new SearchForm(
                "123",
                "3",
                "2007",
                "456"
            );

            String result = form.getRedirectUrl(3);

            Assertions.assertEquals("/?page=3&providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007&referenceNumber=456", result);
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
