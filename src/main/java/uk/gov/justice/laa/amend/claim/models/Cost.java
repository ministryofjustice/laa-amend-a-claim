package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

@Getter
public enum Cost {

    PROFIT_COSTS("profit-costs", "profitCosts"),
    DISBURSEMENTS("disbursements", "disbursements"),
    DISBURSEMENTS_VAT("disbursements-vat", "disbursementsVat"),
    COUNSEL_COSTS("counsel-costs", "counselCosts"),
    DETENTION_TRAVEL_AND_WAITING_COSTS("detention-travel-and-waiting-costs", "detentionTravelAndWaitingCosts"),
    JR_FORM_FILLING_COSTS("jr-form-filling-costs", "jrFormFillingCosts"),
    TRAVEL_COSTS("travel-costs", "travelCosts"),
    WAITING_COSTS("waiting-costs", "waitingCosts");

    private final String path;
    private final String prefix;

    Cost(String path, String prefix) {
        this.path = path;
        this.prefix = prefix;
    }

    public static Cost fromValue(String value) {
        return switch (value) {
            case "profit-costs" -> PROFIT_COSTS;
            case "disbursements" -> DISBURSEMENTS;
            case "disbursements-vat" -> DISBURSEMENTS_VAT;
            case "counsel-costs" -> COUNSEL_COSTS;
            case "detention-travel-and-waiting-costs" -> DETENTION_TRAVEL_AND_WAITING_COSTS;
            case "jr-form-filling-costs" -> JR_FORM_FILLING_COSTS;
            case "travel-costs" -> TRAVEL_COSTS;
            case "waiting-costs" -> WAITING_COSTS;
            default -> throw new IllegalArgumentException(String.format("Unknown cost value: %s", value));
        };
    }
}
