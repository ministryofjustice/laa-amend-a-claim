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
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());
            claim.setAdjournedHearing(ClaimField.builder().key("ah").submitted(100).calculated(100).build());
            claim.setCmrhTelephone(ClaimField.builder().key("ct").submitted(100).calculated(100).build());
            claim.setCmrhOral(ClaimField.builder().key("co").submitted(100).calculated(100).build());
            claim.setHoInterview(ClaimField.builder().key("hi").submitted(100).calculated(100).build());
            claim.setSubstantiveHearing(ClaimField.builder().key("sh").submitted(100).calculated(100).build());
            claim.setVatClaimed(ClaimField.builder().key("vc").submitted(100).calculated(100).build());
            claim.setTotalAmount(ClaimField.builder().key("ta").submitted(100).calculated(100).build());
            claim.setHasAssessment(true);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(13, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals("co", result.get(7).getKey());
            Assertions.assertEquals(100, result.get(7).getSubmitted());
            Assertions.assertEquals(100, result.get(7).getCalculated());

            Assertions.assertEquals("ct", result.get(8).getKey());
            Assertions.assertEquals(100, result.get(8).getSubmitted());
            Assertions.assertEquals(100, result.get(8).getCalculated());

            Assertions.assertEquals("hi", result.get(9).getKey());
            Assertions.assertEquals(100, result.get(9).getSubmitted());
            Assertions.assertEquals(100, result.get(9).getCalculated());

            Assertions.assertEquals("sh", result.get(10).getKey());
            Assertions.assertEquals(100, result.get(10).getSubmitted());
            Assertions.assertEquals(100, result.get(10).getCalculated());

            Assertions.assertEquals("ah", result.get(11).getKey());
            Assertions.assertEquals(100, result.get(11).getSubmitted());
            Assertions.assertEquals(100, result.get(11).getCalculated());

            Assertions.assertEquals("vc", result.get(12).getKey());
            Assertions.assertEquals(100, result.get(12).getSubmitted());
            Assertions.assertEquals(100, result.get(12).getCalculated());
        }

        @Test
        void rowsRenderedForClaimValuesWhenClaimDoesNotHaveAnAssessment() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());
            claim.setAdjournedHearing(ClaimField.builder().key("ah").submitted(100).calculated(100).build());
            claim.setCmrhTelephone(ClaimField.builder().key("ct").submitted(100).calculated(100).build());
            claim.setCmrhOral(ClaimField.builder().key("co").submitted(100).calculated(100).build());
            claim.setHoInterview(ClaimField.builder().key("hi").submitted(100).calculated(100).build());
            claim.setSubstantiveHearing(ClaimField.builder().key("sh").submitted(100).calculated(100).build());
            claim.setVatClaimed(ClaimField.builder().key("vc").submitted(100).calculated(100).build());
            claim.setTotalAmount(ClaimField.builder().key("ta").submitted(100).calculated(100).build());
            claim.setHasAssessment(false);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(14, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals("co", result.get(7).getKey());
            Assertions.assertEquals(100, result.get(7).getSubmitted());
            Assertions.assertEquals(100, result.get(7).getCalculated());

            Assertions.assertEquals("ct", result.get(8).getKey());
            Assertions.assertEquals(100, result.get(8).getSubmitted());
            Assertions.assertEquals(100, result.get(8).getCalculated());

            Assertions.assertEquals("hi", result.get(9).getKey());
            Assertions.assertEquals(100, result.get(9).getSubmitted());
            Assertions.assertEquals(100, result.get(9).getCalculated());

            Assertions.assertEquals("sh", result.get(10).getKey());
            Assertions.assertEquals(100, result.get(10).getSubmitted());
            Assertions.assertEquals(100, result.get(10).getCalculated());

            Assertions.assertEquals("ah", result.get(11).getKey());
            Assertions.assertEquals(100, result.get(11).getSubmitted());
            Assertions.assertEquals(100, result.get(11).getCalculated());

            Assertions.assertEquals("vc", result.get(12).getKey());
            Assertions.assertEquals(100, result.get(12).getSubmitted());
            Assertions.assertEquals(100, result.get(12).getCalculated());

            Assertions.assertEquals("ta", result.get(13).getKey());
            Assertions.assertEquals(100, result.get(13).getSubmitted());
            Assertions.assertEquals(100, result.get(13).getCalculated());
        }

        @Test void onlyRowsWithValuesRendered() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());
            claim.setAdjournedHearing(ClaimField.builder().key("ah").submitted(100).calculated(100).build());
            claim.setCmrhTelephone(ClaimField.builder().key("ct").build());
            claim.setCmrhOral(ClaimField.builder().key("co").build());
            claim.setHoInterview(ClaimField.builder().key("hi").build());
            claim.setSubstantiveHearing(ClaimField.builder().key("sh").build());
            claim.setVatClaimed(ClaimField.builder().key("vc").calculated(100).build());
            claim.setTotalAmount(ClaimField.builder().key("ta").submitted(100).build());

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(10, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals("ah", result.get(7).getKey());
            Assertions.assertEquals(100, result.get(7).getSubmitted());
            Assertions.assertEquals(100, result.get(7).getCalculated());

            Assertions.assertEquals("vc", result.get(8).getKey());
            Assertions.assertEquals(100, result.get(8).getCalculated());

            Assertions.assertEquals("ta", result.get(9).getKey());
            Assertions.assertEquals(100, result.get(9).getSubmitted());
        }

        @Test
        void rowsRenderedForNullBoltOnClaimValues() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(7, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());
        }

        @Test
        void rowsRenderedForZeroBoltOnClaimValues() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());
            claim.setAdjournedHearing(ClaimField.builder().key("ah").submitted(BigDecimal.ZERO).build());
            claim.setCmrhTelephone(ClaimField.builder().key("ct").submitted(0).build());
            claim.setCmrhOral(ClaimField.builder().key("co").submitted(0).build());
            claim.setHoInterview(ClaimField.builder().key("hi").submitted(0).build());
            claim.setSubstantiveHearing(ClaimField.builder().key("sh").submitted(BigDecimal.ZERO).build());

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(7, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());
        }
    }

    @Nested
    class GetReviewClaimFieldRowsTests {

        @Test
        void rowsRenderedForClaimValues() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());
            claim.setAdjournedHearing(ClaimField.builder().key("ah").submitted(100).calculated(100).build());
            claim.setCmrhTelephone(ClaimField.builder().key("ct").submitted(100).calculated(100).build());
            claim.setCmrhOral(ClaimField.builder().key("co").submitted(100).calculated(100).build());
            claim.setHoInterview(ClaimField.builder().key("hi").submitted(100).calculated(100).build());
            claim.setSubstantiveHearing(ClaimField.builder().key("sh").submitted(100).calculated(100).build());
            claim.setVatClaimed(ClaimField.builder().key("vc").submitted(100).calculated(100).build());
            claim.setTotalAmount(ClaimField.builder().key("ta").submitted(100).calculated(100).build());

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getReviewClaimFieldRows();

            Assertions.assertEquals(13, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals("co", result.get(7).getKey());
            Assertions.assertEquals(100, result.get(7).getSubmitted());
            Assertions.assertEquals(100, result.get(7).getCalculated());

            Assertions.assertEquals("ct", result.get(8).getKey());
            Assertions.assertEquals(100, result.get(8).getSubmitted());
            Assertions.assertEquals(100, result.get(8).getCalculated());

            Assertions.assertEquals("hi", result.get(9).getKey());
            Assertions.assertEquals(100, result.get(9).getSubmitted());
            Assertions.assertEquals(100, result.get(9).getCalculated());

            Assertions.assertEquals("sh", result.get(10).getKey());
            Assertions.assertEquals(100, result.get(10).getSubmitted());
            Assertions.assertEquals(100, result.get(10).getCalculated());

            Assertions.assertEquals("ah", result.get(11).getKey());
            Assertions.assertEquals(100, result.get(11).getSubmitted());
            Assertions.assertEquals(100, result.get(11).getCalculated());

            Assertions.assertEquals("vc", result.get(12).getKey());
            Assertions.assertEquals(100, result.get(12).getSubmitted());
            Assertions.assertEquals(100, result.get(12).getCalculated());
            Assertions.assertEquals("/submissions/%s/claims/%s/assessment-outcome", result.get(12).getChangeUrl());
        }

        @Test void onlyRowsWithValuesRendered() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());
            claim.setAdjournedHearing(ClaimField.builder().key("ah").submitted(100).calculated(100).build());
            claim.setCmrhTelephone(ClaimField.builder().key("ct").build());
            claim.setCmrhOral(ClaimField.builder().key("co").build());
            claim.setHoInterview(ClaimField.builder().key("hi").build());
            claim.setSubstantiveHearing(ClaimField.builder().key("sh").build());
            claim.setVatClaimed(ClaimField.builder().key("vc").calculated(100).build());
            claim.setTotalAmount(ClaimField.builder().key("ta").submitted(100).build());

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getReviewClaimFieldRows();

            Assertions.assertEquals(9, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals("ah", result.get(7).getKey());
            Assertions.assertEquals(100, result.get(7).getSubmitted());
            Assertions.assertEquals(100, result.get(7).getCalculated());

            Assertions.assertEquals("vc", result.get(8).getKey());
            Assertions.assertEquals(100, result.get(8).getCalculated());
            Assertions.assertEquals("/submissions/%s/claims/%s/assessment-outcome", result.get(8).getChangeUrl());
        }

        @Test
        void rowsRenderedForNullBoltOnClaimValues() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getReviewClaimFieldRows();

            Assertions.assertEquals(7, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());
        }

        @Test
        void rowsRenderedForZeroBoltOnClaimValues() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(ClaimField.builder().key("ff").build());
            claim.setNetProfitCost(ClaimField.builder().key("npc").build());
            claim.setNetDisbursementAmount(ClaimField.builder().key("nda").build());
            claim.setDisbursementVatAmount(ClaimField.builder().key("dva").build());
            claim.setCounselsCost(ClaimField.builder().key("cc").build());
            claim.setDetentionTravelWaitingCosts(ClaimField.builder().key("dtwc").build());
            claim.setJrFormFillingCost(ClaimField.builder().key("jffc").build());
            claim.setAdjournedHearing(ClaimField.builder().key("ah").submitted(BigDecimal.ZERO).build());
            claim.setCmrhTelephone(ClaimField.builder().key("ct").submitted(0).build());
            claim.setCmrhOral(ClaimField.builder().key("co").submitted(0).build());
            claim.setHoInterview(ClaimField.builder().key("hi").submitted(0).build());
            claim.setSubstantiveHearing(ClaimField.builder().key("sh").submitted(BigDecimal.ZERO).build());

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimField> result = viewModel.getReviewClaimFieldRows();

            Assertions.assertEquals(7, result.size());

            Assertions.assertEquals("ff", result.get(0).getKey());

            Assertions.assertEquals("npc", result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals("nda", result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals("dva", result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals("dtwc", result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals("jffc", result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals("cc", result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.ZERO, result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());
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
