package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimDetailsView;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrimeClaimDetails extends ClaimDetails {

    private ClaimField travelCosts;
    private ClaimField waitingCosts;

    @Override
    public void setNilledValues() {
        super.setNilledValues();
        applyIfNotNull(travelCosts, cf -> cf.setNilled(BigDecimal.ZERO));
        applyIfNotNull(waitingCosts, cf -> cf.setNilled(BigDecimal.ZERO));
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        applyIfNotNull(travelCosts, ClaimField::setAmendedToCalculated);
        applyIfNotNull(waitingCosts, ClaimField::setAmendedToCalculated);
    }

    @Override
    public void setReducedValues() {
        super.setReducedValues();
        applyIfNotNull(travelCosts, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(waitingCosts, ClaimField::setAmendedToSubmitted);
    }

    @Override
    public boolean getIsCrimeClaim() {
        return true;
    }

    @Override
    public ClaimDetailsView<? extends Claim> toViewModel() {
        return new CrimeClaimDetailsView(this);
    }
}
