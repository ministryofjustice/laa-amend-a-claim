package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
public enum Cost {

    PROFIT_COSTS("profit-costs", "profitCosts", Assessment::getNetProfitCostsAmount, Assessment::setNetProfitCostsAmount),
    DISBURSEMENTS("disbursements", "disbursements", Assessment::getDisbursementAmount, Assessment::setDisbursementAmount),
    DISBURSEMENTS_VAT("disbursements-vat", "disbursementsVat", Assessment::getDisbursementVatAmount, Assessment::setDisbursementVatAmount),
    COUNSEL_COSTS("counsel-costs", "counselCosts", Assessment::getNetCostOfCounselAmount, Assessment::setNetCostOfCounselAmount),
    DETENTION_TRAVEL_AND_WAITING_COSTS("detention-travel-and-waiting-costs", "detentionTravelAndWaitingCosts", Assessment::getTravelAndWaitingCostsAmount, Assessment::setTravelAndWaitingCostsAmount),
    JR_FORM_FILLING_COSTS("jr-form-filling-costs", "jrFormFillingCosts", Assessment::getJrFormFillingAmount, Assessment::setJrFormFillingAmount),
    TRAVEL_COSTS("travel-costs", "travelCosts", Assessment::getNetTravelCostsAmount, Assessment::setNetTravelCostsAmount),
    WAITING_COSTS("waiting-costs", "waitingCosts", Assessment::getNetWaitingCostsAmount, Assessment::setNetWaitingCostsAmount);

    private final String path;
    private final String prefix;
    private final Function<Assessment, BigDecimal> getter;
    private final BiConsumer<Assessment, BigDecimal> setter;

    Cost(String path, String prefix, Function<Assessment, BigDecimal> getter, BiConsumer<Assessment, BigDecimal> setter) {
        this.path = path;
        this.prefix = prefix;
        this.getter = getter;
        this.setter = setter;
    }

    public static Cost fromPath(String path) {
        return Arrays
            .stream(values())
            .filter(cost -> cost.path.equals(path))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
