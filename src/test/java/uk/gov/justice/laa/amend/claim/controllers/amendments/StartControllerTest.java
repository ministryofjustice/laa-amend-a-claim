package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(controllers = StartController.class)
class StartControllerTest extends BaseControllerTest {

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private ClaimDetails claim;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();
    claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
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

    AmendmentForms forms = new AmendmentForms(client1Form);

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
