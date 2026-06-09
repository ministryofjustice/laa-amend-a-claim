package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum Cost {
  PROFIT_COSTS("profit-costs", "profitCosts"),
  DISBURSEMENTS("disbursements", "disbursements"),
  DISBURSEMENTS_VAT("disbursements-vat", "disbursementsVat"),
  COUNSEL_COSTS("counsel-costs", "counselCosts"),
  DETENTION_TRAVEL_AND_WAITING_COSTS(
      "detention-travel-and-waiting-costs", "detentionTravelAndWaitingCosts"),
  JR_FORM_FILLING_COSTS("jr-form-filling-costs", "jrFormFillingCosts"),
  TRAVEL_COSTS("travel-costs", "travelCosts"),
  WAITING_COSTS("waiting-costs", "waitingCosts"),
  TRAVEL_AND_WAITING_COSTS("travel-and-waiting-costs", "travelAndWaitingCosts");

  private final String path;
  private final String prefix;

  Cost(String path, String prefix) {
    this.path = path;
    this.prefix = prefix;
  }

  public String getChangeUrl() {
    return "/submissions/%s/claims/%s/" + path;
  }

  public static Cost fromPath(String path) {
    return Arrays.stream(values())
        .filter(cost -> cost.path.equals(path))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
