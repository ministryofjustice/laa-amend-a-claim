package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
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

    @Override
    public Cost getCostForRow(ClaimField row) {
        if (row == null) return null;

        if (row.equals(claim.getTravelCosts())) return Cost.TRAVEL_COSTS;
        if (row.equals(claim.getWaitingCosts())) return Cost.WAITING_COSTS;

        // Fall back to parent implementation
        return ClaimViewModel.super.getCostForRow(row);
    }
}
