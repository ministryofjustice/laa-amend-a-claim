package uk.gov.justice.laa.amend.claim.viewmodels;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DISBURSEMENT_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_DISBURSEMENTS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TRAVEL_COSTS;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.WAITING_COSTS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.CalculatedTotalClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

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
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
            claim.setHasAssessment(true);

            CrimeClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(7, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(TRAVEL_COSTS, result.get(4).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/travel-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(WAITING_COSTS, result.get(5).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/waiting-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(VAT, result.get(6).getKey());
        }

        @Test
        void rowsRenderedForClaimValuesWhenClaimDoesNotHaveAnAssessment() {
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
            claim.setTotalAmount(CalculatedTotalClaimField.builder().build());
            claim.setHasAssessment(false);

            CrimeClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(8, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(TRAVEL_COSTS, result.get(4).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/travel-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(WAITING_COSTS, result.get(5).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/waiting-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(VAT, result.get(6).getKey());

            Assertions.assertEquals(TOTAL, result.get(7).getKey());
        }
    }

    @Nested
    class GetReviewClaimFieldRowsTests {
        @Test
        void rowsRenderedForClaimValues() {
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

            CrimeClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getReviewClaimFieldRows();

            Assertions.assertEquals(7, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(TRAVEL_COSTS, result.get(4).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/travel-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(WAITING_COSTS, result.get(5).getKey());
            Assertions.assertEquals(
                    "/submissions/%s/claims/%s/waiting-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(VAT, result.get(6).getKey());
        }
    }

    @Nested
    class GetErrorTests {

        @Test
        void convertFieldsThatNeedAmendingIntoErrors() {
            CrimeClaimDetails claim = new CrimeClaimDetails();

            ClaimField netProfitCostField = MockClaimsFunctions.createNetProfitCostField();
            ClaimField travelCostsField = MockClaimsFunctions.createTravelCostField();
            ClaimField waitingCostsField = MockClaimsFunctions.createWaitingCostField();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            ClaimField allowedTotalVatField = MockClaimsFunctions.createAllowedTotalVatField();
            ClaimField allowedTotalInclVatField = MockClaimsFunctions.createAllowedTotalInclVatField();

            netProfitCostField.setAssessed(null);
            travelCostsField.setAssessed(null);
            waitingCostsField.setAssessed(null);
            assessedTotalVatField.setAssessed(null);
            assessedTotalInclVatField.setAssessed(null);
            allowedTotalVatField.setAssessed(null);
            allowedTotalInclVatField.setAssessed(null);

            claim.setNetProfitCost(netProfitCostField);
            claim.setTravelCosts(travelCostsField);
            claim.setWaitingCosts(waitingCostsField);
            claim.setAssessedTotalVat(assessedTotalVatField);
            claim.setAssessedTotalInclVat(assessedTotalInclVatField);
            claim.setAllowedTotalVat(allowedTotalVatField);
            claim.setAllowedTotalInclVat(allowedTotalInclVatField);

            CrimeClaimDetailsView viewModel = new CrimeClaimDetailsView(claim);

            List<ReviewAndAmendFormError> expectedErrors = List.of(
                    new ReviewAndAmendFormError("profit-cost", "claimSummary.rows.profitCost.error"),
                    new ReviewAndAmendFormError("assessed-total-vat", "claimSummary.rows.assessedTotalVat.error"),
                    new ReviewAndAmendFormError(
                            "assessed-total-incl-vat", "claimSummary.rows.assessedTotalInclVat.error"),
                    new ReviewAndAmendFormError("allowed-total-vat", "claimSummary.rows.allowedTotalVat.error"),
                    new ReviewAndAmendFormError(
                            "allowed-total-incl-vat", "claimSummary.rows.allowedTotalInclVat.error"));

            Assertions.assertEquals(expectedErrors, viewModel.getErrors());
        }
    }
}
