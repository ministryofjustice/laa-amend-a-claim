package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

public class MonetaryValueFormTest extends FormTest {

    @Test
    void testMissingValue() {
        MonetaryValueForm form = new MonetaryValueForm();

        form.setValue(null);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.required}", violation.getMessage());
    }

    @Test
    void testValueLessThanMin() {
        MonetaryValueForm form = new MonetaryValueForm();

        form.setValue(BigDecimal.valueOf(-0.01));

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.min}", violation.getMessage());
    }

    @Test
    void testValueEqualToMin() {
        MonetaryValueForm form = new MonetaryValueForm();

        BigDecimal value = BigDecimal.valueOf(0);

        form.setValue(value);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        Assertions.assertTrue(violations.isEmpty());

        Assertions.assertEquals(form.getValue(), value);
    }

    @Test
    void testValueEqualToMax() {
        MonetaryValueForm form = new MonetaryValueForm();

        form.setValue(BigDecimal.valueOf(1000000.00));

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.max}", violation.getMessage());
    }

    @Test
    void testValueMoreThanMax() {
        MonetaryValueForm form = new MonetaryValueForm();

        form.setValue(BigDecimal.valueOf(1000000.01));

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        Assertions.assertEquals("{monetaryValue.error.max}", violation.getMessage());
    }
}
