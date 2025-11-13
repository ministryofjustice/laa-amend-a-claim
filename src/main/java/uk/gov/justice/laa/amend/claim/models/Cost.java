package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimSummary;

import java.util.Arrays;

@Getter
public enum Cost {

    PROFIT_COSTS(
        "profit-costs",
        "profitCosts",
        new ClaimFieldAccessor<>(ClaimSummary.class, ClaimSummary::getNetProfitCost, ClaimSummary::setNetProfitCost)
    ),

    DISBURSEMENTS(
        "disbursements",
        "disbursements",
        new ClaimFieldAccessor<>(ClaimSummary.class, ClaimSummary::getNetDisbursementAmount, ClaimSummary::setNetDisbursementAmount)
    ),

    DISBURSEMENTS_VAT(
        "disbursements-vat",
        "disbursementsVat",
        new ClaimFieldAccessor<>(ClaimSummary.class, ClaimSummary::getDisbursementVatAmount, ClaimSummary::setDisbursementVatAmount)
    ),

    COUNSEL_COSTS(
        "counsel-costs",
        "counselCosts",
        new ClaimFieldAccessor<>(CivilClaimSummary.class, CivilClaimSummary::getCounselsCost, CivilClaimSummary::setCounselsCost)
    ),

    DETENTION_TRAVEL_AND_WAITING_COSTS(
        "detention-travel-and-waiting-costs",
        "detentionTravelAndWaitingCosts",
        new ClaimFieldAccessor<>(CivilClaimSummary.class, CivilClaimSummary::getDetentionTravelWaitingCosts, CivilClaimSummary::setDetentionTravelWaitingCosts)
    ),

    JR_FORM_FILLING_COSTS(
        "jr-form-filling-costs",
        "jrFormFillingCosts",
        new ClaimFieldAccessor<>(CivilClaimSummary.class, CivilClaimSummary::getJrFormFillingCost, CivilClaimSummary::setJrFormFillingCost)
    ),

    TRAVEL_COSTS(
        "travel-costs",
        "travelCosts",
        new ClaimFieldAccessor<>(CrimeClaimSummary.class, CrimeClaimSummary::getTravelCosts, CrimeClaimSummary::setTravelCosts)
    ),

    WAITING_COSTS(
        "waiting-costs",
        "waitingCosts",
        new ClaimFieldAccessor<>(CrimeClaimSummary.class, CrimeClaimSummary::getWaitingCosts, CrimeClaimSummary::setWaitingCosts)
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
