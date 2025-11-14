package uk.gov.justice.laa.amend.claim.viewmodels;



import uk.gov.justice.laa.amend.claim.models.Claim2;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.util.ArrayList;
import java.util.List;

public interface ClaimViewModel<T extends Claim2> {

    T claim();

    /**
     * Returns the claim field rows in the order they should be displayed in the table.
     *
     * @return ordered list of claim field rows for display
     */
    default List<ClaimField> getTableRows() {
        List<ClaimField> rows = new ArrayList<>();

        if (claim().getFixedFee() != null) rows.add(claim().getFixedFee());
        if (claim().getNetProfitCost() != null) rows.add(claim().getNetProfitCost());
        if (claim().getNetDisbursementAmount() != null) rows.add(claim().getNetDisbursementAmount());
        if (claim().getDisbursementVatAmount() != null) rows.add(claim().getDisbursementVatAmount());

        // Subclasses should add their specific rows here
        addClaimTypeSpecificRows(rows);

        if (claim().getVatClaimed() != null) rows.add(claim().getVatClaimed());
        if (claim().getTotalAmount() != null) rows.add(claim().getTotalAmount());

        return rows;
    }

    /**
     * Override in subclasses to add claim-type specific rows.
     *
     * @param rows the list to add rows to
     */
    void addClaimTypeSpecificRows(List<ClaimField> rows);

    /**
     * Returns the change URL for a given claim field row, or null if the row is not editable.
     *
     * @param row the claim field row
     * @return the change URL, or null if not editable
     */
    default String getChangeUrl(ClaimField row) {
        if (row == null) return null;

        Cost cost = getCostForRow(row);
        if (cost == null) return null;

        return String.format("/submissions/%s/claims/%s/%s", claim().getSubmissionId(), claim().getClaimId(), cost.getPath());
    }

    /**
     * Determines if a given row represents the total row.
     *
     * @param row the claim field row
     * @return true if this is the total row
     */
    default boolean isTotalRow(ClaimField row) {
        return row != null && row.equals(claim().getTotalAmount());
    }

    /**
     * Maps a ClaimField to its corresponding Cost enum, or null if the row is not editable.
     *
     * @param row the claim field row
     * @return the corresponding Cost, or null if not editable
     */
    default Cost getCostForRow(ClaimField row) {
        if (row == null) return null;

        if (row.equals(claim().getNetProfitCost())) return Cost.PROFIT_COSTS;
        if (row.equals(claim().getNetDisbursementAmount())) return Cost.DISBURSEMENTS;
        if (row.equals(claim().getDisbursementVatAmount())) return Cost.DISBURSEMENTS_VAT;

        // Subclasses should override to add their specific costs
        return null;
    }
}
