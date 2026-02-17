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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;

class UniqueFileNumberValidatorTest {

    private UniqueFileNumberValidator validator;
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        validator = new UniqueFileNumberValidator();
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
        form.setUniqueFileNumber(null);

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }

    @Test
    void testBlankIsValid() {
        SearchForm form = new SearchForm();
        form.setUniqueFileNumber("");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "123/456789", "1/2/3"})
    void testInvalidFormatIsInvalid(String value) {
        SearchForm form = new SearchForm();
        form.setUniqueFileNumber(value);

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.uniqueFileNumber.error.format}");
    }

    @Test
    void testInvalidCharactersIsInvalid() {
        SearchForm form = new SearchForm();
        form.setUniqueFileNumber("!!!!!!/!!!");

        assertFalse(validator.isValid(form, constraintValidatorContext));
        verify(constraintValidatorContext)
                .buildConstraintViolationWithTemplate("{index.uniqueFileNumber.error.invalid}");
    }

    @Test
    void testValidInputIsValid() {
        SearchForm form = new SearchForm();
        form.setUniqueFileNumber("123456/789");

        assertTrue(validator.isValid(form, constraintValidatorContext));
    }
}
