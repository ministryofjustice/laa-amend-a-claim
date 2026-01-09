package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CivilClaimDetailsViewTest extends ClaimDetailsViewTest<CivilClaimDetails, CivilClaimDetailsView> {

    @Override
    protected CivilClaimDetails createClaim() {
        return new CivilClaimDetails();
    }

    @Override
    protected CivilClaimDetailsView createView(CivilClaimDetails claim) {
        return new CivilClaimDetailsView(claim);
    }

    @Nested
    class GetMatterTypeCodeOneTests {
        @Test
        void getMatterTypeCodeOneWhenInExpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("IMLB+AHQS");
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertEquals("IMLB", viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenInUnexpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("IMLB");
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertEquals("IMLB", viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenNull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode(null);
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenEmpty() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("");
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenBlank() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode(" ");
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }
    }

    @Nested
    class GetMatterTypeCodeTwoTests {
        @Test
        void getMatterTypeCodeTwoWhenInExpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("IMLB+AHQS");
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertEquals("AHQS", viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenInUnexpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("IMLB");
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertNull(viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenNull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode(null);
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenEmpty() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("");
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenBlank() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode(" ");
            CivilClaimDetailsView viewModel = createView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }
    }

    @Nested
    class GetSummaryRowsTests {
        @Test
        void createMapOfKeyValuePairs() {
            LocalDateTime submittedDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
            LocalDate caseStartDate = LocalDate.of(2001, 1, 1);
            LocalDate caseEndDate = LocalDate.of(2002, 1, 1);

            CivilClaimDetails claim = createClaim();
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
            claim.setMatterTypeCode("IMLB+AHQS");
            claim.setCaseStartDate(caseStartDate);
            claim.setCaseEndDate(caseEndDate);
            claim.setEscaped(true);
            claim.setVatApplicable(false);

            CivilClaimDetailsView viewModel = createView(claim);

            Map<String, Object> result = viewModel.getSummaryRows();

            Map<String, Object> expectedResult = new LinkedHashMap<>();
            expectedResult.put("clientName", "John Smith");
            expectedResult.put("ufn", "unique file number");
            expectedResult.put("ucn", "case reference number");
            expectedResult.put("providerName", "provider name");
            expectedResult.put("providerAccountNumber", "provider account number");
            expectedResult.put("submittedDate", submittedDate);
            expectedResult.put("areaOfLaw", "area of law");
            expectedResult.put("categoryOfLaw", "category of law");
            expectedResult.put("feeCode", "fee code");
            expectedResult.put("feeCodeDescription", "fee code description");
            expectedResult.put("matterTypeCodeOne", "IMLB");
            expectedResult.put("matterTypeCodeTwo", "AHQS");
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
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", 100, 100);
            ClaimField cmrhTelephone = new ClaimField("9", 100, 100);
            ClaimField cmrhOral = new ClaimField("10", 100, 100);
            ClaimField hoInterview = new ClaimField("11", 100, 100);
            ClaimField substantiveHearing = new ClaimField("12", 100, 100);
            ClaimField vatClaimed = new ClaimField("13", 100, 100);
            ClaimField totalAmount = new ClaimField("14", 100, 100);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);
            claim.setAdjournedHearing(adjournedHearing);
            claim.setCmrhTelephone(cmrhTelephone);
            claim.setCmrhOral(cmrhOral);
            claim.setHoInterview(hoInterview);
            claim.setSubstantiveHearing(substantiveHearing);
            claim.setVatClaimed(vatClaimed);
            claim.setTotalAmount(totalAmount);
            claim.setHasAssessment(true);

            CivilClaimDetailsView viewModel = createView(claim);
            ArrayList<ClaimField> expectedRows = new ArrayList<>(List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost,
                cmrhOral,
                cmrhTelephone,
                hoInterview,
                substantiveHearing,
                adjournedHearing,
                vatClaimed
            ));

            Assertions.assertEquals(expectedRows, viewModel.getSummaryClaimFieldRows());
        }

        @Test
        void rowsRenderedForClaimValuesWhenClaimDoesNotHaveAnAssessment() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", 100, 100);
            ClaimField cmrhTelephone = new ClaimField("9", 100, 100);
            ClaimField cmrhOral = new ClaimField("10", 100, 100);
            ClaimField hoInterview = new ClaimField("11", 100, 100);
            ClaimField substantiveHearing = new ClaimField("12", 100, 100);
            ClaimField vatClaimed = new ClaimField("13", 100, 100);
            ClaimField totalAmount = new ClaimField("14", 100, 100);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);
            claim.setAdjournedHearing(adjournedHearing);
            claim.setCmrhTelephone(cmrhTelephone);
            claim.setCmrhOral(cmrhOral);
            claim.setHoInterview(hoInterview);
            claim.setSubstantiveHearing(substantiveHearing);
            claim.setVatClaimed(vatClaimed);
            claim.setTotalAmount(totalAmount);
            claim.setHasAssessment(false);

            CivilClaimDetailsView viewModel = createView(claim);
            ArrayList<ClaimField> expectedRows = new ArrayList<>(List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost,
                cmrhOral,
                cmrhTelephone,
                hoInterview,
                substantiveHearing,
                adjournedHearing,
                vatClaimed,
                totalAmount
            ));

            Assertions.assertEquals(expectedRows, viewModel.getSummaryClaimFieldRows());
        }

        @Test void onlyRowsWithValuesRendered() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", 100, 100);
            ClaimField cmrhTelephone = new ClaimField("9", null, null);
            ClaimField cmrhOral = new ClaimField("10", null, null);
            ClaimField hoInterview = new ClaimField("11", null, null);
            ClaimField substantiveHearing = new ClaimField("12", null, null);
            ClaimField vatClaimed = new ClaimField("13", null, 100);
            ClaimField totalAmount = new ClaimField("14", 100, null);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);
            claim.setAdjournedHearing(adjournedHearing);
            claim.setCmrhTelephone(cmrhTelephone);
            claim.setCmrhOral(cmrhOral);
            claim.setHoInterview(hoInterview);
            claim.setSubstantiveHearing(substantiveHearing);
            claim.setVatClaimed(vatClaimed);
            claim.setTotalAmount(totalAmount);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> expectedRows = new ArrayList<>(List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost,
                adjournedHearing,
                vatClaimed,
                totalAmount
            ));

            Assertions.assertEquals(expectedRows, viewModel.getSummaryClaimFieldRows());
        }

        @Test
        void rowsRenderedForNullBoltOnClaimValues() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost
            );

            Assertions.assertEquals(expectedRows, viewModel.getSummaryClaimFieldRows());
        }

        @Test
        void rowsRenderedForZeroBoltOnClaimValues() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", BigDecimal.ZERO, 100);
            ClaimField cmrhTelephone = new ClaimField("9", 0, null);
            ClaimField cmrhOral = new ClaimField("10", 0, null);
            ClaimField hoInterview = new ClaimField("11", 0, null);
            ClaimField substantiveHearing = new ClaimField("12", BigDecimal.ZERO, null);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);
            claim.setAdjournedHearing(adjournedHearing);
            claim.setCmrhTelephone(cmrhTelephone);
            claim.setCmrhOral(cmrhOral);
            claim.setHoInterview(hoInterview);
            claim.setSubstantiveHearing(substantiveHearing);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost
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
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", 100, 100);
            ClaimField cmrhTelephone = new ClaimField("9", 100, 100);
            ClaimField cmrhOral = new ClaimField("10", 100, 100);
            ClaimField hoInterview = new ClaimField("11", 100, 100);
            ClaimField substantiveHearing = new ClaimField("12", 100, 100);
            ClaimField vatClaimed = new ClaimField("13", 100, 100);
            ClaimField totalAmount = new ClaimField("14", 100, 100);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);
            claim.setAdjournedHearing(adjournedHearing);
            claim.setCmrhTelephone(cmrhTelephone);
            claim.setCmrhOral(cmrhOral);
            claim.setHoInterview(hoInterview);
            claim.setSubstantiveHearing(substantiveHearing);
            claim.setVatClaimed(vatClaimed);
            claim.setTotalAmount(totalAmount);
            claim.setHasAssessment(true);

            CivilClaimDetailsView viewModel = createView(claim);
            ArrayList<ClaimField> expectedRows = new ArrayList<>(List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost,
                cmrhOral,
                cmrhTelephone,
                hoInterview,
                substantiveHearing,
                adjournedHearing,
                vatClaimed
            ));

            Assertions.assertEquals(expectedRows, viewModel.getReviewClaimFieldRows());
        }

        @Test void nullBoltOnRowsRenderedForClaimValues() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", null, 100);
            ClaimField cmrhTelephone = new ClaimField("9", null, null);
            ClaimField cmrhOral = new ClaimField("10", null, null);
            ClaimField hoInterview = new ClaimField("11", null, null);
            ClaimField substantiveHearing = new ClaimField("12", null, null);
            ClaimField vatClaimed = new ClaimField("13", null, 100);
            ClaimField totalAmount = new ClaimField("14", 100, null);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);
            claim.setAdjournedHearing(adjournedHearing);
            claim.setCmrhTelephone(cmrhTelephone);
            claim.setCmrhOral(cmrhOral);
            claim.setHoInterview(hoInterview);
            claim.setSubstantiveHearing(substantiveHearing);
            claim.setVatClaimed(vatClaimed);
            claim.setTotalAmount(totalAmount);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> expectedRows = new ArrayList<>(List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost,
                cmrhOral,
                cmrhTelephone,
                hoInterview,
                substantiveHearing,
                adjournedHearing,
                vatClaimed
            ));

            Assertions.assertEquals(expectedRows, viewModel.getReviewClaimFieldRows());
        }

        @Test
        void rowsRenderedForNullBoltOnClaimValues() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost
            );

            Assertions.assertEquals(expectedRows, viewModel.getReviewClaimFieldRows());
        }

        @Test
        void rowsRenderedForZeroBoltOnClaimValues() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", BigDecimal.ZERO, 100);
            ClaimField cmrhTelephone = new ClaimField("9", 0, null);
            ClaimField cmrhOral = new ClaimField("10", 0, null);
            ClaimField hoInterview = new ClaimField("11", 0, null);
            ClaimField substantiveHearing = new ClaimField("12", BigDecimal.ZERO, null);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);
            claim.setAdjournedHearing(adjournedHearing);
            claim.setCmrhTelephone(cmrhTelephone);
            claim.setCmrhOral(cmrhOral);
            claim.setHoInterview(hoInterview);
            claim.setSubstantiveHearing(substantiveHearing);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost,
                cmrhOral,
                cmrhTelephone,
                hoInterview,
                substantiveHearing,
                adjournedHearing
            );

            Assertions.assertEquals(expectedRows, viewModel.getReviewClaimFieldRows());
        }
    }

    @Nested
    class GetErrorTests {

        @Test
        void convertFieldsThatNeedAmendingIntoErrors() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setNetProfitCost(createClaimField("profitCost", ClaimFieldStatus.MODIFIABLE));
            claim.setCounselsCost(createClaimField("counselsCost", ClaimFieldStatus.MODIFIABLE));
            claim.setJrFormFillingCost(createClaimField("jrFormFilling", ClaimFieldStatus.MODIFIABLE));
            claim.setAssessedTotalVat(createClaimField("assessedTotalVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAssessedTotalInclVat(createClaimField("assessedTotalInclVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAllowedTotalVat(createClaimField("allowedTotalVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAllowedTotalInclVat(createClaimField("allowedTotalInclVat", ClaimFieldStatus.MODIFIABLE));
            CivilClaimDetailsView viewModel = createView(claim);

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
