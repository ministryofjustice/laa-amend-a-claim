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

class OfficeCodeValidatorTest {

    private OfficeCodeValidator validator;
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        validator = new OfficeCodeValidator();
        constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        NodeBuilderCustomizableContext nodeBuilderCustomizableContext = mock(NodeBuilderCustomizableContext.class);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilderCustomizableContext);
        when(builder.addConstraintViolation()).thenReturn(constraintValidatorContext);
    }

    @Test
    void testNullIsInvalid() {
        SearchForm form = new SearchForm();
        form.setOfficeCode(null);

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.officeCode.error.required}");
    }

    @Test
    void testBlankIsInvalid() {
        SearchForm form = new SearchForm();
        form.setOfficeCode("");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.officeCode.error.required}");
    }

    @Test
    void testFewerThanSixCharactersIsInvalid() {
        SearchForm form = new SearchForm();
        form.setOfficeCode("12345");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.officeCode.error.format}");
    }

    @Test
    void testMoreThanSixCharactersIsInvalid() {
        SearchForm form = new SearchForm();
        form.setOfficeCode("1234567");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.officeCode.error.format}");
    }

    @Test
    void testInvalidCharactersIsInvalid() {
        SearchForm form = new SearchForm();
        form.setOfficeCode("!!!!!!");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate("{index.officeCode.error.invalid}");
    }

    @Test
    void testValidInputIsValid() {
        SearchForm form = new SearchForm();
        form.setOfficeCode("123456");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }
}
