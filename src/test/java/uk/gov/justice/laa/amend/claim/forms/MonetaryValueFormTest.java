package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.util.Set;

public class MonetaryValueFormTest extends FormTest {

    private final Cost cost = Cost.PROFIT_COSTS;

    @Test
    void testNullValue() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(null);
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        String expectedMessage = String.format("{%s.error.required}", cost.getPrefix());
        Assertions.assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void testEmptyValue() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("");
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        String expectedMessage = String.format("{%s.error.required}", cost.getPrefix());
        Assertions.assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void testBlankValue() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(" ");
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        String expectedMessage = String.format("{%s.error.required}", cost.getPrefix());
        Assertions.assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void testValueLessThanMin() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("-0.01");
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        String expectedMessage = String.format("{%s.error.min}", cost.getPrefix());
        Assertions.assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void testValueEqualToMin() {
        String value = "0";

        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(value);
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        Assertions.assertTrue(violations.isEmpty());

        Assertions.assertEquals(value, form.getValue());
    }

    @Test
    void testValueWithComma() {
        String value = "1,000";

        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(value);
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        Assertions.assertTrue(violations.isEmpty());

        Assertions.assertEquals(value, form.getValue());
    }

    @Test
    void testValueWithInvalidCommaPlacement() {
        String value = "1,0000";

        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(value);
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        Assertions.assertTrue(violations.isEmpty());

        Assertions.assertEquals(value, form.getValue());
    }

    @Test
    void testValueEqualToMax() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("1000000.00");
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        String expectedMessage = String.format("{%s.error.max}", cost.getPrefix());
        Assertions.assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void testValueMoreThanMax() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("1000000.01");
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        String expectedMessage = String.format("{%s.error.max}", cost.getPrefix());
        Assertions.assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void testValueWithInvalidCharacters() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("!");
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        String expectedMessage = String.format("{%s.error.invalid}", cost.getPrefix());
        Assertions.assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void testValueWithMoreThan2DecimalPlaces() {
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue("1.234");
        form.setCost(cost);

        Set<ConstraintViolation<MonetaryValueForm>> violations = validator.validate(form);
        ConstraintViolation<MonetaryValueForm> violation = getViolation(violations, "value");

        Assertions.assertNotNull(violation);
        String expectedMessage = String.format("{%s.error.invalid}", cost.getPrefix());
        Assertions.assertEquals(expectedMessage, violation.getMessage());
    }
}
