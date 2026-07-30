package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(controllers = StartAmendmentsController.class)
class StartAmendmentsControllerTest extends BaseControllerTest {

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();
  }

  @Test
  void savesFormsIntoSessionThenRedirects() throws Exception {
    CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode("ABC");
    claim.setMatterTypeCode("MAT1");
    MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
    session.setAttribute(claimId.toString(), claim);

    claim.setClientForename("forename");
    claim.setClientSurname("surname");
    claim.setClientGender("gender");
    claim.setClientEthnicity("ethnicity");
    claim.setClientDisability("disability");

    var client1Rows =
        Map.of(
            "INITIAL", claim.getClientForename(),
            "SURNAME", claim.getClientSurname(),
            "GENDER", claim.getClientGender(),
            "ETHNICITY", claim.getClientEthnicity(),
            "DISABILITY", claim.getClientDisability());
    var client1Form = new AmendmentForm();
    client1Form.setInputs(client1Rows);

    var caseTypeRows =
        Map.of("FEE_CODE", claim.getFeeCode(), "MATTER_TYPE_CODE", claim.getMatterTypeCode());
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    claim.setStageReached("stagereached");
    claim.setUniqueFileNumber("uniqueFileNumber");
    claim.setRepresentationOrderDate(LocalDate.of(2000, 1, 1));
    claim.setCaseEndDate(LocalDate.of(2001, 1, 1));
    claim.setStandardFeeCategory("standardFeeCategory");
    claim.setOutcome("outcome");
    claim.setSuspectsDefendantsCount(1);
    claim.setPoliceStationCourtAttendancesCount(2);
    claim.setPoliceStationCourtPrisonId("policeStationCourtPrisonId");
    claim.setSchemeId("schemeId");
    claim.setDsccNumber("dsccNumber");
    claim.setMaatId("maatId");
    claim.setPrisonLawPriorApprovalNumber("prisonLawPriorApprovalNumber");
    claim.setIsDutySolicitor(true);
    claim.setIsYouthCourt(true);
    Map<String, String> caseDetailsRows = new HashMap<>();
    caseDetailsRows.put("STAGE_REACHED", claim.getStageReached());
    caseDetailsRows.put("UNIQUE_FILE_NUMBER", claim.getUniqueFileNumber());
    caseDetailsRows.put("REPRESENTATION_ORDER_DATE-day", "1");
    caseDetailsRows.put("REPRESENTATION_ORDER_DATE-month", "1");
    caseDetailsRows.put("REPRESENTATION_ORDER_DATE-year", "2000");
    caseDetailsRows.put("CASE_CONCLUDED_DATE-day", "1");
    caseDetailsRows.put("CASE_CONCLUDED_DATE-month", "1");
    caseDetailsRows.put("CASE_CONCLUDED_DATE-year", "2001");
    caseDetailsRows.put("STANDARD_FEE_CATEGORY", claim.getStandardFeeCategory());
    caseDetailsRows.put("OUTCOME_FOR_CLIENT", claim.getOutcome());
    caseDetailsRows.put("SUSPECTS_DEFENDANTS_COUNT", "1");
    caseDetailsRows.put("POLICE_STATION_COURT_ATTENDANCES_COUNT", "2");
    caseDetailsRows.put("POLICE_STATION_COURT_PRISON_ID", claim.getPoliceStationCourtPrisonId());
    caseDetailsRows.put("SCHEME_ID", claim.getSchemeId());
    caseDetailsRows.put("DSCC_NUMBER", claim.getDsccNumber());
    caseDetailsRows.put("MAAT_ID", claim.getMaatId());
    caseDetailsRows.put(
        "PRISON_LAW_PRIOR_APPROVAL_NUMBER", claim.getPrisonLawPriorApprovalNumber());
    caseDetailsRows.put("IS_DUTY_SOLICITOR", "true");
    caseDetailsRows.put("IS_YOUTH_COURT", "true");

    var caseDetailsForm = new AmendmentForm();
    caseDetailsForm.setInputs(caseDetailsRows);

    AmendmentForms forms = new AmendmentForms(client1Form, caseTypeForm, caseDetailsForm);

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildRedirectPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), forms));
  }

  @Test
  void savesMediationFormsIntoSessionThenRedirects() throws Exception {
    MediationClaimDetails claim = MockClaimsFunctions.createMockMediationClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode("ABC");
    claim.setMatterType("MAT1");
    MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
    session.setAttribute(claimId.toString(), claim);

    claim.setClientForename("forename");
    claim.setClientSurname("surname");
    claim.setClientGender("gender");
    claim.setClientEthnicity("ethnicity");
    claim.setClientDisability("disability");
    claim.setClientDateOfBirth(LocalDate.of(1990, 5, 15));
    claim.setUniqueClientNumber("uniqueClientNumber");
    claim.setClientPostcode("PC1 1AA");
    claim.setIsClientLegallyAided(true);
    claim.setIsClientPostalApplicationAccepted(false);

    Map<String, String> client1Rows = new HashMap<>();
    client1Rows.put("FORENAME", claim.getClientForename());
    client1Rows.put("SURNAME", claim.getClientSurname());
    client1Rows.put("DATE_OF_BIRTH-day", "15");
    client1Rows.put("DATE_OF_BIRTH-month", "5");
    client1Rows.put("DATE_OF_BIRTH-year", "1990");
    client1Rows.put("UNIQUE_CLIENT_NUMBER", claim.getUniqueClientNumber());
    client1Rows.put("POSTCODE", claim.getClientPostcode());
    client1Rows.put("GENDER", claim.getClientGender());
    client1Rows.put("ETHNICITY", claim.getClientEthnicity());
    client1Rows.put("DISABILITY", claim.getClientDisability());
    client1Rows.put("IS_LEGALLY_AIDED", "true");
    client1Rows.put("IS_POSTAL_APPLICATION_ACCEPTED", "false");
    var client1Form = new AmendmentForm();
    client1Form.setInputs(client1Rows);

    claim.setClient2Forename("forename2");
    claim.setClient2Surname("surname2");
    claim.setClient2DateOfBirth(LocalDate.of(1985, 3, 20));
    claim.setClient2Ucn("client2Ucn");
    claim.setClient2Postcode("PC2 2BB");
    claim.setClient2Gender("gender2");
    claim.setClient2Ethnicity("ethnicity2");
    claim.setClient2Disability("disability2");
    claim.setIsClient2LegallyAided(false);
    claim.setIsClient2PostalApplicationAccepted(true);

    Map<String, String> client2Rows = new HashMap<>();
    client2Rows.put("CLIENT_2_FORENAME", claim.getClient2Forename());
    client2Rows.put("CLIENT_2_SURNAME", claim.getClient2Surname());
    client2Rows.put("CLIENT_2_DATE_OF_BIRTH-day", "20");
    client2Rows.put("CLIENT_2_DATE_OF_BIRTH-month", "3");
    client2Rows.put("CLIENT_2_DATE_OF_BIRTH-year", "1985");
    client2Rows.put("CLIENT_2_UCN", claim.getClient2Ucn());
    client2Rows.put("CLIENT_2_POSTCODE", claim.getClient2Postcode());
    client2Rows.put("CLIENT_2_GENDER", claim.getClient2Gender());
    client2Rows.put("CLIENT_2_ETHNICITY", claim.getClient2Ethnicity());
    client2Rows.put("CLIENT_2_DISABILITY", claim.getClient2Disability());
    client2Rows.put("IS_CLIENT_2_LEGALLY_AIDED", "false");
    client2Rows.put("IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED", "true");
    var client2Form = new AmendmentForm();
    client2Form.setInputs(client2Rows);

    var caseTypeRows =
        Map.of("FEE_CODE", claim.getFeeCode(), "MATTER_TYPE_CODE", claim.getMatterType());
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    claim.setAreaOfLaw(AreaOfLaw.MEDIATION);
    claim.setCaseReferenceNumber("caseReferenceNumber");
    claim.setCaseStartDate(LocalDate.of(2020, 6, 10));
    claim.setCaseId("caseId123");
    claim.setUniqueCaseId("uniqueCaseId123");
    claim.setCaseEndDate(LocalDate.of(2001, 1, 1));
    claim.setMediationSessionsCount(3);
    claim.setMediationTimeMinutes(45);
    claim.setOutcome("outcome");
    claim.setOutreachLocation("outreachLocation");
    claim.setReferralSource("referralSource");
    claim.setScheduleReference("scheduleReference");

    Map<String, String> caseDetailsRows = new HashMap<>();
    caseDetailsRows.put("CASE_REFERENCE_NUMBER", claim.getCaseReferenceNumber());
    caseDetailsRows.put("CASE_START_DATE-day", "10");
    caseDetailsRows.put("CASE_START_DATE-month", "6");
    caseDetailsRows.put("CASE_START_DATE-year", "2020");
    caseDetailsRows.put("CLAIM_ID", claim.getCaseId());
    caseDetailsRows.put("UNIQUE_CASE_ID", claim.getUniqueCaseId());
    caseDetailsRows.put("CASE_CONCLUDED_DATE-day", "1");
    caseDetailsRows.put("CASE_CONCLUDED_DATE-month", "1");
    caseDetailsRows.put("CASE_CONCLUDED_DATE-year", "2001");
    caseDetailsRows.put("MEDIATION_SESSIONS_COUNT", "3");
    caseDetailsRows.put("MEDIATION_TIME_MINUTES", "45");
    caseDetailsRows.put("OUTCOME", claim.getOutcome());
    caseDetailsRows.put("OUTREACH_LOCATION", claim.getOutreachLocation());
    caseDetailsRows.put("REFERRAL_SOURCE", claim.getReferralSource());
    caseDetailsRows.put("SCHEDULE_REFERENCE", claim.getScheduleReference());

    var caseDetailsForm = new AmendmentForm();
    caseDetailsForm.setInputs(caseDetailsRows);

    AmendmentForms forms =
        new AmendmentForms(client1Form, client2Form, caseTypeForm, caseDetailsForm);

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildRedirectPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), forms));
  }

  private String buildPath() {
    return "/submissions/%s/claims/%s/amendments".formatted(submissionId, claimId);
  }

  private String buildRedirectPath() {
    return "/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }
}
