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
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(controllers = StartController.class)
class StartControllerTest extends BaseControllerTest {

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private CrimeClaimDetails claim;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();
    claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode("ABC");
    claim.setMatterTypeCode("MAT1");
    MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
    session.setAttribute(claimId.toString(), claim);
  }

  @Test
  void savesFormsIntoSessionThenRedirects() throws Exception {
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
    caseDetailsRows.put("REPRESENTATION_ORDER_DATE", "TODO");
    caseDetailsRows.put("CASE_CONCLUDED_DATE", "TODO");
    caseDetailsRows.put("STANDARD_FEE_CATEGORY", claim.getStandardFeeCategory());
    caseDetailsRows.put("OUTCOME_FOR_CLIENT", claim.getOutcome());
    caseDetailsRows.put("SUSPECTS_DEFENDANTS_COUNT", "TODO");
    caseDetailsRows.put("POLICE_STATION_COURT_ATTENDANCES_COUNT", "TODO");
    caseDetailsRows.put("POLICE_STATION_COURT_PRISON_ID", claim.getPoliceStationCourtPrisonId());
    caseDetailsRows.put("SCHEME_ID", claim.getSchemeId());
    caseDetailsRows.put("DSCC_NUMBER", claim.getDsccNumber());
    caseDetailsRows.put("MAAT_ID", claim.getMaatId());
    caseDetailsRows.put(
        "PRISON_LAW_PRIOR_APPROVAL_NUMBER", claim.getPrisonLawPriorApprovalNumber());
    caseDetailsRows.put("IS_DUTY_SOLICITOR", "TODO");
    caseDetailsRows.put("IS_YOUTH_COURT", "TODO");

    var caseDetailsForm = new AmendmentForm();
    caseDetailsForm.setInputs(caseDetailsRows);

    AmendmentForms forms = new AmendmentForms(client1Form, caseTypeForm, caseDetailsForm);

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
