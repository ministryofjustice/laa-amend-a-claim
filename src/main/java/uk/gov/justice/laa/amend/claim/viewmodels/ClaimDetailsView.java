package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface ClaimDetailsView<T extends ClaimDetails> extends BaseClaimView<T> {

    // 'Summary' rows for the 'Claim details' page
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
        addPoliceStationCourtPrisonIdRow(rows);
        addSchemeIdRow(rows);
        addMatterTypeCodeRow(rows);
        rows.put("caseStartDate", claim().getCaseStartDate());
        rows.put("caseEndDate", claim().getCaseEndDate());
        rows.put("escaped", claim().getEscaped());
        rows.put("vatRequested", claim().getVatApplicable());
        return rows;
    }

    void addUcnSummaryRow(Map<String, Object> summaryRows);

    void addPoliceStationCourtPrisonIdRow(Map<String, Object> summaryRows);

    void addSchemeIdRow(Map<String, Object> summaryRows);

    void addMatterTypeCodeRow(Map<String, Object> summaryRows);

    // 'Values' rows for the 'Claim details' page
    default List<ClaimField> getSummaryClaimFieldRows() {
        List<ClaimField> rows = claimFieldsWithBoltOns();
        addRowIfNotNull(
            rows,
            claim().getVatClaimed(),
            claim().isHasAssessment() ? null : claim().getTotalAmount()
        );
        return rows;
    }

    // 'Claim costs' rows for the 'Review and amend' page
    default List<ClaimField> getReviewClaimFieldRows() {
        List<ClaimField> rows = claimFieldsWithBoltOns();
        addRowIfNotNull(
            rows,
            claim().getVatClaimed()
        );
        return rows;
    }

    // 'Total claim value' rows for the 'Review and amend' page
    default List<ClaimField> getAssessedTotals() {
        List<ClaimField> rows = new ArrayList<>();
        addRowIfNotNull(
            rows,
            resolveValue(claim().getAssessedTotalVat(), claim().getAllowedTotalVat()),
            resolveValue(claim().getAssessedTotalInclVat(), claim().getAllowedTotalInclVat())
        );

        return rows;
    }

    private ClaimField resolveValue(ClaimField assessed, ClaimField allowed) {
        if (assessed != null && allowed != null && assessed.isNotAssessable()) {
            assessed.setAssessed(allowed.getAssessed());
        }
        return assessed;
    }

    // 'Total allowed value' rows for the 'Review and amend' page
    default List<ClaimField> getAllowedTotals() {
        List<ClaimField> rows = new ArrayList<>();
        addRowIfNotNull(
            rows,
            setCalculatedDisplayForNulls(claim().getAllowedTotalVat()),
            setCalculatedDisplayForNulls(claim().getAllowedTotalInclVat())
        );

        return rows;
    }

    default void addRowIfNotNull(List<ClaimField> list, ClaimField... claimFields) {
        for (ClaimField claimField : claimFields) {
            if (claimField != null) {
                list.add(claimField);
            }
        }
    }

    default ClaimField checkSubmittedValue(ClaimField field) {
        if (field != null && field.hasSubmittedValue()) {
            return field;
        }
        return null;
    }

    default ClaimField setDisplayForNulls(ClaimField field) {
        if (field != null) {
            field.setSubmittedForDisplay();
            field.setCalculatedForDisplay();
            field.setAssessedForDisplay();
        }
        return field;
    }

    private ClaimField setCalculatedDisplayForNulls(ClaimField field) {
        if (field != null) {
            field.setCalculatedForDisplay();
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

    List<ClaimField> claimFieldsWithBoltOns();

    default List<ReviewAndAmendFormError> getErrors() {
        return Stream.of(claimFields(), getAssessedTotals(), getAllowedTotals())
            .flatMap(List::stream)
            .filter(ClaimField::isAssessableAndUnassessed)
            .map(f -> new ReviewAndAmendFormError(f.getId(), f.getErrorKey()))
            .toList();
    }

    default boolean hasAssessment() {
        return claim().isHasAssessment() && lastAssessment() != null;
    }

    default AssessmentInfo lastAssessment() {
        return claim().getLastAssessment();
    }

    default ThymeleafMessage lastEditedBy(MicrosoftApiUser user) {
        LocalDateTime dateTime = lastAssessment().getLastAssessmentDate().toLocalDateTime();
        String date = DateUtils.displayDateTimeDateValue(dateTime);
        String time = DateUtils.displayDateTimeTimeValue(dateTime);
        ThymeleafMessage outcome = new ThymeleafMessage(lastAssessment().getLastAssessmentOutcome().getMessageKey());
        if (user != null) {
            return new ThymeleafMessage("claimSummary.lastAssessmentText", user.getDisplayName(), date, time, outcome);
        } else {
            return new ThymeleafMessage("claimSummary.lastAssessmentText.noUser", date, time, outcome);
        }
    }
}
