package uk.gov.justice.laa.amend.claim.viewmodels;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.utils.DateUtils;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

public interface ClaimDetailsView<T extends ClaimDetails> extends BaseClaimView<T> {

    // 'Summary' rows for the 'Claim details' page
    default Map<String, Object> getSummaryRows() {
        var rows = new LinkedHashMap<String, Object>();
        rows.put("clientName", getClientName());
        rows.put("ufn", claim().getUniqueFileNumber());
        addUcnSummaryRow(rows);
        rows.put("providerName", getProviderName());
        rows.put("officeCode", claim().getOfficeCode());
        rows.put("submittedDate", claim().getSubmittedDate());
        rows.put(
                "areaOfLaw",
                claim().getAreaOfLaw() != null
                        ? new ThymeleafMessage(claim().getAreaOfLaw().getMessageKey())
                        : null);
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

    default Map<String, Object> getVoidConfirmationRows() {
        var rows = new LinkedHashMap<String, Object>();
        rows.put("clientName", getClientName());
        rows.put("ufn", claim().getUniqueFileNumber());
        addUcnSummaryRow(rows);
        rows.put("providerName", getProviderName());
        rows.put("officeCode", claim().getOfficeCode());
        rows.put("submittedDate", claim().getSubmittedDate());
        rows.put("categoryOfLaw", claim().getCategoryOfLaw());
        rows.put("feeCodeDescription", claim().getFeeCodeDescription());
        return rows;
    }

    void addUcnSummaryRow(Map<String, Object> summaryRows);

    void addPoliceStationCourtPrisonIdRow(Map<String, Object> summaryRows);

    void addSchemeIdRow(Map<String, Object> summaryRows);

    void addMatterTypeCodeRow(Map<String, Object> summaryRows);

    // 'Values' rows for the 'Claim details' page
    default List<ClaimFieldRow> getSummaryClaimFieldRows() {
        Stream<ClaimField> rows = Stream.concat(
                claimFields(),
                Stream.of(claim().getVatClaimed(), claim().isHasAssessment() ? null : claim().getTotalAmount()));
        return toClaimFieldRows(rows).toList();
    }

    // 'Claim costs' rows for the 'Review and amend' page
    default List<ClaimFieldRow> getReviewClaimFieldRows() {
        Stream<ClaimField> rows = Stream.concat(claimFields(), Stream.of(claim().getVatClaimed()));
        return toClaimFieldRows(rows).toList();
    }

    // 'Total claim value' rows for the 'Review and amend' page
    default List<ClaimFieldRow> getAssessedTotals() {
        return toClaimFieldRows(claim().getAssessedTotalFields()).toList();
    }

    // 'Total allowed value' rows for the 'Review and amend' page
    default List<ClaimFieldRow> getAllowedTotals() {
        return toClaimFieldRows(claim().getAllowedTotalFields()).toList();
    }

    default Stream<ClaimField> claimFields() {
        return Stream.of(
                claim().getFixedFee(),
                claim().getNetProfitCost(),
                claim().getNetDisbursementAmount(),
                claim().getDisbursementVatAmount());
    }

    default List<ReviewAndAmendFormError> getErrors() {
        Stream<ClaimField> claimFields = Stream.of(
                        claimFields(), claim().getAssessedTotalFields(), claim().getAllowedTotalFields())
                .flatMap(Function.identity());

        return toClaimFieldRows(claimFields)
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
        String date = DateUtils.displayDateTimeDateValue(claim().getLastUpdatedDateTime());
        String time = DateUtils.displayDateTimeTimeValue(claim().getLastUpdatedDateTime());

        List<Object> args = new ArrayList<>();
        String editMessageKey;
        if (user != null && user.getName() != null) {
            args.add(user.getName());
            editMessageKey = "claimSummary.lastAssessmentText";
        } else {
            editMessageKey = "claimSummary.lastAssessmentText.noUser";
        }
        args.add(date);
        args.add(time);

        String messageKey = (lastAssessment() != null && !isVoidClaim())
                ? lastAssessment().getLastAssessmentOutcome().getMessageKey()
                : "claimSummary.void.message";
        args.add(new ThymeleafMessage(messageKey));

        return new ThymeleafMessage(editMessageKey, args.toArray());
    }

    private Stream<ClaimFieldRow> toClaimFieldRows(Stream<ClaimField> claimFields) {
        return claimFields
                .filter(Objects::nonNull)
                .map(ClaimField::toClaimFieldRow)
                .filter(Objects::nonNull);
    }

    default String reviewAssessmentChangeUrl(String submissionId, String claimId, String question) {
        return String.format("/submissions/%s/claims/%s/assessment-outcome#%s", submissionId, claimId, question);
    }

    private Object getProviderName() {
        if (claim().getProviderName() == null) {
            return new ThymeleafMessage("provider.firmName.notAvailable");
        }
        return claim().getProviderName();
    }

    default boolean isVoidClaim() {
        return ClaimStatus.VOID == claim().getStatus();
    }
}
