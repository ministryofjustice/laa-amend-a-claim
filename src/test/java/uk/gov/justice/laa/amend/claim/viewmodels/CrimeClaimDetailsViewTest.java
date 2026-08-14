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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

public class CrimeClaimDetailsViewTest
    extends ClaimDetailsViewTest<CrimeClaimDetails, CrimeClaimDetailsView> {

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
      OffsetDateTime submittedDate = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
      LocalDate caseStartDate = LocalDate.of(2001, 1, 1);
      LocalDate caseEndDate = LocalDate.of(2002, 1, 1);

      CrimeClaimDetails claim = createClaim();
      claim.setClientForename("John");
      claim.setClientSurname("Smith");
      claim.setUniqueFileNumber("unique file number");
      claim.setCaseReferenceNumber("case reference number");
      claim.setProviderName("provider name");
      claim.setOfficeCode("office code");
      claim.setSubmittedDate(submittedDate);
      claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
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

      Map<String, Object> expectedResult = new LinkedHashMap<>();
      expectedResult.put("CLIENT_NAME", "John Smith");
      expectedResult.put("UNIQUE_FILE_NUMBER", "unique file number");
      expectedResult.put("PROVIDER_NAME", "provider name");
      expectedResult.put("OFFICE_CODE", "office code");
      expectedResult.put("SUBMITTED_DATE", submittedDate);
      expectedResult.put(
          "AREA_OF_LAW", new ThymeleafMessage(AreaOfLaw.CRIME_LOWER.getMessageKey()));
      expectedResult.put("CATEGORY_OF_LAW", "category of law");
      expectedResult.put("FEE_CODE", "fee code");
      expectedResult.put("FEE_CODE_DESCRIPTION", "fee code description");
      expectedResult.put("POLICE_STATION_COURT_PRISON_ID", "police station court prison id");
      expectedResult.put("SCHEME_ID", "scheme id");
      expectedResult.put("MATTER_TYPE_CODE", "matter type code");
      expectedResult.put("CASE_START_DATE", caseStartDate);
      expectedResult.put("CASE_CONCLUDED_DATE", caseEndDate);
      expectedResult.put("ESCAPED", true);
      expectedResult.put("VAT_REQUESTED", false);

      CrimeClaimDetailsView viewModel = createView(claim);
      Map<String, Object> result = viewModel.getSummaryRows();
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

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(TRAVEL_COSTS, result.get(4).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/travel-costs", result.get(4).changeUrl());

      Assertions.assertEquals(WAITING_COSTS, result.get(5).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/waiting-costs", result.get(5).changeUrl());

      Assertions.assertEquals(VAT, result.get(6).key());
    }

    @Test
    void rowsRenderedForClaimValuesWhenClaimDoesNotHaveAnAssessment() {
      CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
      claim.setTotalAmount(CalculatedTotalClaimField.builder().build());
      claim.setHasAssessment(false);

      CrimeClaimDetailsView viewModel = createView(claim);
      List<ClaimFieldRow> result = viewModel.getSummaryClaimFieldRows();

      Assertions.assertEquals(8, result.size());

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(TRAVEL_COSTS, result.get(4).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/travel-costs", result.get(4).changeUrl());

      Assertions.assertEquals(WAITING_COSTS, result.get(5).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/waiting-costs", result.get(5).changeUrl());

      Assertions.assertEquals(VAT, result.get(6).key());

      Assertions.assertEquals(TOTAL, result.get(7).key());
    }
  }

  @Nested
  class GetReviewClaimFieldRowsTests {
    @Test
    void rowsRenderedForClaimValues() {
      CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();

      CrimeClaimDetailsView viewModel = createView(claim);
      List<ClaimFieldRow> result = viewModel.getReviewClaimFieldRows();

      Assertions.assertEquals(6, result.size());

      Assertions.assertEquals(FIXED_FEE, result.get(0).key());

      Assertions.assertEquals(NET_PROFIT_COST, result.get(1).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/profit-costs", result.get(1).changeUrl());

      Assertions.assertEquals(NET_DISBURSEMENTS_COST, result.get(2).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/disbursements", result.get(2).changeUrl());

      Assertions.assertEquals(DISBURSEMENT_VAT, result.get(3).key());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/disbursements-vat", result.get(3).changeUrl());

      Assertions.assertEquals(TRAVEL_COSTS, result.get(4).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/travel-costs", result.get(4).changeUrl());

      Assertions.assertEquals(WAITING_COSTS, result.get(5).key());
      Assertions.assertEquals("/submissions/%s/claims/%s/waiting-costs", result.get(5).changeUrl());
    }
  }

  @Nested
  class GetErrorTests {

    @Test
    void convertFieldsThatNeedAmendingIntoErrors() {
      ClaimField netProfitCostField = MockClaimsFunctions.createNetProfitCostField();
      ClaimField travelCostsField = MockClaimsFunctions.createTravelCostField();
      ClaimField waitingCostsField = MockClaimsFunctions.createWaitingCostField();
      netProfitCostField.setAssessed(null);
      travelCostsField.setAssessed(null);
      waitingCostsField.setAssessed(null);

      ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
      ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
      assessedTotalVatField.setAssessed(null);
      assessedTotalInclVatField.setAssessed(null);
      ClaimField allowedTotalVatField = MockClaimsFunctions.createAllowedTotalVatField();
      ClaimField allowedTotalInclVatField = MockClaimsFunctions.createAllowedTotalInclVatField();
      allowedTotalVatField.setAssessed(null);
      allowedTotalInclVatField.setAssessed(null);

      CrimeClaimDetails claim = new CrimeClaimDetails();
      claim.setNetProfitCost(netProfitCostField);
      claim.setTravelCosts(travelCostsField);
      claim.setWaitingCosts(waitingCostsField);
      claim.setAssessedTotalVat(assessedTotalVatField);
      claim.setAssessedTotalInclVat(assessedTotalInclVatField);
      claim.setAllowedTotalVat(allowedTotalVatField);
      claim.setAllowedTotalInclVat(allowedTotalInclVatField);

      CrimeClaimDetailsView viewModel = new CrimeClaimDetailsView(claim);

      List<ReviewAndAmendFormError> expectedErrors =
          List.of(
              new ReviewAndAmendFormError("profit-cost", "claimSummary.rows.PROFIT_COST.error"),
              new ReviewAndAmendFormError(
                  "assessed-total-vat", "claimSummary.rows.ASSESSED_TOTAL_VAT.error"),
              new ReviewAndAmendFormError(
                  "assessed-total-incl-vat", "claimSummary.rows.ASSESSED_TOTAL_INCL_VAT.error"),
              new ReviewAndAmendFormError(
                  "allowed-total-vat", "claimSummary.rows.ALLOWED_TOTAL_VAT.error"),
              new ReviewAndAmendFormError(
                  "allowed-total-incl-vat", "claimSummary.rows.ALLOWED_TOTAL_INCL_VAT.error"));

      Assertions.assertEquals(expectedErrors, viewModel.getErrors());
    }
  }
}
