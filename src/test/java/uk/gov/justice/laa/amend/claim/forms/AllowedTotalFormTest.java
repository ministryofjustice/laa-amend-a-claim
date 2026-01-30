package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class AllowedTotalFormTest extends FormTest {

    private AllowedTotalForm form;

    @BeforeEach
    void Setup(){
        form = new AllowedTotalForm();
    }

    @Test
    void testNullValue() {
        form.setAllowedTotalInclVat(null);
        form.setAllowedTotalVat(null);

        String totalInclVatViolationMessage = "{allowedTotals.allowedTotalInclVat.error.required}";
        String totalVatViolationMessage = "{allowedTotals.allowedTotalVat.error.required}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testEmptyValue() {
        form.setAllowedTotalInclVat("");
        form.setAllowedTotalVat("");

        String totalInclVatViolationMessage = "{allowedTotals.allowedTotalInclVat.error.required}";
        String totalVatViolationMessage = "{allowedTotals.allowedTotalVat.error.required}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testBlankValue() {
        form.setAllowedTotalInclVat(" ");
        form.setAllowedTotalVat(" ");

        String totalInclVatViolationMessage = "{allowedTotals.allowedTotalInclVat.error.required}";
        String totalVatViolationMessage = "{allowedTotals.allowedTotalVat.error.required}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueLessThanMin() {
        form.setAllowedTotalInclVat("-1");
        form.setAllowedTotalVat("-1");

        String totalInclVatViolationMessage = "{allowedTotals.allowedTotalInclVat.error.min}";
        String totalVatViolationMessage = "{allowedTotals.allowedTotalVat.error.min}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueEqualToMin() {
        String value = "0";
        form.setAllowedTotalInclVat("0");
        form.setAllowedTotalVat("0");

        Set<ConstraintViolation<AllowedTotalForm>> violations = validator.validate(form);

        Assertions.assertTrue(violations.isEmpty());

        Assertions.assertEquals(value, form.getAllowedTotalInclVat());
        Assertions.assertEquals(value, form.getAllowedTotalVat());
    }

    @Test
    void testValueEqualToMax() {
        form.setAllowedTotalInclVat("1000000");
        form.setAllowedTotalVat("1000000");

        String totalInclVatViolationMessage = "{allowedTotals.allowedTotalInclVat.error.max}";
        String totalVatViolationMessage = "{allowedTotals.allowedTotalVat.error.max}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueOverMax() {
        form.setAllowedTotalInclVat("1000001");
        form.setAllowedTotalVat("1000001");

        String totalInclVatViolationMessage = "{allowedTotals.allowedTotalInclVat.error.max}";
        String totalVatViolationMessage = "{allowedTotals.allowedTotalVat.error.max}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testInvalidValues() {
        form.setAllowedTotalInclVat("!!");
        form.setAllowedTotalVat("!?");

        String totalInclVatViolationMessage = "{allowedTotals.allowedTotalInclVat.error.invalid}";
        String totalVatViolationMessage = "{allowedTotals.allowedTotalVat.error.invalid}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueWithMoreThan2DecimalPlaces() {
        form.setAllowedTotalInclVat("10.000");
        form.setAllowedTotalVat("10.000");

        String totalInclVatViolationMessage = "{allowedTotals.allowedTotalInclVat.error.invalid}";
        String totalVatViolationMessage = "{allowedTotals.allowedTotalVat.error.invalid}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueWithComma() {
        form.setAllowedTotalInclVat("1,000.00");
        form.setAllowedTotalVat("1,000.00");

        checkNoViolations(form);
    }

    @Test
    void testValueWithInvalidCommaPlacement() {
        form.setAllowedTotalInclVat("1,0000.00");
        form.setAllowedTotalVat("1,0000.00");

        checkNoViolations(form);
    }

    @Test
    void testValueWithNoCommas() {
        form.setAllowedTotalInclVat("10000");
        form.setAllowedTotalVat("10000");

        checkNoViolations(form);
    }

    private void checkViolations(String totalInclVatViolationMessage, String totalVatViolationMessage){
        Set<ConstraintViolation<AllowedTotalForm>> violations = validator.validate(form);
        ConstraintViolation<AllowedTotalForm> totalVatViolation = getViolation(violations, "allowedTotalVat");
        ConstraintViolation<AllowedTotalForm> totalInclVatViolation = getViolation(violations, "allowedTotalInclVat");

        Assertions.assertNotNull(totalVatViolation);
        Assertions.assertNotNull(totalInclVatViolation);

        Assertions.assertEquals(totalVatViolationMessage, totalVatViolation.getMessage());
        Assertions.assertEquals(totalInclVatViolationMessage, totalInclVatViolation.getMessage());
    }
}
