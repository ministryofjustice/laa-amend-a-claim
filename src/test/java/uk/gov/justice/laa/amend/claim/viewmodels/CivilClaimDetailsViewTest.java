package uk.gov.justice.laa.amend.claim.viewmodels;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

public class CivilClaimDetailsViewTest
    extends ClaimDetailsViewTest<CivilClaimDetails, CivilClaimDetailsView> {

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
      OffsetDateTime submittedDate = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
      LocalDate caseStartDate = LocalDate.of(2001, 1, 1);
      LocalDate caseEndDate = LocalDate.of(2002, 1, 1);

      CivilClaimDetails claim = createClaim();
      claim.setClientForename("John");
      claim.setClientSurname("Smith");
      claim.setUniqueFileNumber("unique file number");
      claim.setUniqueClientNumber("unique client number");
      claim.setProviderName("provider name");
      claim.setOfficeCode("office code");
      claim.setSubmittedDate(submittedDate);
      claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
      claim.setCategoryOfLaw("category of law");
      claim.setFeeCode("fee code");
      claim.setFeeCodeDescription("fee code description");
      claim.setMatterTypeCode("IMLB+AHQS");
      claim.setCaseStartDate(caseStartDate);
      claim.setCaseEndDate(caseEndDate);
      claim.setEscaped(true);
      claim.setVatApplicable(false);

      Map<String, Object> expectedResult = new LinkedHashMap<>();
      expectedResult.put("clientName", "John Smith");
      expectedResult.put("ufn", "unique file number");
      expectedResult.put("ucn", "unique client number");
      expectedResult.put("providerName", "provider name");
      expectedResult.put("officeCode", "office code");
      expectedResult.put("submittedDate", submittedDate);
      expectedResult.put("areaOfLaw", new ThymeleafMessage(AreaOfLaw.CRIME_LOWER.getMessageKey()));
      expectedResult.put("categoryOfLaw", "category of law");
      expectedResult.put("feeCode", "fee code");
      expectedResult.put("feeCodeDescription", "fee code description");
      expectedResult.put("matterTypeCodeOne", "IMLB");
      expectedResult.put("matterTypeCodeTwo", "AHQS");
      expectedResult.put("caseStartDate", caseStartDate);
      expectedResult.put("caseEndDate", caseEndDate);
      expectedResult.put("escaped", true);
      expectedResult.put("vatRequested", false);

      CivilClaimDetailsView viewModel = createView(claim);
      Map<String, Object> result = viewModel.getSummaryRows();
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

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/detention-travel-and-waiting-costs",
          result.get(4).changeUrl());

      Assertions.assertEquals(JR_FORM_FILLING, result.get(5).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).changeUrl());

      Assertions.assertEquals(COUNSELS_COST, result.get(6).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).assessed());
      Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).changeUrl());

      Assertions.assertEquals(CMRH_ORAL, result.get(7).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(7).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(7).calculated());

      Assertions.assertEquals(CMRH_TELEPHONE, result.get(8).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(8).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).calculated());

      Assertions.assertEquals(HO_INTERVIEW, result.get(9).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(9).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(9).calculated());

      Assertions.assertEquals(SUBSTANTIVE_HEARING, result.get(10).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(10).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(10).calculated());

      Assertions.assertEquals(ADJOURNED_FEE, result.get(11).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(11).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(11).calculated());

      Assertions.assertEquals(VAT, result.get(12).key());
      Assertions.assertEquals(true, result.get(12).submitted());
      Assertions.assertEquals(false, result.get(12).calculated());
    }

    @Test
    void rowsRenderedForClaimValuesWhenClaimDoesNotHaveAnAssessment() {
      CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
      claim.setHasAssessment(false);

      CivilClaimDetailsView viewModel = createView(claim);
      List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

      Assertions.assertEquals(14, result.size());

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/detention-travel-and-waiting-costs",
          result.get(4).changeUrl());

      Assertions.assertEquals(JR_FORM_FILLING, result.get(5).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).changeUrl());

      Assertions.assertEquals(COUNSELS_COST, result.get(6).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).assessed());
      Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).changeUrl());

      Assertions.assertEquals(CMRH_ORAL, result.get(7).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(7).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(7).calculated());

      Assertions.assertEquals(CMRH_TELEPHONE, result.get(8).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(8).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).calculated());

      Assertions.assertEquals(HO_INTERVIEW, result.get(9).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(9).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(9).calculated());

      Assertions.assertEquals(SUBSTANTIVE_HEARING, result.get(10).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(10).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(10).calculated());

      Assertions.assertEquals(ADJOURNED_FEE, result.get(11).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(11).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(11).calculated());

      Assertions.assertEquals(VAT, result.get(12).key());
      Assertions.assertEquals(true, result.get(12).submitted());
      Assertions.assertEquals(false, result.get(12).calculated());

      Assertions.assertEquals(TOTAL, result.get(13).key());
      Assertions.assertNull(result.get(13).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(13).calculated());
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

      List.of(ADJOURNED_FEE, SUBSTANTIVE_HEARING, CMRH_TELEPHONE, CMRH_ORAL, HO_INTERVIEW)
          .forEach(
              key ->
                  Assertions.assertFalse(
                      result.stream().anyMatch(row -> key.equals(row.key())),
                      "Field with key '" + key + "' should not exist"));

      Assertions.assertEquals(9, result.size());

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/detention-travel-and-waiting-costs",
          result.get(4).changeUrl());

      Assertions.assertEquals(JR_FORM_FILLING, result.get(5).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).changeUrl());

      Assertions.assertEquals(COUNSELS_COST, result.get(6).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).assessed());
      Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).changeUrl());

      Assertions.assertEquals(VAT, result.get(7).key());
      Assertions.assertEquals(true, result.get(7).submitted());
      Assertions.assertEquals(false, result.get(7).calculated());

      Assertions.assertEquals(TOTAL, result.get(8).key());
      Assertions.assertNull(result.get(8).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).calculated());
    }

    @Test
    void substantiveHearingBoltOnNotVisibleOnFalse() {
      CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
      CivilClaimDetailsView viewModel = createView(claim);

      claim.setSubstantiveHearing(
          updateClaimFieldSubmittedValue(claim.getSubstantiveHearing(), false));
      List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();
      Assertions.assertFalse(
          result.stream().anyMatch(row -> SUBSTANTIVE_HEARING.equals(row.key())),
          "Rows should not contain substantive hearing");
    }

    @Test
    void rowsRenderedForZeroBoltOnClaimValues() {
      CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
      claim.setAdjournedHearing(
          updateClaimFieldSubmittedValue(claim.getAdjournedHearing(), BigDecimal.ZERO));
      claim.setCmrhTelephone(updateClaimFieldSubmittedValue(claim.getCmrhTelephone(), 0));
      claim.setCmrhOral(updateClaimFieldSubmittedValue(claim.getCmrhOral(), 0));
      claim.setHoInterview(updateClaimFieldSubmittedValue(claim.getHoInterview(), 0));
      claim.setSubstantiveHearing(
          updateClaimFieldSubmittedValue(claim.getSubstantiveHearing(), false));

      CivilClaimDetailsView viewModel = createView(claim);
      List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

      Assertions.assertEquals(9, result.size());

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/detention-travel-and-waiting-costs",
          result.get(4).changeUrl());

      Assertions.assertEquals(JR_FORM_FILLING, result.get(5).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).changeUrl());

      Assertions.assertEquals(COUNSELS_COST, result.get(6).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).assessed());
      Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).changeUrl());

      Assertions.assertEquals(VAT, result.get(7).key());
      Assertions.assertEquals(true, result.get(7).submitted());
      Assertions.assertEquals(false, result.get(7).calculated());

      Assertions.assertEquals(TOTAL, result.get(8).key());
      Assertions.assertNull(result.get(8).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).calculated());
    }
  }

  @Nested
  class GetReviewClaimFieldRowsTests {

    @Test
    void rowsRenderedForClaimValues() {
      CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

      CivilClaimDetailsView viewModel = createView(claim);
      claim.setSubstantiveHearing(
          updateClaimFieldSubmittedValue(claim.getSubstantiveHearing(), true));
      List<ClaimFieldRow> result = viewModel.getReviewClaimFieldRows();

      Assertions.assertEquals(12, result.size());

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/detention-travel-and-waiting-costs",
          result.get(4).changeUrl());

      Assertions.assertEquals(JR_FORM_FILLING, result.get(5).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).changeUrl());

      Assertions.assertEquals(COUNSELS_COST, result.get(6).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).assessed());
      Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).changeUrl());

      Assertions.assertEquals(CMRH_ORAL, result.get(7).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(7).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(7).calculated());

      Assertions.assertEquals(CMRH_TELEPHONE, result.get(8).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(8).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(8).calculated());

      Assertions.assertEquals(HO_INTERVIEW, result.get(9).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(9).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(9).calculated());

      Assertions.assertEquals(SUBSTANTIVE_HEARING, result.get(10).key());
      Assertions.assertEquals(true, result.get(10).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(10).calculated());

      Assertions.assertEquals(ADJOURNED_FEE, result.get(11).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(11).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(11).calculated());
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

      Assertions.assertEquals(7, result.size());

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/detention-travel-and-waiting-costs",
          result.get(4).changeUrl());

      Assertions.assertEquals(JR_FORM_FILLING, result.get(5).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).changeUrl());

      Assertions.assertEquals(COUNSELS_COST, result.get(6).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).assessed());
      Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).changeUrl());
    }

    @Test
    void rowsRenderedForZeroBoltOnClaimValues() {
      CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
      claim.setAdjournedHearing(
          updateClaimFieldSubmittedValue(claim.getAdjournedHearing(), BigDecimal.ZERO));
      claim.setCmrhTelephone(updateClaimFieldSubmittedValue(claim.getCmrhTelephone(), 0));
      claim.setCmrhOral(updateClaimFieldSubmittedValue(claim.getCmrhOral(), 0));
      claim.setHoInterview(updateClaimFieldSubmittedValue(claim.getHoInterview(), 0));
      claim.setSubstantiveHearing(
          updateClaimFieldSubmittedValue(claim.getSubstantiveHearing(), BigDecimal.ZERO));

      CivilClaimDetailsView viewModel = createView(claim);
      List<ClaimFieldRow> result = viewModel.getReviewClaimFieldRows();

      Assertions.assertEquals(7, result.size());

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(DETENTION_TRAVEL_COST, result.get(4).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(4).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(4).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(4).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/detention-travel-and-waiting-costs",
          result.get(4).changeUrl());

      Assertions.assertEquals(JR_FORM_FILLING, result.get(5).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(5).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(5).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(5).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/jr-form-filling-costs", result.get(5).changeUrl());

      Assertions.assertEquals(COUNSELS_COST, result.get(6).key());
      Assertions.assertEquals(BigDecimal.valueOf(100), result.get(6).submitted());
      Assertions.assertEquals(BigDecimal.valueOf(200), result.get(6).calculated());
      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(6).assessed());
      Assertions.assertEquals("/submissions/%s/claims/%s/counsel-costs", result.get(6).changeUrl());
    }
  }

  @Nested
  class GetErrorTests {

    @Test
    void convertFieldsThatNeedAmendingIntoErrors() {
      ClaimField netProfitCostField = MockClaimsFunctions.createNetProfitCostField();
      ClaimField counselField = MockClaimsFunctions.createCounselCostField();
      ClaimField jrFormField = MockClaimsFunctions.createJrFormFillingCostField();

      netProfitCostField.setAssessed(null);
      counselField.setAssessed(null);
      jrFormField.setAssessed(null);

      ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
      ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
      assessedTotalVatField.setAssessed(null);
      assessedTotalInclVatField.setAssessed(null);
      ClaimField allowedTotalVatField = MockClaimsFunctions.createAllowedTotalVatField();
      ClaimField allowedTotalInclVatField = MockClaimsFunctions.createAllowedTotalInclVatField();
      allowedTotalVatField.setAssessed(null);
      allowedTotalInclVatField.setAssessed(null);
      CivilClaimDetails claim = new CivilClaimDetails();
      claim.setNetProfitCost(netProfitCostField);
      claim.setCounselsCost(counselField);
      claim.setJrFormFillingCost(jrFormField);
      claim.setAssessedTotalVat(assessedTotalVatField);
      claim.setAssessedTotalInclVat(assessedTotalInclVatField);
      claim.setAllowedTotalVat(allowedTotalVatField);
      claim.setAllowedTotalInclVat(allowedTotalInclVatField);

      CivilClaimDetailsView viewModel = createView(claim);

      List<ReviewAndAmendFormError> expectedErrors =
          List.of(
              new ReviewAndAmendFormError("profit-cost", "claimSummary.rows.profitCost.error"),
              new ReviewAndAmendFormError(
                  "assessed-total-vat", "claimSummary.rows.assessedTotalVat.error"),
              new ReviewAndAmendFormError(
                  "assessed-total-incl-vat", "claimSummary.rows.assessedTotalInclVat.error"),
              new ReviewAndAmendFormError(
                  "allowed-total-vat", "claimSummary.rows.allowedTotalVat.error"),
              new ReviewAndAmendFormError(
                  "allowed-total-incl-vat", "claimSummary.rows.allowedTotalInclVat.error"));

      Assertions.assertEquals(expectedErrors, viewModel.getErrors());
    }
  }
}
