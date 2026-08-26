package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveClaim;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VOID;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.models.AmendmentConfirmation;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.ClaimHistoryService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;

@WebMvcTest(AmendmentsConfirmationController.class)
class AmendmentsConfirmationControllerTest extends BaseControllerTest {

  @MockitoBean private ClaimService claimService;

  @MockitoBean private ClaimHistoryService claimHistoryService;

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private ClaimDetails claim;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();

    claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setAmended(true);
    saveClaim(session, claimId, claim);

    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);
    when(claimHistoryService.getAmendmentConfirmation(claim))
        .thenReturn(new AmendmentConfirmation(false, Set.of()));
  }

  @Test
  void getsConfirmationPageWithDefaultSearchUrl() throws Exception {
    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/confirmation"))
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
        .andExpect(view().name("pages/amendments/confirmation"))
        .andExpect(model().attribute("searchUrl", "/?officeCode=0P322F&page=1"));
  }

  @Test
  void getsConfirmationPageWithUpdatedClaimTotalWhenCalculatedCostsChanged() throws Exception {
    when(claimHistoryService.getAmendmentConfirmation(any()))
        .thenReturn(new AmendmentConfirmation(true, new HashSet<>()));

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/confirmation"))
        .andExpect(model().attribute("updatedClaimTotal", BigDecimal.valueOf(200)));
  }

  @Test
  void returnsNotFoundForNonAmendedClaim() throws Exception {
    claim.setAmended(false);
    mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
  }

  @Test
  void returnsNotFoundForNonValidClaim() throws Exception {
    claim.setStatus(VOID);
    mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
  }

  private String buildPath() {
    return "/submissions/%s/claims/%s/amendments/confirmation".formatted(submissionId, claimId);
  }
}
