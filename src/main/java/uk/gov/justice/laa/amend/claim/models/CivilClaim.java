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
        if (counselsCost != null) {
            counselsCost.setAmended(BigDecimal.ZERO);
        }
        if (detentionTravelWaitingCosts != null) {
            detentionTravelWaitingCosts.setAmended(BigDecimal.ZERO);
        }
        if (jrFormFillingCost != null) {
            jrFormFillingCost.setAmended(BigDecimal.ZERO);
        }
        if (adjournedHearing != null) {
            adjournedHearing.setAmended(false);
        }
        if (cmrhTelephone != null) {
            cmrhTelephone.setAmended(0);
        }
        if (cmrhOral != null) {
            cmrhOral.setAmended(0);
        }
        if (hoInterview != null) {
            hoInterview.setAmended(0);
        }
        if (substantiveHearing != null) {
            substantiveHearing.setAmended(0);
        }
    }

    @Override
    public ClaimViewModel<? extends Claim2> toViewModel() {
        return new CivilClaimViewModel(this);
    }
}
