package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.models.TableRow;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.CHANGE_URL_PLACEHOLDER;

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
    public List<TableRow> buildTableRows(ClaimResponse claimResponse) {
        List<TableRow> rows = new ArrayList<>();

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

        // Fixed fee
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.fixedFee"))
                .submittedAmount(null)
                .calculatedAmount(feeCalculation.getFixedFeeAmount())
                .assessedAmount(feeCalculation.getFixedFeeAmount())
                .build());

        // Profit cost (ex VAT)
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.profitCost"))
                .submittedAmount(claimResponse.getNetProfitCostsAmount())
                .calculatedAmount(feeCalculation.getNetProfitCostsAmount())
                .assessedAmount(feeCalculation.getNetProfitCostsAmount())
                .changeUrl(CHANGE_URL_PLACEHOLDER)
                .build());

        // Disbursement (ex VAT)
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.disbursement"))
                .submittedAmount(claimResponse.getNetDisbursementAmount())
                .calculatedAmount(feeCalculation.getDisbursementAmount())
                .assessedAmount(feeCalculation.getDisbursementAmount())
                .changeUrl(CHANGE_URL_PLACEHOLDER)
                .build());

        // Disbursement VAT
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.disbursementVat"))
                .submittedAmount(claimResponse.getDisbursementsVatAmount())
                .calculatedAmount(feeCalculation.getDisbursementVatAmount())
                .assessedAmount(feeCalculation.getDisbursementVatAmount())
                .changeUrl(CHANGE_URL_PLACEHOLDER)
                .build());

        // Counsel's costs
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.counselCosts"))
                .submittedAmount(claimResponse.getNetCounselCostsAmount())
                .calculatedAmount(feeCalculation.getNetCostOfCounselAmount())
                .assessedAmount(feeCalculation.getNetCostOfCounselAmount())
                .changeUrl(CHANGE_URL_PLACEHOLDER)
                .build());

        // Detention travel & waiting costs
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.detentionTravelWaiting"))
                .submittedAmount(claimResponse.getDetentionTravelWaitingCostsAmount())
                .calculatedAmount(feeCalculation.getDetentionAndWaitingCostsAmount())
                .assessedAmount(feeCalculation.getDetentionAndWaitingCostsAmount())
                .changeUrl(CHANGE_URL_PLACEHOLDER)
                .build());

        // JR / form filling
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.jrFormFilling"))
                .submittedAmount(claimResponse.getJrFormFillingAmount())
                .calculatedAmount(feeCalculation.getJrFormFillingAmount())
                .assessedAmount(feeCalculation.getJrFormFillingAmount())
                .changeUrl(CHANGE_URL_PLACEHOLDER)
                .build());

        // Only add bolt-on rows if bolt-on details exist
        if (boltOnDetails != null) {
            // Adjourned hearing fee
            rows.add(TableRow.builder()
                    .item(getMessage("claimReview.rows.adjournedHearingFee"))
                    .submittedCount(claimResponse.getAdjournedHearingFeeAmount())
                    .calculatedAmount(boltOnDetails.getBoltOnAdjournedHearingFee())
                    .assessedAmount(boltOnDetails.getBoltOnAdjournedHearingFee())
                    .build());

            // CMRH telephone
            rows.add(TableRow.builder()
                    .item(getMessage("claimReview.rows.cmrhTelephone"))
                    .submittedCount(claimResponse.getCmrhTelephoneCount())
                    .calculatedAmount(boltOnDetails.getBoltOnCmrhTelephoneFee())
                    .assessedAmount(boltOnDetails.getBoltOnCmrhTelephoneFee())
                    .build());

            // CMRH oral
            rows.add(TableRow.builder()
                    .item(getMessage("claimReview.rows.cmrhOral"))
                    .submittedCount(claimResponse.getCmrhOralCount())
                    .calculatedAmount(boltOnDetails.getBoltOnCmrhOralFee())
                    .assessedAmount(boltOnDetails.getBoltOnCmrhOralFee())
                    .build());

            // Home office interview
            rows.add(TableRow.builder()
                    .item(getMessage("claimReview.rows.homeOfficeInterview"))
                    .submittedCount(claimResponse.getHoInterview())
                    .calculatedAmount(boltOnDetails.getBoltOnHomeOfficeInterviewFee())
                    .assessedAmount(boltOnDetails.getBoltOnHomeOfficeInterviewFee())
                    .build());

            // Substantive hearing
            rows.add(TableRow.builder()
                    .item(getMessage("claimReview.rows.substantiveHearing"))
                    .submittedCount(claimResponse.getAdjournedHearingFeeAmount())
                    .calculatedAmount(boltOnDetails.getBoltOnAdjournedHearingFee())
                    .assessedAmount(boltOnDetails.getBoltOnAdjournedHearingFee())
                    .build());
        }

        // VAT
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.vat"))
                .submittedAmount(feeCalculation.getVatRateApplied())
                .calculatedAmount(feeCalculation.getVatRateApplied())
                .assessedAmount(feeCalculation.getVatRateApplied())
                .build());

        // Total
        rows.add(TableRow.builder()
                .item(getMessage("claimReview.rows.total"))
                .submittedAmount(feeCalculation.getTotalAmount())
                .calculatedAmount(feeCalculation.getTotalAmount())
                .assessedAmount(feeCalculation.getTotalAmount())
                .build());

        return rows;
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, locale);
    }
}