package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

public class MonetaryValueFormTest extends FormTest {

    private final String prefix = "monetaryValue";

    @Test
    void testNullValue() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(null);
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.required}", violation.getMessage());
    }
    @Test
    void testEmptyValue() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("");
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.required}", violation.getMessage());
    }

    @Test
    void testBlankValue() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(" ");
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.required}", violation.getMessage());
    }

    @Test
    void testValueLessThanMin() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("-0.01");
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.min}", violation.getMessage());
    }

    @Test
    void testValueEqualToMin() {
        String value = "0";

        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(value);
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        Assertions.assertTrue(violations.isEmpty());

        Assertions.assertEquals(value, form.getValue());
    }

    @Test
    void testValueEqualToMax() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("1000000.00");
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.max}", violation.getMessage());
    }

    @Test
    void testValueMoreThanMax() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("1000000.01");
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.max}", violation.getMessage());
    }

    @Test
    void testValueWithInvalidCharacters() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("!");
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.invalid}", violation.getMessage());
    }

    @Test
    void testValueWithMoreThan2DecimalPlaces() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("1.234");
        form.setPrefix(prefix);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.invalid}", violation.getMessage());
    }
}
