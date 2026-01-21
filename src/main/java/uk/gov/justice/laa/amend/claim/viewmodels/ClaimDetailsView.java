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
    default List<ClaimFieldRow> getSummaryClaimFieldRows() {
        List<ClaimField> rows = claimFields();
        addRowIfNotNull(
            rows,
            claim().getVatClaimed(),
            claim().isHasAssessment() ? null : claim().getTotalAmount()
        );
        return rows.stream().map(ClaimField::toClaimFieldRow).toList();
    }

    // 'Claim costs' rows for the 'Review and amend' page
    default List<ClaimFieldRow> getReviewClaimFieldRows() {
        List<ClaimField> rows = claimFields();
        addRowIfNotNull(
            rows,
            claim().getVatClaimed()
        );
        return rows.stream().map(ClaimField::toClaimFieldRow).toList();
    }

    // 'Total claim value' rows for the 'Review and amend' page
    default List<ClaimFieldRow> getAssessedTotals() {
        List<ClaimField> rows = new ArrayList<>();
        addRowIfNotNull(
            rows,
            claim().getAssessedTotalVat(),
            claim().getAssessedTotalInclVat()
        );

        return rows.stream().map(ClaimField::toClaimFieldRow).toList();
    }

    // 'Total allowed value' rows for the 'Review and amend' page
    default List<ClaimFieldRow> getAllowedTotals() {
        List<ClaimField> rows = new ArrayList<>();
        addRowIfNotNull(
            rows,
            claim().getAllowedTotalVat(),
            claim().getAllowedTotalInclVat()
        );

        return rows.stream().map(ClaimField::toClaimFieldRow).toList();
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
        return Stream.of(
                claimFields(),
                claim().getAssessedTotalFields().toList(),
                claim().getAllowedTotalFields().toList()
            )
            .flatMap(List::stream)
            .map(ClaimField::toClaimFieldRow)
            .filter(ClaimFieldRow::isAssessableAndUnassessed)
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
