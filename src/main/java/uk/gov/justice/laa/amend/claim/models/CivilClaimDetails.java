package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class CivilClaimDetails extends ClaimDetails {

    private ClaimField detentionTravelWaitingCosts;
    private ClaimField jrFormFillingCost;
    private ClaimField adjournedHearing;
    private ClaimField cmrhTelephone;
    private ClaimField cmrhOral;
    private ClaimField hoInterview;
    private ClaimField substantiveHearing;
    private ClaimField counselsCost;

    @Override
    public void setNilledValues() {
        super.setNilledValues();
        setAmendedToValue(counselsCost, ClaimFieldValue.of(BigDecimal.ZERO));
        setAmendedToValue(detentionTravelWaitingCosts, ClaimFieldValue.of(BigDecimal.ZERO));
        setAmendedToValue(jrFormFillingCost, ClaimFieldValue.of(BigDecimal.ZERO));
        setAmendedToValue(adjournedHearing, ClaimFieldValue.of(false));
        setAmendedToValue(cmrhTelephone, ClaimFieldValue.of(0));
        setAmendedToValue(cmrhOral, ClaimFieldValue.of(0));
        setAmendedToValue(hoInterview, ClaimFieldValue.of(0));
        setAmendedToValue(substantiveHearing, ClaimFieldValue.of(0));
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        setAmendedToCalculated(detentionTravelWaitingCosts);
        setAmendedToCalculated(jrFormFillingCost);
        setAmendedToCalculated(adjournedHearing);
        setAmendedToCalculated(cmrhTelephone);
        setAmendedToCalculated(cmrhOral);
        setAmendedToCalculated(hoInterview);
        setAmendedToCalculated(substantiveHearing);
        setAmendedToCalculated(counselsCost);
    }

    @Override
    public boolean getIsCrimeClaim() {
        return false;
    }

    @Override
    public ClaimDetailsView<? extends Claim> toViewModel() {
        return new CivilClaimDetailsView(this);
    }
}
