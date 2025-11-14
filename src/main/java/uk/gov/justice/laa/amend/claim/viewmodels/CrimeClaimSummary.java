package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrimeClaimSummary extends ClaimSummary {
    private String matterTypeCode;
    private ClaimFieldRow travelCosts;
    private ClaimFieldRow waitingCosts;

    @Override
    protected void addClaimTypeSpecificRows(List<ClaimFieldRow> rows) {
        addRowIfNotNull(rows, travelCosts, waitingCosts);
    }

    @Override
    protected Cost getCostForRow(ClaimFieldRow row) {
        if (row == null) return null;

        if (row.equals(travelCosts)) return Cost.TRAVEL_COSTS;
        if (row.equals(waitingCosts)) return Cost.WAITING_COSTS;

        // Fall back to parent implementation
        return super.getCostForRow(row);
    }
}
