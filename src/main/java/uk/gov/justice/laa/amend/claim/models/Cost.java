package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Cost {

    PROFIT_COSTS(
        "profit-costs",
        "profitCosts",
        new ClaimFieldAccessor<>(Claim2.class, Claim2::getNetProfitCost, Claim2::setNetProfitCost)
    ),

    DISBURSEMENTS(
        "disbursements",
        "disbursements",
        new ClaimFieldAccessor<>(Claim2.class, Claim2::getNetDisbursementAmount, Claim2::setNetDisbursementAmount)
    ),

    DISBURSEMENTS_VAT(
        "disbursements-vat",
        "disbursementsVat",
        new ClaimFieldAccessor<>(Claim2.class, Claim2::getDisbursementVatAmount, Claim2::setDisbursementVatAmount)
    ),

    COUNSEL_COSTS(
        "counsel-costs",
        "counselCosts",
        new ClaimFieldAccessor<>(CivilClaim.class, CivilClaim::getCounselsCost, CivilClaim::setCounselsCost)
    ),

    DETENTION_TRAVEL_AND_WAITING_COSTS(
        "detention-travel-and-waiting-costs",
        "detentionTravelAndWaitingCosts",
        new ClaimFieldAccessor<>(CivilClaim.class, CivilClaim::getDetentionTravelWaitingCosts, CivilClaim::setDetentionTravelWaitingCosts)
    ),

    JR_FORM_FILLING_COSTS(
        "jr-form-filling-costs",
        "jrFormFillingCosts",
        new ClaimFieldAccessor<>(CivilClaim.class, CivilClaim::getJrFormFillingCost, CivilClaim::setJrFormFillingCost)
    ),

    TRAVEL_COSTS(
        "travel-costs",
        "travelCosts",
        new ClaimFieldAccessor<>(CrimeClaim.class, CrimeClaim::getTravelCosts, CrimeClaim::setTravelCosts)
    ),

    WAITING_COSTS(
        "waiting-costs",
        "waitingCosts",
        new ClaimFieldAccessor<>(CrimeClaim.class, CrimeClaim::getWaitingCosts, CrimeClaim::setWaitingCosts)
    );

    private final String path;
    private final String prefix;
    private final ClaimFieldAccessor<?> accessor;

    Cost(String path, String prefix, ClaimFieldAccessor<?> accessor) {
        this.path = path;
        this.prefix = prefix;
        this.accessor = accessor;
    }

    public static Cost fromPath(String path) {
        return Arrays
            .stream(values())
            .filter(cost -> cost.path.equals(path))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
