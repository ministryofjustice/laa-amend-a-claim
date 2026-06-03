package uk.gov.justice.laa.amend.claim.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AreaOfLaw;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

@SpringJUnitConfig
@ContextConfiguration(classes = {ClaimMapperImpl.class, ClaimMapperHelper.class})
class ClaimMapperTest {

  @Autowired private ClaimMapper mapper;

  @Test
  void testMapTotalAmount() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setTotalAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getTotalAmount();
    assertEquals(AmendClaimConstants.Label.TOTAL, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void testMapFixedFee() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setFixedFeeAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getFixedFee();
    assertEquals(AmendClaimConstants.Label.FIXED_FEE, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapNetProfitCost() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setNetProfitCostsAmount(BigDecimal.valueOf(100));
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setNetProfitCostsAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getNetProfitCost();
    assertEquals(AmendClaimConstants.Label.NET_PROFIT_COST, claimField.getKey());
    assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
  }

  @Test
  void mapVatClaimedWhenFeeCalculationResponseIsNull() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setIsVatApplicable(false);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getVatClaimed();
    assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
    assertEquals(false, claimField.getSubmitted());
    assertEquals(false, claimField.getCalculated());
    assertEquals(false, claimField.getAssessed());
  }

  @Test
  void mapVatClaimedWhenVatIndicatorIsNull() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setIsVatApplicable(true);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    response.setFeeCalculationResponse(feeCalc);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getVatClaimed();
    assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
    assertEquals(true, claimField.getSubmitted());
    assertEquals(false, claimField.getCalculated());
    assertEquals(true, claimField.getAssessed());
  }

  @Test
  void mapVatClaimedWhenVatIndicatorIsFalse() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setIsVatApplicable(true);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setVatIndicator(false);
    response.setFeeCalculationResponse(feeCalc);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getVatClaimed();
    assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
    assertEquals(true, claimField.getSubmitted());
    assertEquals(false, claimField.getCalculated());
    assertEquals(true, claimField.getAssessed());
  }

  @Test
  void mapVatClaimedWhenVatIndicatorIsTrue() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setIsVatApplicable(true);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setVatIndicator(true);
    response.setFeeCalculationResponse(feeCalc);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getVatClaimed();
    assertEquals(AmendClaimConstants.Label.VAT, claimField.getKey());
    assertEquals(true, claimField.getSubmitted());
    assertEquals(true, claimField.getCalculated());
    assertEquals(true, claimField.getAssessed());
  }

  @Test
  void mapNetDisbursementAmount() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setNetDisbursementAmount(BigDecimal.valueOf(100));
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setDisbursementAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getNetDisbursementAmount();
    assertEquals(AmendClaimConstants.Label.NET_DISBURSEMENTS_COST, claimField.getKey());
    assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
  }

  @Test
  void mapDisbursementVatAmount() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setDisbursementsVatAmount(BigDecimal.valueOf(100));
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setDisbursementVatAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getDisbursementVatAmount();
    assertEquals(AmendClaimConstants.Label.DISBURSEMENT_VAT, claimField.getKey());
    assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
  }

  @Test
  void mapCounselsCost() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setNetCounselCostsAmount(BigDecimal.valueOf(100));
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setNetCostOfCounselAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getCounselsCost();
    assertEquals(AmendClaimConstants.Label.COUNSELS_COST, claimField.getKey());
    assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
  }

  @Test
  void mapDetentionTravelWaitingCosts() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setDetentionTravelWaitingCostsAmount(BigDecimal.valueOf(100));
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setDetentionTravelAndWaitingCostsAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getDetentionTravelWaitingCosts();
    assertEquals(AmendClaimConstants.Label.DETENTION_TRAVEL_COST, claimField.getKey());
    assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
  }

  @Test
  void mapJrFormFillingCost() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setJrFormFillingAmount(BigDecimal.valueOf(100));
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setJrFormFillingAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getJrFormFillingCost();
    assertEquals(AmendClaimConstants.Label.JR_FORM_FILLING, claimField.getKey());
    assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
  }

  @Test
  void mapAdjournedHearingFee() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setAdjournedHearingFeeAmount(100);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    BoltOnPatch bolt = new BoltOnPatch();
    bolt.setBoltOnAdjournedHearingFee(BigDecimal.valueOf(120));
    feeCalc.setBoltOnDetails(bolt);
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAdjournedHearing();
    assertEquals(AmendClaimConstants.Label.ADJOURNED_FEE, claimField.getKey());
    assertEquals(100, claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(100, claimField.getAssessed());
  }

  @Test
  void mapCmrhTelephone() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setCmrhTelephoneCount(100);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    BoltOnPatch bolt = new BoltOnPatch();
    bolt.setBoltOnCmrhTelephoneFee(BigDecimal.valueOf(120));
    feeCalc.setBoltOnDetails(bolt);
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getCmrhTelephone();
    assertEquals(AmendClaimConstants.Label.CMRH_TELEPHONE, claimField.getKey());
    assertEquals(100, claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(100, claimField.getAssessed());
  }

  @Test
  void mapCmrhOral() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setCmrhOralCount(100);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    BoltOnPatch bolt = new BoltOnPatch();
    bolt.setBoltOnCmrhOralFee(BigDecimal.valueOf(120));
    feeCalc.setBoltOnDetails(bolt);
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getCmrhOral();
    assertEquals(AmendClaimConstants.Label.CMRH_ORAL, claimField.getKey());
    assertEquals(100, claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(100, claimField.getAssessed());
  }

  @Test
  void mapHoInterview() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setHoInterview(100);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    BoltOnPatch bolt = new BoltOnPatch();
    bolt.setBoltOnHomeOfficeInterviewFee(BigDecimal.valueOf(120));
    feeCalc.setBoltOnDetails(bolt);
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getHoInterview();
    assertEquals(AmendClaimConstants.Label.HO_INTERVIEW, claimField.getKey());
    assertEquals(100, claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(100, claimField.getAssessed());
  }

  @Test
  void mapSubstantiveHearing() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setIsSubstantiveHearing(true);
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    BoltOnPatch bolt = new BoltOnPatch();
    bolt.setBoltOnSubstantiveHearingFee(BigDecimal.valueOf(120));
    feeCalc.setBoltOnDetails(bolt);
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getSubstantiveHearing();
    assertEquals(AmendClaimConstants.Label.SUBSTANTIVE_HEARING, claimField.getKey());
    assertEquals(true, claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(true, claimField.getAssessed());
  }

  @Test
  void mapTravelCosts() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setTravelWaitingCostsAmount(BigDecimal.valueOf(100));
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setNetTravelCostsAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getTravelCosts();
    assertEquals(AmendClaimConstants.Label.TRAVEL_COSTS, claimField.getKey());
    assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
  }

  @Test
  void mapWaitingCosts() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setNetWaitingCostsAmount(BigDecimal.valueOf(100));
    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setNetWaitingCostsAmount(BigDecimal.valueOf(120));
    response.setFeeCalculationResponse(feeCalc);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getWaitingCosts();
    assertEquals(AmendClaimConstants.Label.WAITING_COSTS, claimField.getKey());
    assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    assertEquals(BigDecimal.valueOf(100), claimField.getAssessed());
  }

  @Test
  void mapAssessedTotalVat() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    AssessedClaimField claimField = (AssessedClaimField) claim.getAssessedTotalVat();
    assertEquals(AmendClaimConstants.Label.ASSESSED_TOTAL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertNull(claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAssessedTotalInclVat() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    AssessedClaimField claimField = (AssessedClaimField) claim.getAssessedTotalInclVat();
    assertEquals(AmendClaimConstants.Label.ASSESSED_TOTAL_INCL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertNull(claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAllowedTotalVat() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
    feeCalculationPatch.setCalculatedVatAmount(BigDecimal.valueOf(100));
    feeCalculationPatch.setDisbursementVatAmount(BigDecimal.valueOf(200));
    response.setFeeCalculationResponse(feeCalculationPatch);

    CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAllowedTotalVat();
    assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(300), claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAllowedTotalVatWhenFeeCalculationIsNull() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAllowedTotalVat();
    assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertNull(claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAllowedTotalVatWhenCalculatedVatAmountAndDisbursementVatAmountAreNull() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
    response.setFeeCalculationResponse(feeCalculationPatch);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAllowedTotalVat();
    assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertNull(claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAllowedTotalVatWhenCalculatedVatAmountIsNull() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
    feeCalculationPatch.setCalculatedVatAmount(BigDecimal.valueOf(100));
    response.setFeeCalculationResponse(feeCalculationPatch);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAllowedTotalVat();
    assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAllowedTotalVatWhenDisbursementVatAmountIsNull() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
    feeCalculationPatch.setDisbursementVatAmount(BigDecimal.valueOf(100));
    response.setFeeCalculationResponse(feeCalculationPatch);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAllowedTotalVat();
    assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAllowedTotalInclVat() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    FeeCalculationPatch feeCalculationPatch = new FeeCalculationPatch();
    feeCalculationPatch.setTotalAmount(BigDecimal.valueOf(100));
    response.setFeeCalculationResponse(feeCalculationPatch);

    CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAllowedTotalInclVat();
    assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertEquals(BigDecimal.valueOf(100), claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAllowedTotalInclVatWhenFeeCalculationIsNull() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAllowedTotalInclVat();
    assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertNull(claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void mapAllowedTotalInclVatWhenTotalAmountIsNull() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);

    var claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    ClaimField claimField = claim.getAllowedTotalInclVat();
    assertEquals(AmendClaimConstants.Label.ALLOWED_TOTAL_INCL_VAT, claimField.getKey());
    assertNull(claimField.getSubmitted());
    assertNull(claimField.getCalculated());
    assertNull(claimField.getAssessed());
  }

  @Test
  void testMapToCivilClaim() {
    var response = createClaimResponse(AreaOfLaw.LEGAL_HELP);
    response.setUniqueFileNumber("UFN123");
    response.setCaseReferenceNumber("CASE456");
    response.setClientSurname("Doe");
    response.setClientForename("John");
    response.setGenderCode("genderCode");
    response.setEthnicityCode("ethnicityCode");
    response.setDisabilityCode("disabilityCode");
    response.setUniqueClientNumber("21121985/J/DOE");
    response.setCaseStartDate("2025-01-01");
    response.setCaseConcludedDate("2025-02-01");
    response.setOfficeCode("0P322F");
    response.setDateSubmitted(OffsetDateTime.parse("2025-01-10T14:30:00+02:00"));
    response.setStatus(ClaimStatus.VALID);
    response.setClientDateOfBirth("1970-01-01");
    response.setClientPostcode("AB12 ABC");
    response.setIsEligibleClient(false);
    response.setClientTypeCode("clientType");
    response.setHomeOfficeClientNumber("HOUCN123");

    UUID claimSummaryFeeId = UUID.randomUUID();

    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setClaimSummaryFeeId(claimSummaryFeeId);
    feeCalc.setFeeCode("FeeCode");
    feeCalc.setFeeCodeDescription("FeeCodeDesc");
    feeCalc.setCategoryOfLaw("Civil");
    BoltOnPatch boltOn = new BoltOnPatch();
    boltOn.setEscapeCaseFlag(true);
    feeCalc.setBoltOnDetails(boltOn);
    response.setFeeCalculationResponse(feeCalc);

    response.setMatterTypeCode("MT1+MT2");

    CivilClaimDetails claim = (CivilClaimDetails) mapper.mapToClaimDetails(response);

    assertEquals("UFN123", claim.getUniqueFileNumber());
    assertEquals("CASE456", claim.getCaseReferenceNumber());
    assertEquals("Doe", claim.getClientSurname());
    assertEquals("John", claim.getClientForename());
    assertEquals("genderCode", claim.getClientGender());
    assertEquals("ethnicityCode", claim.getClientEthnicity());
    assertEquals("disabilityCode", claim.getClientDisability());
    assertEquals("21121985/J/DOE", claim.getUniqueClientNumber());
    assertEquals("FeeCode", claim.getFeeCode());
    assertEquals("FeeCodeDesc", claim.getFeeCodeDescription());
    assertEquals("Civil", claim.getCategoryOfLaw());
    assertTrue(claim.getEscaped());
    assertEquals("MT1+MT2", claim.getMatterTypeCode());
    assertEquals(claimSummaryFeeId, claim.getClaimSummaryFeeId());
    assertEquals(uk.gov.justice.laa.amend.claim.models.AreaOfLaw.LEGAL_HELP, claim.getAreaOfLaw());
    assertEquals("0P322F", claim.getOfficeCode());
    assertNull(claim.getProviderName());
    assertEquals(OffsetDateTime.parse("2025-01-10T14:30:00+02:00"), claim.getSubmittedDate());
    assertEquals(ClaimStatus.VALID, claim.getStatus());
    assertEquals(LocalDate.parse("1970-01-01"), claim.getClientDateOfBirth());
    assertEquals("AB12 ABC", claim.getClientPostcode());
    assertEquals(false, claim.getEligibleClient());
    assertEquals("clientType", claim.getClientType());
    assertEquals("HOUCN123", claim.getHoUcn());
  }

  @Test
  void testMapToCrimeClaim() {
    var response = createClaimResponse(AreaOfLaw.CRIME_LOWER);
    response.setUniqueFileNumber("UFN123");
    response.setCaseReferenceNumber("CASE456");
    response.setClientSurname("Doe");
    response.setClientForename("John");
    response.setCaseStartDate("2025-01-01");
    response.setCaseConcludedDate("2025-02-01");
    response.setOfficeCode("0P322F");
    response.setDateSubmitted(OffsetDateTime.parse("2025-01-10T14:30:00+02:00"));
    response.setStatus(ClaimStatus.VALID);

    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setFeeCode("FeeCode");
    feeCalc.setFeeCodeDescription("FeeCodeDesc");
    feeCalc.setCategoryOfLaw("Civil");
    BoltOnPatch boltOn = new BoltOnPatch();
    boltOn.setEscapeCaseFlag(true);
    feeCalc.setBoltOnDetails(boltOn);
    response.setFeeCalculationResponse(feeCalc);

    response.setMatterTypeCode("INVC");
    response.setPoliceStationCourtPrisonId("PrisonCode");
    response.setSchemeId("SchemeId");
    response.setStageReachedCode("StageReachedCode");
    response.setOutcomeCode("OutcomeCode");
    response.setRepresentationOrderDate("2025-01-01");
    response.setStandardFeeCategoryCode("StandardFeeCategoryCode");
    response.setSuspectsDefendantsCount(1);
    response.setPoliceStationCourtAttendancesCount(2);
    response.setMaatId("MaatId");
    response.setDsccNumber("DsccNumber");
    response.setPrisonLawPriorApprovalNumber("PrisonLawPriorApprovalNumber");
    response.setIsDutySolicitor(true);
    response.setIsYouthCourt(true);
    CrimeClaimDetails claim = (CrimeClaimDetails) mapper.mapToClaimDetails(response);

    assertEquals("UFN123", claim.getUniqueFileNumber());
    assertEquals("CASE456", claim.getCaseReferenceNumber());
    assertEquals("Doe", claim.getClientSurname());
    assertEquals("John", claim.getClientForename());
    assertEquals("FeeCode", claim.getFeeCode());
    assertEquals("FeeCodeDesc", claim.getFeeCodeDescription());
    assertEquals("Civil", claim.getCategoryOfLaw());
    assertTrue(claim.getEscaped());
    assertEquals("INVC", claim.getMatterTypeCode());
    assertEquals("PrisonCode", claim.getPoliceStationCourtPrisonId());
    assertEquals("SchemeId", claim.getSchemeId());
    assertEquals(ClaimStatus.VALID, claim.getStatus());
    assertEquals("0P322F", claim.getOfficeCode());
    assertEquals("StageReachedCode", claim.getStageReached());
    assertEquals("OutcomeCode", claim.getOutcome());
    assertEquals(LocalDate.parse("2025-01-01"), claim.getRepresentationOrderDate());
    assertEquals("StandardFeeCategoryCode", claim.getStandardFeeCategory());
    assertEquals(1, claim.getSuspectsDefendantsCount());
    assertEquals(2, claim.getPoliceStationCourtAttendancesCount());
    assertEquals("MaatId", claim.getMaatId());
    assertEquals("DsccNumber", claim.getDsccNumber());
    assertEquals("PrisonLawPriorApprovalNumber", claim.getPrisonLawPriorApprovalNumber());
    assertEquals(true, claim.getIsDutySolicitor());
    assertEquals(true, claim.getIsYouthCourt());

    assertEquals(OffsetDateTime.parse("2025-01-10T14:30:00+02:00"), claim.getSubmittedDate());
  }

  @Test
  void testMapToMediationClaim() {
    var response = createClaimResponse(AreaOfLaw.MEDIATION);
    response.setUniqueFileNumber("UFN123");
    response.setCaseReferenceNumber("CASE456");

    response.setClientSurname("Doe");
    response.setClientForename("John");
    response.setClientDateOfBirth("1970-01-01");
    response.setClientPostcode("AB12 ABC");
    response.setGenderCode("genderCode");
    response.setEthnicityCode("ethnicityCode");
    response.setDisabilityCode("disabilityCode");
    response.setUniqueClientNumber("21121985/J/DOE");
    response.setIsLegallyAided(true);
    response.setIsPostalApplicationAccepted(false);

    response.setClient2Surname("Poe");
    response.setClient2Forename("Jane");
    response.setClient2DateOfBirth("1971-01-01");
    response.setClient2Postcode("CD45 CDE");
    response.setClient2GenderCode("genderCode2");
    response.setClient2EthnicityCode("ethnicityCode2");
    response.setClient2DisabilityCode("disabilityCode2");
    response.setClient2Ucn("21121985/J/POE");
    response.setClient2IsLegallyAided(false);
    response.setIsClient2PostalApplicationAccepted(true);

    response.setCaseStartDate("2025-01-01");
    response.setCaseConcludedDate("2025-02-01");
    response.setOfficeCode("0P322F");
    response.setDateSubmitted(OffsetDateTime.parse("2025-01-10T14:30:00+02:00"));
    response.setStatus(ClaimStatus.VALID);

    UUID claimSummaryFeeId = UUID.randomUUID();

    FeeCalculationPatch feeCalc = new FeeCalculationPatch();
    feeCalc.setClaimSummaryFeeId(claimSummaryFeeId);
    feeCalc.setFeeCode("FeeCode");
    feeCalc.setFeeCodeDescription("FeeCodeDesc");
    feeCalc.setCategoryOfLaw("Civil");
    BoltOnPatch boltOn = new BoltOnPatch();
    boltOn.setEscapeCaseFlag(true);
    feeCalc.setBoltOnDetails(boltOn);
    response.setFeeCalculationResponse(feeCalc);

    response.setMatterTypeCode("MT1+MT2");

    MediationClaimDetails claim = (MediationClaimDetails) mapper.mapToClaimDetails(response);

    assertEquals("UFN123", claim.getUniqueFileNumber());
    assertEquals("CASE456", claim.getCaseReferenceNumber());

    assertEquals("Doe", claim.getClientSurname());
    assertEquals("John", claim.getClientForename());
    assertEquals(LocalDate.parse("1970-01-01"), claim.getClientDateOfBirth());
    assertEquals("AB12 ABC", claim.getClientPostcode());
    assertEquals("genderCode", claim.getClientGender());
    assertEquals("ethnicityCode", claim.getClientEthnicity());
    assertEquals("disabilityCode", claim.getClientDisability());
    assertEquals("21121985/J/DOE", claim.getUniqueClientNumber());
    assertEquals(true, claim.getIsClientLegallyAided());
    assertEquals(false, claim.getIsClientPostalApplicationAccepted());

    assertEquals("Poe", claim.getClient2Surname());
    assertEquals("Jane", claim.getClient2Forename());
    assertEquals(LocalDate.parse("1971-01-01"), claim.getClient2DateOfBirth());
    assertEquals("CD45 CDE", claim.getClient2Postcode());
    assertEquals("genderCode2", claim.getClient2Gender());
    assertEquals("ethnicityCode2", claim.getClient2Ethnicity());
    assertEquals("disabilityCode2", claim.getClient2Disability());
    assertEquals("21121985/J/POE", claim.getClient2Ucn());
    assertEquals(false, claim.getIsClient2LegallyAided());
    assertEquals(true, claim.getIsClient2PostalApplicationAccepted());

    assertEquals("FeeCode", claim.getFeeCode());
    assertEquals("FeeCodeDesc", claim.getFeeCodeDescription());
    assertEquals("Civil", claim.getCategoryOfLaw());
    assertTrue(claim.getEscaped());
    assertEquals("MT1+MT2", claim.getMatterTypeCode());
    assertEquals(claimSummaryFeeId, claim.getClaimSummaryFeeId());
    assertEquals(uk.gov.justice.laa.amend.claim.models.AreaOfLaw.MEDIATION, claim.getAreaOfLaw());
    assertEquals("0P322F", claim.getOfficeCode());
    assertNull(claim.getProviderName());
    assertEquals(OffsetDateTime.parse("2025-01-10T14:30:00+02:00"), claim.getSubmittedDate());
    assertEquals(ClaimStatus.VALID, claim.getStatus());
  }

  @Test
  void testMapUniqueFileId() {
    var response = createClaimResponse(AreaOfLaw.MEDIATION);
    response.setUniqueCaseId("112233/001");

    var claim = mapper.mapToClaimDetails(response);

    String claimField = claim.getUniqueCaseId();
    assertNotNull(claimField);
    assertEquals("112233/001", claimField);
  }

  private static ClaimResponseV2 createClaimResponse(AreaOfLaw areaOfLaw) {
    var response = new ClaimResponseV2();
    response.setAreaOfLaw(areaOfLaw);
    return response;
  }
}
