package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface ClaimDetailsView<T extends ClaimDetails> extends BaseClaimView<T> {

    default Map<String, Object> getSummaryRows() {
        Map<String, Object> rows = new LinkedHashMap<>();
        rows.put("clientName", getClientName());
        rows.put("ufn", claim().getUniqueFileNumber());
        addUcnSummaryRow(rows);
        rows.put("providerName", claim().getProviderName());
        rows.put("providerAccountNumber", claim().getProviderAccountNumber());
        rows.put("submittedDate", claim().getSubmittedDate());
        rows.put("areaOfLaw", claim().getAreaOfLaw());
        rows.put("categoryOfLaw", claim().getCategoryOfLaw());
        rows.put("feeCode", claim().getFeeCode());
        rows.put("feeCodeDescription", claim().getFeeCodeDescription());
        addPoliceStationCourtPrisonId(rows);
        addSchemeId(rows);
        addMatterTypeField(rows);
        rows.put("caseStartDate", claim().getCaseStartDate());
        rows.put("caseEndDate", claim().getCaseEndDate());
        rows.put("escaped", claim().getEscaped());
        rows.put("vatRequested", claim().getVatApplicable());
        return rows;
    }

    void addUcnSummaryRow(Map<String, Object> summaryRows);

    void addPoliceStationCourtPrisonId(Map<String, Object> summaryRows);

    void addSchemeId(Map<String, Object> summaryRows);

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

    default List<ClaimField> getAssessedTotals() {
        List<ClaimField> rows = new ArrayList<>();
        addRowIfNotNull(
            rows,
            claim().getAssessedTotalVat(),
            claim().getAssessedTotalInclVat()
        );

        return rows;
    }

    default List<ClaimField> getAllowedTotals() {
        List<ClaimField> rows = new ArrayList<>();
        addRowIfNotNull(
            rows,
            claim().getAllowedTotalVat(),
            claim().getAllowedTotalInclVat()
        );

        return rows;
    }

    default void addRowIfNotNull(List<ClaimField> list, ClaimField... claimFields) {
        for (ClaimField claimField : claimFields) {
            if (claimField != null && claimField.getStatus() != ClaimFieldStatus.DO_NOT_DISPLAY) {
                list.add(claimField);
            }
        }
    }

    default ClaimField checkSubmittedValue(ClaimField field) {
        if (field != null && field.getSubmitted() != null) {
            return field;
        }
        return null;
    }

    default ClaimField setDisplayForNulls(ClaimField field) {
        if (field != null) {
            if (field.getSubmitted() == null) {
                field.setSubmitted(BigDecimal.ZERO);
            }
            if (field.getCalculated() == null) {
                field.setCalculated(BigDecimal.ZERO);
            }
            if (field.getAssessed() == null) {
                field.setAssessed(BigDecimal.ZERO);
            }
        }
        return field;
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
        return Stream.of(claimFields(), getAssessedTotals(), getAllowedTotals())
            .flatMap(List::stream)
            .filter(ClaimField::isAssessableAndUnassessed)
            .map(f -> new ReviewAndAmendFormError(f.getId(), f.getErrorKey()))
            .toList();
    }

    default boolean hasAssessment() {
        return claim().getLastAssessment() != null && claim().isHasAssessment();
    }

    default AssessmentInfo lastAssessment() {
        return claim().getLastAssessment();
    }

    default MessageWithParams lastEditedBy(MicrosoftApiUser user) {
        LocalDateTime dateTime = lastAssessment().getLastAssessmentDate().toLocalDateTime();
        String date = DateUtils.displayDateTimeDateValue(dateTime);
        String time = DateUtils.displayDateTimeTimeValue(dateTime);
        MessageWithParams outcome = new MessageWithParams(lastAssessment().getLastAssessmentOutcome().getMessageKey());
        Object[] params = new Object[]{user.getDisplayName(), date, time, outcome};
        return new MessageWithParams("claimSummary.lastAssessmentText", params);
    }
}
