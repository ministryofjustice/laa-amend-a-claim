package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimViewModel;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimViewModel;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrimeClaim extends Claim {

    private String matterTypeCode;
    private ClaimField travelCosts;
    private ClaimField waitingCosts;

    @Override
    public void setNilledValues() {
        super.setNilledValues();
        if (travelCosts != null) {
            travelCosts.setAmended(BigDecimal.ZERO);
        }
        if (waitingCosts != null) {
            waitingCosts.setAmended(BigDecimal.ZERO);
        }
    }

    @Override
    public ClaimViewModel<? extends Claim> toViewModel() {
        return new CrimeClaimViewModel(this);
    }
}
