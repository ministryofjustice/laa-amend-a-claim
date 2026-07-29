package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

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

@WebMvcTest(controllers = CheckController.class)
class CheckControllerTest extends BaseControllerTest {

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
    claim.setClientForename("forename");
    claim.setClientSurname("surname");
    MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
    session.setAttribute(claimId.toString(), claim);
  }

  @Test
  void getsClientFormAndClientViewIntoModel() throws Exception {
    var amendmentForms =
        new AmendmentForms(new AmendmentForm(), new AmendmentForm(), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), amendmentForms);

    mockMvc
        .perform(get(buildCheckPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/check"))
        .andExpect(model().attributeExists("forms", "client1Form", "clientView"));
  }

  @Test
  void submitCheckRedirectsToSuccessPage() throws Exception {
    // TODO change in BC-620 when the submit button functionality is implemented
    var amendmentForms =
        new AmendmentForms(new AmendmentForm(), new AmendmentForm(), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), amendmentForms);

    mockMvc
        .perform(post(buildCheckPath()).session(session).with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildSuccessPath()));
  }

  private String buildCheckPath() {
    return "/submissions/%s/claims/%s/amendments/check".formatted(submissionId, claimId);
  }

  private String buildSuccessPath() {
    return "/submissions/%s/claims/%s/amendments/success".formatted(submissionId, claimId);
  }
}
