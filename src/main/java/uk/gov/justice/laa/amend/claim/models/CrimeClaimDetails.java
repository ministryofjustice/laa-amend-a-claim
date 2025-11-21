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
        setAmendedToValue(travelCosts, new ClaimFieldValue.Value(BigDecimal.ZERO));
        setAmendedToValue(waitingCosts, new ClaimFieldValue.Value(BigDecimal.ZERO));
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        setAmendedToCalculated(travelCosts);
        setAmendedToCalculated(waitingCosts);
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
