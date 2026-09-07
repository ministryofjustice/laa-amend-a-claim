package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VOID;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getAmendmentErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveAmendmentErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveClaim;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.payments.amend.controllers.BaseControllerTest;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;

@WebMvcTest(AmendmentsCannotBeSubmittedController.class)
class AmendmentsCannotBeSubmittedControllerTest extends BaseControllerTest {

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
    saveClaim(session, claimId, claim);
  }

  @Test
  void getsCannotBeSubmittedPageWithErrorsAndClearsThemFromSession() throws Exception {
    saveAmendmentErrors(session, claimId, List.of("This claim has been modified by another user."));

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/cannot-be-submitted"))
        .andExpect(model().attribute("submissionId", submissionId))
        .andExpect(model().attribute("claimId", claimId))
        .andExpect(
            model().attribute("errors", List.of("This claim has been modified by another user.")));

    assertThatAmendmentErrorsWereCleared();
  }

  @Test
  void returnsNotFoundWhenNoAmendmentErrorsInSession() throws Exception {
    mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
  }

  @Test
  void returnsNotFoundForNonValidClaim() throws Exception {
    claim.setStatus(VOID);
    saveAmendmentErrors(session, claimId, List.of("This claim has been modified by another user."));

    mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
  }

  private void assertThatAmendmentErrorsWereCleared() {
    assertThat(getAmendmentErrors(session, claimId)).isEmpty();
  }

  private String buildPath() {
    return "/submissions/%s/claims/%s/amendments/cannot-submit".formatted(submissionId, claimId);
  }
}
