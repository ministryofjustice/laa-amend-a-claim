package uk.gov.justice.laa.amend.claim.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.models.Cost;

public class CostConverterTest {

    @Test
    void shouldConvertProfitCosts() {
        String source = "profit-costs";
        CostConverter converter = new CostConverter();
        Cost result = converter.convert(source);
        Assertions.assertEquals(Cost.PROFIT_COSTS, result);
    }

    @Test
    void shouldConvertDisbursements() {
        String source = "disbursements";
        CostConverter converter = new CostConverter();
        Cost result = converter.convert(source);
        Assertions.assertEquals(Cost.DISBURSEMENTS, result);
    }

    @Test
    void shouldConvertDisbursementsVat() {
        String source = "disbursements-vat";
        CostConverter converter = new CostConverter();
        Cost result = converter.convert(source);
        Assertions.assertEquals(Cost.DISBURSEMENTS_VAT, result);
    }

    @Test
    void shouldConvertCounselCosts() {
        String source = "counsel-costs";
        CostConverter converter = new CostConverter();
        Cost result = converter.convert(source);
        Assertions.assertEquals(Cost.COUNSEL_COSTS, result);
    }

    @Test
    void shouldConvertDetentionTravelAndWaitingCosts() {
        String source = "detention-travel-and-waiting-costs";
        CostConverter converter = new CostConverter();
        Cost result = converter.convert(source);
        Assertions.assertEquals(Cost.DETENTION_TRAVEL_AND_WAITING_COSTS, result);
    }

    @Test
    void shouldConvertJrFormFillingCosts() {
        String source = "jr-form-filling-costs";
        CostConverter converter = new CostConverter();
        Cost result = converter.convert(source);
        Assertions.assertEquals(Cost.JR_FORM_FILLING_COSTS, result);
    }

    @Test
    void shouldConvertTravelCosts() {
        String source = "travel-costs";
        CostConverter converter = new CostConverter();
        Cost result = converter.convert(source);
        Assertions.assertEquals(Cost.TRAVEL_COSTS, result);
    }

    @Test
    void shouldConvertWaitingCosts() {
        String source = "waiting-costs";
        CostConverter converter = new CostConverter();
        Cost result = converter.convert(source);
        Assertions.assertEquals(Cost.WAITING_COSTS, result);
    }

    @Test
    void shouldThrow404ExceptionWhenInvalidCost() {
        String source = "foo";
        CostConverter converter = new CostConverter();

        ResponseStatusException exception =
                Assertions.assertThrows(ResponseStatusException.class, () -> converter.convert(source));

        Assertions.assertEquals(404, exception.getStatusCode().value());
    }
}
