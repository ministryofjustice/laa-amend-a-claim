package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DateValidatorTest {

    private DateValidator validator;
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        validator = new DateValidator();
        constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        NodeBuilderCustomizableContext nodeBuilderCustomizableContext = mock(NodeBuilderCustomizableContext.class);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilderCustomizableContext);
        when(builder.addConstraintViolation()).thenReturn(constraintValidatorContext);
    }

    @Test
    void testBothNullValid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth(null);
        form.setSubmissionDateYear(null);

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }

    @Test
    void testBothBlankValid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("");
        form.setSubmissionDateYear("");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }

    @Test
    void testNullMonthInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth(null);
        form.setSubmissionDateYear("2025");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.month.error.required}");
    }

    @Test
    void tesBlankMonthInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("");
        form.setSubmissionDateYear("2025");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.month.error.required}");
    }

    @Test
    void testNullYearInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear(null);

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.year.error.required}");
    }

    @Test
    void testBlankYearInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear("");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.year.error.required}");
    }

    @Test
    void testNonNumericMonthInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("abc");
        form.setSubmissionDateYear("2025");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.month.error.invalid}");
    }

    @Test
    void testNonNumericYearInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("5");
        form.setSubmissionDateYear("xyz");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.year.error.invalid}");
    }

    @Test
    void testNonNumericMonthAndYearInvalid() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("abc");
        form.setSubmissionDateYear("xyz");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.month.error.invalid}");
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.year.error.invalid}");
    }

    @Test
    void testInvalidMonthCombination() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("13");
        form.setSubmissionDateYear("2025");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.submissionDate.month.error.range}");
    }

    @Test
    void testValidMonthYear() {
        SearchForm form = new SearchForm();
        form.setSubmissionDateMonth("2");
        form.setSubmissionDateYear("2025");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }
}
