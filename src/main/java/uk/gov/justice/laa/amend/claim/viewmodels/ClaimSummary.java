package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Data;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.justice.laa.amend.claim.utils.FormUtils.getClientFullName;

@Data
public class ClaimSummary implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String submissionId;
    private String claimId;
    private String uniqueFileNumber;
    private String caseReferenceNumber;
    private String clientSurname;
    private String clientForename;
    private String submittedDate;
    private LocalDate caseStartDate;
    private LocalDate caseEndDate;
    private String feeScheme;
    private String categoryOfLaw;
    private String providerName;
    private Boolean escaped;
    private Boolean vatApplicable;
    private String providerAccountNumber;
    private ClaimFieldRow vatClaimed;
    private ClaimFieldRow fixedFee;
    private ClaimFieldRow netProfitCost;
    private ClaimFieldRow netDisbursementAmount;
    private ClaimFieldRow totalAmount;
    private ClaimFieldRow disbursementVatAmount;
    private OutcomeType assessmentOutcome;


    public String getClientName() {
        return getClientFullName(clientForename, clientSurname);
    }

    /**
     * Returns the claim field rows in the order they should be displayed in the table.
     *
     * @return ordered list of claim field rows for display
     */
    public List<ClaimFieldRow> getTableRows() {
        List<ClaimFieldRow> rows = new ArrayList<>();

        addRowIfNotNull(rows, fixedFee, netProfitCost, netDisbursementAmount, disbursementVatAmount);

        // Subclasses should add their specific rows here
        addClaimTypeSpecificRows(rows);

        addRowIfNotNull(rows, vatClaimed, totalAmount);

        return rows;
    }

    /**
     * Override in subclasses to add claim-type specific rows.
     *
     * @param rows the list to add rows to
     */
    protected void addClaimTypeSpecificRows(List<ClaimFieldRow> rows) {
        // Base implementation does nothing
    }

    /**
     * Returns the change URL for a given claim field row, or null if the row is not editable.
     *
     * @param row the claim field row
     * @return the change URL, or null if not editable
     */
    public String getChangeUrl(ClaimFieldRow row) {
        if (row == null) return null;

        Cost cost = getCostForRow(row);
        if (cost == null) return null;

        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost.getPath());
    }

    /**
     * Determines if a given row represents the total row.
     *
     * @param row the claim field row
     * @return true if this is the total row
     */
    public boolean isTotalRow(ClaimFieldRow row) {
        return row != null && row.equals(totalAmount);
    }

    /**
     * Maps a ClaimFieldRow to its corresponding Cost enum, or null if the row is not editable.
     *
     * @param row the claim field row
     * @return the corresponding Cost, or null if not editable
     */
    protected Cost getCostForRow(ClaimFieldRow row) {
        if (row == null) return null;

        if (row.equals(netProfitCost)) return Cost.PROFIT_COSTS;
        if (row.equals(netDisbursementAmount)) return Cost.DISBURSEMENTS;
        if (row.equals(disbursementVatAmount)) return Cost.DISBURSEMENTS_VAT;

        // Subclasses should override to add their specific costs
        return null;
    }

    protected void addRowIfNotNull(List<ClaimFieldRow> list, ClaimFieldRow... rows) {
        for (ClaimFieldRow row : rows) {
            if (row != null) list.add(row);
        }
    }
}
