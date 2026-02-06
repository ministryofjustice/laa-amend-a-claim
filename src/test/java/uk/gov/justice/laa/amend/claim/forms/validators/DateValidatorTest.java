package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;

class DateValidatorTest {

    private DateValidator validator;
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        validator = new DateValidator();
        constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        NodeBuilderCustomizableContext nodeBuilderCustomizableContext = mock(NodeBuilderCustomizableContext.class);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilderCustomizableContext);
        when(builder.addConstraintViolation()).thenReturn(constraintValidatorContext);
    }

    @Test
    void testBothNullIsValid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth(null);
        form.setSubmissionDateYear(null);

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }

    @Test
    void testBothBlankIsValid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("");
        form.setSubmissionDateYear("");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }

    @Test
    void testNullMonthIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth(null);
        form.setSubmissionDateYear("2025");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.submissionDate.month.error.required}");
    }

    @Test
    void tesBlankMonthIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("");
        form.setSubmissionDateYear("2025");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.submissionDate.month.error.required}");
    }

    @Test
    void testNullYearIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear(null);

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.submissionDate.year.error.required}");
    }

    @Test
    void testBlankYearIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear("");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.submissionDate.year.error.required}");
    }

    @Test
    void testNonNumericMonthIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("abc");
        form.setSubmissionDateYear("2025");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.submissionDate.month.error.invalid}");
    }

    @Test
    void testNonNumericYearIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear("xyz");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.submissionDate.year.error.invalid}");
    }

    @Test
    void testNonNumericMonthAndYearIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("abc");
        form.setSubmissionDateYear("xyz");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext, times(2))
                .buildConstraintViolationWithTemplate("{index.submissionDate.error.invalid}");
    }

    @Test
    void testNonNumericMonthAndMissingYearIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("abc");
        form.setSubmissionDateYear("");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext, times(2))
                .buildConstraintViolationWithTemplate("{index.submissionDate.error.invalid}");
    }

    @Test
    void testMissingMonthAndNonNumericYearIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("");
        form.setSubmissionDateYear("xyz");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext, times(2))
                .buildConstraintViolationWithTemplate("{index.submissionDate.error.invalid}");
    }

    @Test
    void testOutOfRangeMonthAndValidYearIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("13");
        form.setSubmissionDateYear("2025");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.submissionDate.month.error.range}");
    }

    @Test
    void testOutOfRangeMonthAndInvalidYearIsInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("13");
        form.setSubmissionDateYear("xyz");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext, times(2))
                .buildConstraintViolationWithTemplate("{index.submissionDate.error.invalid}");
    }

    @Test
    void testValidMonthAndYearIsValid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("2");
        form.setSubmissionDateYear("2025");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }
}
