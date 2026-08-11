package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveClaim;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.AmendmentsHeaderView;

@WebMvcTest(controllers = {AmendClientTabController.class})
class AmendClientTabControllerTest extends BaseControllerTest {

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
    saveClaim(session, claimId, claim);
    when(amendmentsHeaderViewFactory.create(any()))
        .thenReturn(new AmendmentsHeaderView(false, null));
  }

  @Test
  void viewClientDisplaysClient1FormOnlyForNonMediationClaim() throws Exception {
    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    mockMvc
        .perform(get(buildViewClientPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/view-client"))
        .andExpect(model().attributeExists("clientView"))
        .andExpect(model().attribute("client1Form", existingForms.getClient1Form().getCurrent()))
        .andExpect(model().attributeDoesNotExist("client2Form"))
        .andExpect(model().attribute("forms", existingForms));
  }

  @Test
  void viewClientDisplaysClient1AndClient2FormsForMediationClaim() throws Exception {
    claim = MockClaimsFunctions.createMockMediationClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setAreaOfLaw(AreaOfLaw.MEDIATION);
    MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
    saveClaim(session, claimId, claim);

    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    mockMvc
        .perform(get(buildViewClientPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/view-client"))
        .andExpect(model().attributeExists("clientView"))
        .andExpect(model().attribute("client1Form", existingForms.getClient1Form().getCurrent()))
        .andExpect(model().attribute("client2Form", existingForms.getClient2Form().getCurrent()))
        .andExpect(model().attribute("forms", existingForms));
  }

  private String buildViewClientPath() {
    return "/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }

  private String buildAmendClient1Path() {
    return "/submissions/%s/claims/%s/amendments/amend-client".formatted(submissionId, claimId);
  }

  private String buildAmendClient2Path() {
    return "/submissions/%s/claims/%s/amendments/amend-client-two".formatted(submissionId, claimId);
  }
}
