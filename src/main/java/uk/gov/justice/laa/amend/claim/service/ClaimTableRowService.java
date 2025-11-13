package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimValuesTableRow;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service responsible for building table rows for claim review display.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimTableRowService {

    private final MessageSource messageSource;
    Locale locale;

    /**
     * Builds a list of table rows from claim response data.
     *
     * @param claimResponse the claim response containing fee calculation data
     * @return list of table rows for display
     */
    public List<ClaimValuesTableRow> buildTableRows(ClaimResponse claimResponse) {
        List<ClaimValuesTableRow> rows = new ArrayList<>();

        if (claimResponse == null) {
            log.warn("Claim response is null, returning empty table rows");
            return rows;
        }

        FeeCalculationPatch feeCalculation = claimResponse.getFeeCalculationResponse();
        if (feeCalculation == null) {
            log.warn("Fee calculation is null for claim, returning empty table rows");
            return rows;
        }

        BoltOnPatch boltOnDetails = feeCalculation.getBoltOnDetails();
        if (boltOnDetails == null) {
            log.warn("Bolt-on details are null for claim, adding only non-bolt-on rows");
        }

        String submissionId = claimResponse.getSubmissionId();
        String claimId = claimResponse.getId();

        // Fixed fee
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.fixedFee"))
                .submittedAmount(null)
                .calculatedAmount(feeCalculation.getFixedFeeAmount())
                .build());

        // Profit cost (ex VAT)
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.profitCost"))
                .submittedAmount(claimResponse.getNetProfitCostsAmount())
                .calculatedAmount(feeCalculation.getNetProfitCostsAmount())
                .changeUrl(getUrl(submissionId, claimId, Cost.PROFIT_COSTS))
                .build());

        // Disbursement (ex VAT)
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.disbursement"))
                .submittedAmount(claimResponse.getNetDisbursementAmount())
                .calculatedAmount(feeCalculation.getDisbursementAmount())
                .changeUrl(getUrl(submissionId, claimId, Cost.DISBURSEMENTS))
                .build());

        // Disbursement VAT
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.disbursementVat"))
                .submittedAmount(claimResponse.getDisbursementsVatAmount())
                .calculatedAmount(feeCalculation.getDisbursementVatAmount())
                .changeUrl(getUrl(submissionId, claimId, Cost.DISBURSEMENTS_VAT))
                .build());

        // Counsel's costs
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.counselCosts"))
                .submittedAmount(claimResponse.getNetCounselCostsAmount())
                .calculatedAmount(feeCalculation.getNetCostOfCounselAmount())
                .changeUrl(getUrl(submissionId, claimId, Cost.COUNSEL_COSTS))
                .build());

        // Detention travel & waiting costs
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.detentionTravelWaiting"))
                .submittedAmount(claimResponse.getDetentionTravelWaitingCostsAmount())
                .calculatedAmount(feeCalculation.getDetentionAndWaitingCostsAmount())
                .changeUrl(getUrl(submissionId, claimId, Cost.DETENTION_TRAVEL_AND_WAITING_COSTS))
                .build());

        // JR / form filling
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.jrFormFilling"))
                .submittedAmount(claimResponse.getJrFormFillingAmount())
                .calculatedAmount(feeCalculation.getJrFormFillingAmount())
                .changeUrl(getUrl(submissionId, claimId, Cost.JR_FORM_FILLING_COSTS))
                .build());

        // Only add bolt-on rows if bolt-on details exist
        if (boltOnDetails != null) {
            // Adjourned hearing fee
            if (claimResponse.getAdjournedHearingFeeAmount() != null) {
                rows.add(ClaimValuesTableRow.builder()
                        .item(getMessage("claimValuesTable.rows.adjournedHearingFee"))
                        .submittedCount(claimResponse.getAdjournedHearingFeeAmount())
                        .calculatedAmount(boltOnDetails.getBoltOnAdjournedHearingFee())
                        .build());
            }

            // CMRH telephone
            if (claimResponse.getAdjournedHearingFeeAmount() != null) {
                rows.add(ClaimValuesTableRow.builder()
                        .item(getMessage("claimValuesTable.rows.cmrhTelephone"))
                        .submittedCount(claimResponse.getCmrhTelephoneCount())
                        .calculatedAmount(boltOnDetails.getBoltOnCmrhTelephoneFee())
                        .build());
            }

            // CMRH oral
            if (claimResponse.getCmrhOralCount() != null) {
                rows.add(ClaimValuesTableRow.builder()
                        .item(getMessage("claimValuesTable.rows.cmrhOral"))
                        .submittedCount(claimResponse.getCmrhOralCount())
                        .calculatedAmount(boltOnDetails.getBoltOnCmrhOralFee())
                        .build());
            }

            // Home office interview
            if (claimResponse.getHoInterview() != null) {
                rows.add(ClaimValuesTableRow.builder()
                        .item(getMessage("claimValuesTable.rows.homeOfficeInterview"))
                        .submittedCount(claimResponse.getHoInterview())
                        .calculatedAmount(boltOnDetails.getBoltOnHomeOfficeInterviewFee())
                        .build());
            }

            // Substantive hearing
            if (claimResponse.getAdjournedHearingFeeAmount() != null) {
                rows.add(ClaimValuesTableRow.builder()
                        .item(getMessage("claimValuesTable.rows.substantiveHearing"))
                        .submittedCount(claimResponse.getAdjournedHearingFeeAmount())
                        .calculatedAmount(boltOnDetails.getBoltOnAdjournedHearingFee())
                        .build());
            }
        }

        // VAT
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.vat"))
                .submittedYesNo(claimResponse.getIsVatApplicable())
                .calculatedYesNo(feeCalculation.getVatIndicator())
                .assessedYesNo(feeCalculation.getVatIndicator())
                .build());

        // Total
        rows.add(ClaimValuesTableRow.builder()
                .item(getMessage("claimValuesTable.rows.total"))
                .submittedAmount(feeCalculation.getTotalAmount())
                .calculatedAmount(feeCalculation.getTotalAmount())
                .assessedAmount(feeCalculation.getTotalAmount())
                .totalRow(true)
                .build());

        return rows;
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, locale);
    }

    private String getUrl(String submissionId, String claimId, Cost cost) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost.getPath());
    }
}