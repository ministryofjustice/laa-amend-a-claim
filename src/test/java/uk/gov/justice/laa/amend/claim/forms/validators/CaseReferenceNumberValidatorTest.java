package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;

class CaseReferenceNumberValidatorTest {

    private CaseReferenceNumberValidator validator;
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        validator = new CaseReferenceNumberValidator();
        constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        NodeBuilderCustomizableContext nodeBuilderCustomizableContext = mock(NodeBuilderCustomizableContext.class);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilderCustomizableContext);
        when(builder.addConstraintViolation()).thenReturn(constraintValidatorContext);
    }

    @Test
    void testNullIsValid() {
        SearchForm form = new SearchForm();
        form.setCaseReferenceNumber(null);

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }

    @Test
    void testBlankIsValid() {
        SearchForm form = new SearchForm();
        form.setCaseReferenceNumber("");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }

    @Test
    void testMoreThanThirtyCharactersIsInvalid() {
        SearchForm form = new SearchForm();
        form.setCaseReferenceNumber("1234567890123456789012345678901");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.caseReferenceNumber.error.format}");
    }

    @Test
    void testInvalidCharactersIsInvalid() {
        SearchForm form = new SearchForm();
        form.setCaseReferenceNumber("!");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.caseReferenceNumber.error.invalid}");
    }

    @Test
    void testValidInputIsValid() {
        SearchForm form = new SearchForm();
        form.setCaseReferenceNumber("123456");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }
}
