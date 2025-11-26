package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.justice.laa.amend.claim.utils.DateUtils.displayDateValue;

public interface ClaimDetailsView<T extends ClaimDetails> extends BaseClaimView<T> {

    default Map<String, Object> getSummaryRows() {
        Map<String, Object> rows = new LinkedHashMap<>();
        rows.put("clientName", getClientName());
        rows.put("ufn", claim().getUniqueFileNumber());
        addUcnSummaryRow(rows);
        rows.put("submittedDate", displayDateValue(claim().getSubmittedDate()));
        rows.put("providerAccountNumber", claim().getProviderAccountNumber());
        rows.put("areaOfLaw", claim().getAreaOfLaw());
        rows.put("categoryOfLaw", claim().getCategoryOfLaw());
        rows.put("feeScheme", claim().getFeeScheme());
        addMatterTypeField(rows);
        rows.put("caseStartDate", displayDateValue(claim().getCaseStartDate()));
        rows.put("caseEndDate", displayDateValue(claim().getCaseEndDate()));
        rows.put("escaped", claim().getEscaped());
        rows.put("vatRequested", claim().getVatApplicable());
        return rows;
    }

    void addUcnSummaryRow(Map<String, Object> summaryRows);

    void addMatterTypeField(Map<String, Object> summaryRows);


    /**
     * Returns the claim field rows in the order they should be displayed in the table.
     *
     * @return ordered list of claim field rows for display
     */
    default List<ClaimField> getTableRows() {
        List<ClaimField> rows = claimFields();
        addRowIfNotNull(
            rows,
            claim().getVatClaimed(),
            claim().getTotalAmount()
        );

        return rows;
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

    default List<ClaimField> claimFields() {
        List<ClaimField> fields = new ArrayList<>();
        addRowIfNotNull(
            fields,
            claim().getFixedFee(),
            claim().getNetProfitCost(),
            claim().getNetDisbursementAmount(),
            claim().getDisbursementVatAmount()
        );
        return fields;
    }

    default List<ReviewAndAmendFormError> getErrors() {
        return claimFields()
            .stream()
            .filter(ClaimField::needsAmending)
            .map(x -> new ReviewAndAmendFormError(x.getId(), x.getErrorKey()))
            .toList();
    }
}
