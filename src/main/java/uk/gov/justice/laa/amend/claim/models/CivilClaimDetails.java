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
        setAmendedToValue(counselsCost, new ClaimFieldValue.Value(BigDecimal.ZERO));
        setAmendedToValue(detentionTravelWaitingCosts, new ClaimFieldValue.Value(BigDecimal.ZERO));
        setAmendedToValue(jrFormFillingCost, new ClaimFieldValue.Value(BigDecimal.ZERO));
        setAmendedToValue(adjournedHearing, new ClaimFieldValue.Value(false));
        setAmendedToValue(cmrhTelephone, new ClaimFieldValue.Value(0));
        setAmendedToValue(cmrhOral, new ClaimFieldValue.Value(0));
        setAmendedToValue(hoInterview, new ClaimFieldValue.Value(0));
        setAmendedToValue(substantiveHearing, new ClaimFieldValue.Value(0));
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
