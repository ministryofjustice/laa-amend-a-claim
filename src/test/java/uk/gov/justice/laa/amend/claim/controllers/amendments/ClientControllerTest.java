package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@WebMvcTest(controllers = ClientController.class)
class ClientControllerTest extends BaseControllerTest {

  private static final String INPUTS = "inputs[%s]";

  private static final String FORENAME = "forename";
  private static final String SURNAME = "surname";
  private static final String GENDER = "gender";
  private static final String ETHNICITY = "ethnicity";
  private static final String DISABILITY = "disability";

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

    var existingForms =
        new AmendmentForms(new AmendmentForm(), new AmendmentForm(), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var client1Rows =
        Map.of(
            "INITIAL", FORENAME,
            "SURNAME", SURNAME,
            "GENDER", GENDER,
            "ETHNICITY", ETHNICITY,
            "DISABILITY", DISABILITY);
    var client1Form = new AmendmentForm();
    client1Form.setInputs(client1Rows);

    var updatedForms =
        new AmendmentForms(new AmendmentForm(), new AmendmentForm(), new AmendmentForm());
    updatedForms.getClient1Form().setCurrent(client1Form);

    var request = post(buildAmendClient1Path()).session(session).with(csrf());
    for (var entry : client1Rows.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildViewClientPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  private String buildViewClientPath() {
    return "/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }

  private String buildAmendClient1Path() {
    return "/submissions/%s/claims/%s/amendments/amend-client".formatted(submissionId, claimId);
  }
}
