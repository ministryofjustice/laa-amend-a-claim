package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveClaim;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(AmendmentsConfirmationController.class)
class AmendmentsConfirmationControllerTest extends BaseControllerTest {

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();

    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    saveClaim(session, claimId, claim);
  }

  @Test
  void getsConfirmationPageWithDefaultSearchUrl() throws Exception {
    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/confirmation"))
        .andExpect(model().attribute("submissionId", submissionId))
        .andExpect(model().attribute("claimId", claimId))
        .andExpect(model().attribute("searchUrl", "/"));
  }

  @Test
  void getsConfirmationPageWithSearchUrlFromSession() throws Exception {
    session.setAttribute("searchUrl", "/?officeCode=0P322F&page=1");

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/confirmation"))
        .andExpect(model().attribute("searchUrl", "/?officeCode=0P322F&page=1"));
  }

  private String buildPath() {
    return "/submissions/%s/claims/%s/amendments/confirmation".formatted(submissionId, claimId);
  }
}
