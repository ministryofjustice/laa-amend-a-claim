package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Assessment implements Serializable {

    public Assessment(ClaimResponse claim) {
        this.netProfitCostsAmount = claim.getNetProfitCostsAmount();
        this.disbursementAmount = claim.getNetDisbursementAmount();
        this.disbursementVatAmount = claim.getDisbursementsVatAmount();
        this.netCostOfCounselAmount = claim.getNetCounselCostsAmount();
        this.travelAndWaitingCostsAmount = claim.getTravelWaitingCostsAmount();
        this.jrFormFillingAmount = claim.getJrFormFillingAmount();
        this.netTravelCostsAmount = claim.getFeeCalculationResponse() != null ? claim.getFeeCalculationResponse().getNetTravelCostsAmount() : null;
        this.netWaitingCostsAmount = claim.getFeeCalculationResponse() != null ? claim.getFeeCalculationResponse().getNetWaitingCostsAmount() : null;
    }

    private BigDecimal netProfitCostsAmount;
    private BigDecimal disbursementAmount;
    private BigDecimal disbursementVatAmount;
    private BigDecimal netCostOfCounselAmount;
    private BigDecimal travelAndWaitingCostsAmount;
    private BigDecimal jrFormFillingAmount;
    private BigDecimal netTravelCostsAmount;
    private BigDecimal netWaitingCostsAmount;
}
