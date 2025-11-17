package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PERIOD_FORMAT;

public interface ClaimViewModel<T extends Claim> {

    T claim();

    default T getClaim() {
        return claim();
    }

    /**
     * Returns the claim field rows in the order they should be displayed in the table.
     *
     * @return ordered list of claim field rows for display
     */
    default List<ClaimField> getTableRows() {
        List<ClaimField> rows = new ArrayList<>();

        addRowIfNotNull(
            rows,
            claim().getFixedFee(),
            claim().getNetProfitCost(),
            claim().getNetDisbursementAmount(),
            claim().getDisbursementVatAmount()
        );

        // Subclasses should add their specific rows here
        addClaimTypeSpecificRows(rows);

        addRowIfNotNull(
            rows,
            claim().getVatClaimed(),
            claim().getTotalAmount()
        );

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
        if (row == null) {
            return null;
        }

        Cost cost = row.getCost();
        if (cost == null) {
            return null;
        }

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

    default void addRowIfNotNull(List<ClaimField> list, ClaimField... claimFields) {
        for (ClaimField claimField : claimFields) {
            if (claimField != null) {
                list.add(claimField);
            }
        }
    }

    default String getAccountNumber() {
        return claim().getScheduleReference() != null ? claim().getScheduleReference().split("/")[0] : null;
    }

    default String getCaseStartDateForDisplay() {
        return claim().getCaseStartDate() != null ? claim().getCaseStartDate().format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    default String getCaseEndDateForDisplay() {
        return claim().getCaseEndDate() != null ? claim().getCaseEndDate().format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    default String getSubmissionPeriodForDisplay() {
        return claim().getSubmissionPeriod() != null ? claim().getSubmissionPeriod().format(DateTimeFormatter.ofPattern(DEFAULT_PERIOD_FORMAT)) : null;
    }

    default long getSubmissionPeriodForSorting() {
        return claim().getSubmissionPeriod() != null ? claim().getSubmissionPeriod().atDay(1).toEpochDay() : 0;
    }

    default String getClientName() {
        String clientForename = claim().getClientForename();
        String clientSurname = claim().getClientSurname();
        if (clientForename != null & clientSurname != null) {
            return String.format("%s %s", clientForename, clientSurname);
        } else if (clientForename != null) {
            return clientForename;
        } else {
            return clientSurname;
        }
    }
}
