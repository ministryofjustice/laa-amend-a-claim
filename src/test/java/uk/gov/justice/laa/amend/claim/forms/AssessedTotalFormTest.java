package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class AssessedTotalFormTest extends FormTest {

    private AssessedTotalForm form;

    @BeforeEach
    void Setup(){
        form = new AssessedTotalForm();
    }

    @Test
    void testNullValue() {
        form.setAssessedTotalInclVat(null);
        form.setAssessedTotalVat(null);

        String totalInclVatViolationMessage = "{assessedTotals.assessedTotalInclVat.error.required}";
        String totalVatViolationMessage = "{assessedTotals.assessedTotalVat.error.required}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testEmptyValue() {
        form.setAssessedTotalInclVat("");
        form.setAssessedTotalVat("");

        String totalInclVatViolationMessage = "{assessedTotals.assessedTotalInclVat.error.required}";
        String totalVatViolationMessage = "{assessedTotals.assessedTotalVat.error.required}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testBlankValue() {
        form.setAssessedTotalInclVat(" ");
        form.setAssessedTotalVat(" ");

        String totalInclVatViolationMessage = "{assessedTotals.assessedTotalInclVat.error.required}";
        String totalVatViolationMessage = "{assessedTotals.assessedTotalVat.error.required}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueLessThanMin() {
        form.setAssessedTotalInclVat("-1");
        form.setAssessedTotalVat("-1");

        String totalInclVatViolationMessage = "{assessedTotals.assessedTotalInclVat.error.min}";
        String totalVatViolationMessage = "{assessedTotals.assessedTotalVat.error.min}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueEqualToMin() {
        String value = "0";
        form.setAssessedTotalInclVat("0");
        form.setAssessedTotalVat("0");

        Set<ConstraintViolation<AssessedTotalForm>> violations = validator.validate(form);

        Assertions.assertTrue(violations.isEmpty());

        Assertions.assertEquals(value, form.getAssessedTotalInclVat());
        Assertions.assertEquals(value, form.getAssessedTotalVat());
    }

    @Test
    void testValueEqualToMax() {
        form.setAssessedTotalInclVat("1000000");
        form.setAssessedTotalVat("1000000");

        String totalInclVatViolationMessage = "{assessedTotals.assessedTotalInclVat.error.max}";
        String totalVatViolationMessage = "{assessedTotals.assessedTotalVat.error.max}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueOverMax() {
        form.setAssessedTotalInclVat("1000001");
        form.setAssessedTotalVat("1000001");

        String totalInclVatViolationMessage = "{assessedTotals.assessedTotalInclVat.error.max}";
        String totalVatViolationMessage = "{assessedTotals.assessedTotalVat.error.max}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testInvalidValues() {
        form.setAssessedTotalInclVat("!!");
        form.setAssessedTotalVat("!?");

        String totalInclVatViolationMessage = "{assessedTotals.assessedTotalInclVat.error.invalid}";
        String totalVatViolationMessage = "{assessedTotals.assessedTotalVat.error.invalid}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    @Test
    void testValueWithMoreThan2DecimalPlaces() {
        form.setAssessedTotalInclVat("10.000");
        form.setAssessedTotalVat("10.000");

        String totalInclVatViolationMessage = "{assessedTotals.assessedTotalInclVat.error.invalid}";
        String totalVatViolationMessage = "{assessedTotals.assessedTotalVat.error.invalid}";

        checkViolations(totalInclVatViolationMessage, totalVatViolationMessage);
    }

    private void checkViolations(String totalInclVatViolationMessage, String totalVatViolationMessage){
        Set<ConstraintViolation<AssessedTotalForm>> violations = validator.validate(form);
        ConstraintViolation<AssessedTotalForm> totalVatViolation = getViolation(violations, "assessedTotalVat");
        ConstraintViolation<AssessedTotalForm> totalInclVatViolation = getViolation(violations, "assessedTotalInclVat");

        Assertions.assertNotNull(totalVatViolation);
        Assertions.assertNotNull(totalInclVatViolation);

        Assertions.assertEquals(totalVatViolationMessage, totalVatViolation.getMessage());
        Assertions.assertEquals(totalInclVatViolationMessage, totalInclVatViolation.getMessage());
    }
}
