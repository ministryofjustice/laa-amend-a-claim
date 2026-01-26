package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.COUNSELS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DETENTION_TRAVEL_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DISBURSEMENT_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_DISBURSEMENTS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.VAT;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.updateClaimFieldSubmittedValue;

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
            claim.setUniqueClientNumber("unique client number");
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
            expectedResult.put("ucn", "unique client number");
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
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setHasAssessment(true);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(13, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(JR_FORM_FILLING, result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(COUNSELS_COST, result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals(CMRH_ORAL, result.get(7).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(7).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(7).getCalculated());

            Assertions.assertEquals(CMRH_TELEPHONE, result.get(8).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(8).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).getCalculated());

            Assertions.assertEquals(HO_INTERVIEW, result.get(9).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(9).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(9).getCalculated());

            Assertions.assertEquals(SUBSTANTIVE_HEARING, result.get(10).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(10).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(10).getCalculated());

            Assertions.assertEquals(ADJOURNED_FEE, result.get(11).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(11).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(11).getCalculated());

            Assertions.assertEquals(VAT, result.get(12).getKey());
            Assertions.assertEquals(true, result.get(12).getSubmitted());
            Assertions.assertEquals(false, result.get(12).getCalculated());
        }

        @Test
        void rowsRenderedForClaimValuesWhenClaimDoesNotHaveAnAssessment() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setHasAssessment(false);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(14, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(JR_FORM_FILLING, result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(COUNSELS_COST, result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals(CMRH_ORAL, result.get(7).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(7).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(7).getCalculated());

            Assertions.assertEquals(CMRH_TELEPHONE, result.get(8).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(8).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).getCalculated());

            Assertions.assertEquals(HO_INTERVIEW, result.get(9).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(9).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(9).getCalculated());

            Assertions.assertEquals(SUBSTANTIVE_HEARING, result.get(10).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(10).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(10).getCalculated());

            Assertions.assertEquals(ADJOURNED_FEE, result.get(11).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(11).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(11).getCalculated());

            Assertions.assertEquals(VAT, result.get(12).getKey());
            Assertions.assertEquals(true, result.get(12).getSubmitted());
            Assertions.assertEquals(false, result.get(12).getCalculated());

            Assertions.assertEquals(TOTAL, result.get(13).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(13).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(13).getCalculated());
        }

        @Test
        void rowsRenderedForNullBoltOnClaimValues() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAdjournedHearing(null);
            claim.setCmrhTelephone(null);
            claim.setCmrhOral(null);
            claim.setHoInterview(null);
            claim.setSubstantiveHearing(null);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

            List.of(ADJOURNED_FEE, SUBSTANTIVE_HEARING, CMRH_TELEPHONE, CMRH_ORAL, HO_INTERVIEW).forEach(key ->
                Assertions.assertFalse(
                    result.stream().anyMatch(row -> key.equals(row.getKey())),
                    "Field with key '" + key + "' should not exist"
                )
            );

            Assertions.assertEquals(9, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(JR_FORM_FILLING, result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(COUNSELS_COST, result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals(VAT, result.get(7).getKey());
            Assertions.assertEquals(true, result.get(7).getSubmitted());
            Assertions.assertEquals(false, result.get(7).getCalculated());

            Assertions.assertEquals(TOTAL, result.get(8).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(8).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).getCalculated());
        }


        @Test
        void substantiveHearingBoltOnNotVisibleOnFalse() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            CivilClaimDetailsView viewModel = createView(claim);

            claim.setSubstantiveHearing(updateClaimFieldSubmittedValue(claim.getSubstantiveHearing(), false));
            List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();
            Assertions.assertFalse(
                result.stream().anyMatch(row -> SUBSTANTIVE_HEARING.equals(row.getKey())),
                "Rows should not contain substantive hearing"
            );
        }

        @Test
        void rowsRenderedForZeroBoltOnClaimValues() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAdjournedHearing(updateClaimFieldSubmittedValue(claim.getAdjournedHearing(), BigDecimal.ZERO));
            claim.setCmrhTelephone(updateClaimFieldSubmittedValue(claim.getCmrhTelephone(), 0));
            claim.setCmrhOral(updateClaimFieldSubmittedValue(claim.getCmrhOral(), 0));
            claim.setHoInterview(updateClaimFieldSubmittedValue(claim.getHoInterview(), 0));
            claim.setSubstantiveHearing(updateClaimFieldSubmittedValue(claim.getSubstantiveHearing(), false));

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

            Assertions.assertEquals(9, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(JR_FORM_FILLING, result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(COUNSELS_COST, result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals(VAT, result.get(7).getKey());
            Assertions.assertEquals(true, result.get(7).getSubmitted());
            Assertions.assertEquals(false, result.get(7).getCalculated());

            Assertions.assertEquals(TOTAL, result.get(8).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(8).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).getCalculated());
        }
    }

    @Nested
    class GetReviewClaimFieldRowsTests {

        @Test
        void rowsRenderedForClaimValues() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

            CivilClaimDetailsView viewModel = createView(claim);
            claim.setSubstantiveHearing(updateClaimFieldSubmittedValue(claim.getSubstantiveHearing(), true));
            List<ClaimFieldRow> result = viewModel.getReviewClaimFieldRows();

            Assertions.assertEquals(13, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(JR_FORM_FILLING, result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(COUNSELS_COST, result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals(CMRH_ORAL, result.get(7).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(7).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(7).getCalculated());

            Assertions.assertEquals(CMRH_TELEPHONE, result.get(8).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(8).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).getCalculated());

            Assertions.assertEquals(HO_INTERVIEW, result.get(9).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(9).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(9).getCalculated());

            Assertions.assertEquals(SUBSTANTIVE_HEARING, result.get(10).getKey());
            Assertions.assertEquals(true, result.get(10).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(10).getCalculated());

            Assertions.assertEquals(ADJOURNED_FEE, result.get(11).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(11).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(11).getCalculated());

            Assertions.assertEquals(VAT, result.get(12).getKey());
            Assertions.assertEquals(true, result.get(12).getSubmitted());
            Assertions.assertEquals(false, result.get(12).getCalculated());
            Assertions.assertEquals("/submissions/%s/claims/%s/assessment-outcome", result.get(12).getChangeUrl());
        }

        @Test
        void rowsRenderedForNullBoltOnClaimValues() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAdjournedHearing(null);
            claim.setCmrhTelephone(null);
            claim.setCmrhOral(null);
            claim.setHoInterview(null);
            claim.setSubstantiveHearing(null);

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getReviewClaimFieldRows();

            Assertions.assertEquals(8, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(JR_FORM_FILLING, result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(COUNSELS_COST, result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals(VAT, result.get(7).getKey());
            Assertions.assertEquals(true, result.get(7).getSubmitted());
            Assertions.assertEquals(false, result.get(7).getCalculated());
            Assertions.assertEquals("/submissions/%s/claims/%s/assessment-outcome", result.get(7).getChangeUrl());
        }

        @Test
        void rowsRenderedForZeroBoltOnClaimValues() {
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setAdjournedHearing(updateClaimFieldSubmittedValue(claim.getAdjournedHearing(), BigDecimal.ZERO));
            claim.setCmrhTelephone(updateClaimFieldSubmittedValue(claim.getCmrhTelephone(), 0));
            claim.setCmrhOral(updateClaimFieldSubmittedValue(claim.getCmrhOral(), 0));
            claim.setHoInterview(updateClaimFieldSubmittedValue(claim.getHoInterview(), 0));
            claim.setSubstantiveHearing(updateClaimFieldSubmittedValue(claim.getSubstantiveHearing(), BigDecimal.ZERO));

            CivilClaimDetailsView viewModel = createView(claim);
            List<ClaimFieldRow> result = viewModel.getReviewClaimFieldRows();

            Assertions.assertEquals(8, result.size());

            Assertions.assertEquals(FIXED_FEE, result.get(0).getKey());

            Assertions.assertEquals(NET_PROFIT_COST, result.get(1).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).getChangeUrl());

            Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).getChangeUrl());

            Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).getKey());
            Assertions.assertEquals("/submissions/%s/claims/%s/disbursements-vat", result.get(3).getChangeUrl());

            Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", result.get(4).getChangeUrl());

            Assertions.assertEquals(JR_FORM_FILLING, result.get(5).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).getChangeUrl());

            Assertions.assertEquals(COUNSELS_COST, result.get(6).getKey());
            Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).getSubmitted());
            Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).getCalculated());
            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).getChangeUrl());

            Assertions.assertEquals(VAT, result.get(7).getKey());
            Assertions.assertEquals(true, result.get(7).getSubmitted());
            Assertions.assertEquals(false, result.get(7).getCalculated());
        }
    }

    @Nested
    class GetErrorTests {

        @Test
        void convertFieldsThatNeedAmendingIntoErrors() {
            CivilClaimDetails claim = new CivilClaimDetails();

            ClaimField netProfitCostField = MockClaimsFunctions.createNetProfitCostField();
            ClaimField counselField = MockClaimsFunctions.createCounselCostField();
            ClaimField jrFormField = MockClaimsFunctions.createJrFormFillingCostField();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            ClaimField allowedTotalVatField = MockClaimsFunctions.createAllowedTotalVatField();
            ClaimField allowedTotalInclVatField = MockClaimsFunctions.createAllowedTotalInclVatField();

            netProfitCostField.setAssessed(null);
            counselField.setAssessed(null);
            jrFormField.setAssessed(null);
            assessedTotalVatField.setAssessed(null);
            assessedTotalInclVatField.setAssessed(null);
            allowedTotalVatField.setAssessed(null);
            allowedTotalInclVatField.setAssessed(null);

            claim.setNetProfitCost(netProfitCostField);
            claim.setCounselsCost(counselField);
            claim.setJrFormFillingCost(jrFormField);
            claim.setAssessedTotalVat(assessedTotalVatField);
            claim.setAssessedTotalInclVat(assessedTotalInclVatField);
            claim.setAllowedTotalVat(allowedTotalVatField);
            claim.setAllowedTotalInclVat(allowedTotalInclVatField);

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
