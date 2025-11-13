package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CostTest {

    @Nested
    class FromPathTests {
        @Test
        void shouldConvertProfitCosts() {
            String str = "profit-costs";
            Cost result = Cost.fromPath(str);
            Assertions.assertEquals(Cost.PROFIT_COSTS, result);
        }

        @Test
        void shouldConvertDisbursements() {
            String str = "disbursements";
            Cost result = Cost.fromPath(str);
            Assertions.assertEquals(Cost.DISBURSEMENTS, result);
        }

        @Test
        void shouldConvertDisbursementsVat() {
            String str = "disbursements-vat";
            Cost result = Cost.fromPath(str);
            Assertions.assertEquals(Cost.DISBURSEMENTS_VAT, result);
        }

        @Test
        void shouldConvertCounselCosts() {
            String str = "counsel-costs";
            Cost result = Cost.fromPath(str);
            Assertions.assertEquals(Cost.COUNSEL_COSTS, result);
        }

        @Test
        void shouldConvertDetentionTravelAndWaitingCosts() {
            String str = "detention-travel-and-waiting-costs";
            Cost result = Cost.fromPath(str);
            Assertions.assertEquals(Cost.DETENTION_TRAVEL_AND_WAITING_COSTS, result);
        }

        @Test
        void shouldConvertJrFormFillingCosts() {
            String str = "jr-form-filling-costs";
            Cost result = Cost.fromPath(str);
            Assertions.assertEquals(Cost.JR_FORM_FILLING_COSTS, result);
        }

        @Test
        void shouldConvertTravelCosts() {
            String str = "travel-costs";
            Cost result = Cost.fromPath(str);
            Assertions.assertEquals(Cost.TRAVEL_COSTS, result);
        }

        @Test
        void shouldConvertWaitingCosts() {
            String str = "waiting-costs";
            Cost result = Cost.fromPath(str);
            Assertions.assertEquals(Cost.WAITING_COSTS, result);
        }

        @Test
        void shouldNotConvertInvalidString() {
            String str = "foo";
            Assertions.assertThrows(IllegalArgumentException.class, () -> Cost.fromPath(str));
        }
    }
}
