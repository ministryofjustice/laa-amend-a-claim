package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaim;

import java.util.List;

public record CrimeClaimViewModel(CrimeClaim claim) implements ClaimViewModel<CrimeClaim> {

    @Override
    public void addClaimTypeSpecificRows(List<ClaimField> rows) {
        addRowIfNotNull(
            rows,
            claim.getTravelCosts(),
            claim.getWaitingCosts()
        );
    }
}
