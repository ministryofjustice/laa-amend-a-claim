package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrimeClaimDetailsViewTest extends ClaimDetailsViewTest<CrimeClaimDetails, CrimeClaimDetailsView> {

    @Override
    protected CrimeClaimDetails createClaim() {
        return new CrimeClaimDetails();
    }

    @Override
    protected CrimeClaimDetailsView createView(CrimeClaimDetails claim) {
        return new CrimeClaimDetailsView(claim);
    }

    @Nested
    class GetSummaryRowsTests {
        @Test
        void createMapOfKeyValuePairs() {
            LocalDateTime submittedDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
            LocalDate caseStartDate = LocalDate.of(2001, 1, 1);
            LocalDate caseEndDate = LocalDate.of(2002, 1, 1);

            CrimeClaimDetails claim = createClaim();
            claim.setClientForename("John");
            claim.setClientSurname("Smith");
            claim.setUniqueFileNumber("unique file number");
            claim.setCaseReferenceNumber("case reference number");
            claim.setProviderName("provider name");
            claim.setProviderAccountNumber("provider account number");
            claim.setSubmittedDate(submittedDate);
            claim.setAreaOfLaw("area of law");
            claim.setCategoryOfLaw("category of law");
            claim.setFeeCode("fee code");
            claim.setFeeCodeDescription("fee code description");
            claim.setPoliceStationCourtPrisonId("police station court prison id");
            claim.setSchemeId("scheme id");
            claim.setMatterTypeCode("matter type code");
            claim.setCaseStartDate(caseStartDate);
            claim.setCaseEndDate(caseEndDate);
            claim.setEscaped(true);
            claim.setVatApplicable(false);

            CrimeClaimDetailsView viewModel = createView(claim);

            Map<String, Object> result = viewModel.getSummaryRows();

            Map<String, Object> expectedResult = new LinkedHashMap<>();
            expectedResult.put("clientName", "John Smith");
            expectedResult.put("ufn", "unique file number");
            expectedResult.put("providerName", "provider name");
            expectedResult.put("providerAccountNumber", "provider account number");
            expectedResult.put("submittedDate", submittedDate);
            expectedResult.put("areaOfLaw", "area of law");
            expectedResult.put("categoryOfLaw", "category of law");
            expectedResult.put("feeCode", "fee code");
            expectedResult.put("feeCodeDescription", "fee code description");
            expectedResult.put("policeStationCourtPrisonId", "police station court prison id");
            expectedResult.put("schemeId", "scheme id");
            expectedResult.put("legalMatterCode", "matter type code");
            expectedResult.put("caseStartDate", caseStartDate);
            expectedResult.put("caseEndDate", caseEndDate);
            expectedResult.put("escaped", true);
            expectedResult.put("vatRequested", false);

            Assertions.assertEquals(expectedResult, result);
        }
    }

    @Nested
    class GetSummaryClaimFieldRowsTests {
        @Test
        void rowsRenderedForClaimValuesWhenClaimHasAnAssessment() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField travel = new ClaimField("5", null, null);
            ClaimField waiting = new ClaimField("6", null, null);
            ClaimField totalAmount = new ClaimField("7", null, null);

            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setTravelCosts(travel);
            claim.setWaitingCosts(waiting);
            claim.setTotalAmount(totalAmount);
            claim.setHasAssessment(true);

            CrimeClaimDetailsView viewModel = new CrimeClaimDetailsView(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                travel,
                waiting
            );

            Assertions.assertEquals(expectedRows, viewModel.getSummaryClaimFieldRows());
        }

        @Test
        void rowsRenderedForClaimValuesWhenClaimDoesNotHaveAnAssessment() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField travel = new ClaimField("5", null, null);
            ClaimField waiting = new ClaimField("6", null, null);
            ClaimField totalAmount = new ClaimField("7", null, null);

            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setTravelCosts(travel);
            claim.setWaitingCosts(waiting);
            claim.setTotalAmount(totalAmount);
            claim.setHasAssessment(false);

            CrimeClaimDetailsView viewModel = new CrimeClaimDetailsView(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                travel,
                waiting,
                totalAmount
            );

            Assertions.assertEquals(expectedRows, viewModel.getSummaryClaimFieldRows());
        }
    }

    @Nested
    class GetReviewClaimFieldRowsTests {
        @Test
        void rowsRenderedForClaimValues() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField travel = new ClaimField("5", null, null);
            ClaimField waiting = new ClaimField("6", null, null);
            ClaimField totalAmount = new ClaimField("7", null, null);

            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setTravelCosts(travel);
            claim.setWaitingCosts(waiting);
            claim.setTotalAmount(totalAmount);

            CrimeClaimDetailsView viewModel = new CrimeClaimDetailsView(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                travel,
                waiting
            );

            Assertions.assertEquals(expectedRows, viewModel.getReviewClaimFieldRows());
        }
    }

    @Nested
    class GetErrorTests {

        @Test
        void convertFieldsThatNeedAmendingIntoErrors() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setNetProfitCost(createClaimField("profitCost", ClaimFieldStatus.MODIFIABLE));
            claim.setTravelCosts(createClaimField("travel", ClaimFieldStatus.MODIFIABLE));
            claim.setWaitingCosts(createClaimField("waiting", ClaimFieldStatus.MODIFIABLE));
            claim.setAssessedTotalVat(createClaimField("assessedTotalVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAssessedTotalInclVat(createClaimField("assessedTotalInclVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAllowedTotalVat(createClaimField("allowedTotalVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAllowedTotalInclVat(createClaimField("allowedTotalInclVat", ClaimFieldStatus.MODIFIABLE));
            CrimeClaimDetailsView viewModel = new CrimeClaimDetailsView(claim);

            List<ReviewAndAmendFormError> expectedErrors = List.of(
                new ReviewAndAmendFormError("profit-cost", "claimSummary.rows.profitCost.error"),
                new ReviewAndAmendFormError("assessed-total-vat", "claimSummary.rows.assessedTotalVat.error"),
                new ReviewAndAmendFormError("assessed-total-incl-vat", "claimSummary.rows.assessedTotalInclVat.error"),
                new ReviewAndAmendFormError("allowed-total-vat", "claimSummary.rows.allowedTotalVat.error"),
                new ReviewAndAmendFormError("allowed-total-incl-vat", "claimSummary.rows.allowedTotalInclVat.error")
            );

            Assertions.assertEquals(expectedErrors, viewModel.getErrors());
        }
    }
}
