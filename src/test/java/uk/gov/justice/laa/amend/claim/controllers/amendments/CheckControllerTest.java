package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

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
    setupClaim(MockClaimsFunctions.createMockCrimeClaim());
  }

  @Test
  void getsClientFormAndClientViewIntoModel() throws Exception {
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createCrimeForms());

    mockMvc
        .perform(get(buildCheckPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/check"))
        .andExpect(model().attributeExists("forms", "client1Form", "clientView"));
  }

  @Test
  void getsClient2FormIntoModelForMediationClaims() throws Exception {
    setupClaim(MockClaimsFunctions.createMockMediationClaim());

    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createMediationForms());

    mockMvc
        .perform(get(buildCheckPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/check"))
        .andExpect(model().attributeExists("forms", "client1Form", "client2Form", "clientView"));
  }

  @Test
  void getCheckPageThrows404WhenNoAmendments() throws Exception {
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createEmptyForms());

    mockMvc
        .perform(get(buildCheckPath()).session(session))
        .andExpect(status().isNotFound())
        .andExpect(
            result ->
                assertThat(result.getResolvedException() instanceof ResponseStatusException)
                    .isTrue());
  }

  @Test
  void submitCheckRedirectsToSuccessPage() throws Exception {
    // TODO change in BC-620 when the submit button functionality is implemented
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createCrimeForms());

    mockMvc
        .perform(post(buildCheckPath()).session(session).with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildSuccessPath()));
  }

  private void setupClaim(ClaimDetails claim) {
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setClientForename("forename");
    claim.setClientSurname("surname");
    MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
    session.setAttribute(claimId.toString(), claim);
  }

  private AmendmentForms createCrimeForms() {
    var client1Form = new AmendmentForm();
    client1Form.setInputs(Map.of("SURNAME", "changedSurname"));

    var amendmentForms =
        new AmendmentForms(new AmendmentForm(), new AmendmentForm(), new AmendmentForm());
    amendmentForms.getClient1Form().setCurrent(client1Form);
    return amendmentForms;
  }

  private AmendmentForms createEmptyForms() {
    return new AmendmentForms(new AmendmentForm(), new AmendmentForm(), new AmendmentForm());
  }

  private AmendmentForms createMediationForms() {
    var clientView = ClaimClientViewFactory.create(claim);
    var forms =
        new AmendmentForms(
            new AmendmentForm(clientView.client1Rows()),
            new AmendmentForm(clientView.client2Rows()),
            new AmendmentForm(),
            new AmendmentForm());
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    forms
        .getClient2Form()
        .getCurrent()
        .getInputs()
        .put("CLIENT_2_SURNAME", "changedClient2Surname");
    return forms;
  }

  private String buildCheckPath() {
    return "/submissions/%s/claims/%s/amendments/check".formatted(submissionId, claimId);
  }

  private String buildSuccessPath() {
    return "/submissions/%s/claims/%s/amendments/success".formatted(submissionId, claimId);
  }
}
