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
        setAmendedValue(counselsCost, BigDecimal.ZERO);
        setAmendedValue(detentionTravelWaitingCosts, BigDecimal.ZERO);
        setAmendedValue(jrFormFillingCost, BigDecimal.ZERO);
        setAmendedValue(adjournedHearing, false);
        setAmendedValue(cmrhTelephone, 0);
        setAmendedValue(cmrhOral, 0);
        setAmendedValue(hoInterview, 0);
        setAmendedValue(substantiveHearing, 0);
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
