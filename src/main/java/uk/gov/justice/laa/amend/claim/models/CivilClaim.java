package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimViewModel;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimViewModel;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class CivilClaim extends Claim2 {

    private String matterTypeCodeOne;
    private String matterTypeCodeTwo;
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
        this.counselsCost.setAmended(BigDecimal.ZERO);
        this.detentionTravelWaitingCosts.setAmended(BigDecimal.ZERO);
        this.jrFormFillingCost.setAmended(BigDecimal.ZERO);
        this.adjournedHearing.setAmended(false);
        this.cmrhTelephone.setAmended(0);
        this.cmrhOral.setAmended(0);
        this.hoInterview.setAmended(0);
        this.substantiveHearing.setAmended(0);
    }

    @Override
    public ClaimViewModel<? extends Claim2> toViewModel() {
        return new CivilClaimViewModel(this);
    }
}
