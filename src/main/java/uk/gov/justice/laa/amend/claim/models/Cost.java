package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Cost {

    PROFIT_COSTS(
        "profit-costs",
        "profitCosts",
        new ClaimFieldAccessor<>(ClaimDetails.class, ClaimDetails::getNetProfitCost, ClaimDetails::setNetProfitCost)
    ),

    DISBURSEMENTS(
        "disbursements",
        "disbursements",
        new ClaimFieldAccessor<>(ClaimDetails.class, ClaimDetails::getNetDisbursementAmount, ClaimDetails::setNetDisbursementAmount)
    ),

    DISBURSEMENTS_VAT(
        "disbursements-vat",
        "disbursementsVat",
        new ClaimFieldAccessor<>(ClaimDetails.class, ClaimDetails::getDisbursementVatAmount, ClaimDetails::setDisbursementVatAmount)
    ),

    COUNSEL_COSTS(
        "counsel-costs",
        "counselCosts",
        new ClaimFieldAccessor<>(CivilClaimDetails.class, CivilClaimDetails::getCounselsCost, CivilClaimDetails::setCounselsCost)
    ),

    DETENTION_TRAVEL_AND_WAITING_COSTS(
        "detention-travel-and-waiting-costs",
        "detentionTravelAndWaitingCosts",
        new ClaimFieldAccessor<>(CivilClaimDetails.class, CivilClaimDetails::getDetentionTravelWaitingCosts, CivilClaimDetails::setDetentionTravelWaitingCosts)
    ),

    JR_FORM_FILLING_COSTS(
        "jr-form-filling-costs",
        "jrFormFillingCosts",
        new ClaimFieldAccessor<>(CivilClaimDetails.class, CivilClaimDetails::getJrFormFillingCost, CivilClaimDetails::setJrFormFillingCost)
    ),

    TRAVEL_COSTS(
        "travel-costs",
        "travelCosts",
        new ClaimFieldAccessor<>(CrimeClaimDetails.class, CrimeClaimDetails::getTravelCosts, CrimeClaimDetails::setTravelCosts)
    ),

    WAITING_COSTS(
        "waiting-costs",
        "waitingCosts",
        new ClaimFieldAccessor<>(CrimeClaimDetails.class, CrimeClaimDetails::getWaitingCosts, CrimeClaimDetails::setWaitingCosts)
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
