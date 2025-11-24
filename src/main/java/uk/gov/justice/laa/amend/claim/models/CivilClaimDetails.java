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
        applyIfNotNull(counselsCost, cf -> cf.setNilled(BigDecimal.ZERO));
        applyIfNotNull(detentionTravelWaitingCosts, cf -> cf.setNilled(BigDecimal.ZERO));
        applyIfNotNull(jrFormFillingCost, cf -> cf.setNilled(BigDecimal.ZERO));
        applyIfNotNull(adjournedHearing, cf -> cf.setNilled(false));
        applyIfNotNull(cmrhTelephone, cf -> cf.setNilled(0));
        applyIfNotNull(cmrhOral, cf -> cf.setNilled(0));
        applyIfNotNull(hoInterview, cf -> cf.setNilled(0));
        applyIfNotNull(substantiveHearing, cf -> cf.setNilled(0));
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAmendedToCalculated);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAmendedToCalculated);
        applyIfNotNull(adjournedHearing, ClaimField::setAmendedToCalculated);
        applyIfNotNull(cmrhTelephone, ClaimField::setAmendedToCalculated);
        applyIfNotNull(cmrhOral, ClaimField::setAmendedToCalculated);
        applyIfNotNull(hoInterview, ClaimField::setAmendedToCalculated);
        applyIfNotNull(substantiveHearing, ClaimField::setAmendedToCalculated);
        applyIfNotNull(counselsCost, ClaimField::setAmendedToCalculated);
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
