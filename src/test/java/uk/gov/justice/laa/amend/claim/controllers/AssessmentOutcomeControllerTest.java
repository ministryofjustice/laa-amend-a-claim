package uk.gov.justice.laa.amend.claim.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.allRolesApartFrom;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

@WebMvcTest(AssessmentOutcomeController.class)
public class AssessmentOutcomeControllerTest extends BaseControllerTest {

  @MockitoBean private AssessmentService assessmentService;

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();
    session.setAttribute(claimId.toString(), MockClaimsFunctions.createMockCivilClaim());
  }

  @Test
  public void testGetAssessmentOutcome_ReturnsView() throws Exception {
    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("form"))
        .andExpect(view().name("assessment-outcome"));
  }

  @Test
  public void testOnSubmitReturnsBadRequestWithViewForInvalidForm() throws Exception {
    mockMvc
        .perform(
            post(buildPath())
                .session(session)
                .with(csrf())
                .param("assessmentOutcome", "")
                .param("contingencyAssessment", ""))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("assessment-outcome"));
  }

  @Test
  public void testOnSubmitRedirects() throws Exception {
    String redirectUrl = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

    mockMvc
        .perform(
            post(buildPath())
                .session(session)
                .with(csrf())
                .param("assessmentOutcome", "paid-in-full")
                .param("contingencyAssessment", "true"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  void testGetRequiresRole() throws Exception {
    dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_CASEWORKER));
    mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isForbidden());
  }

  @Test
  void testPostRequiresRole() throws Exception {
    dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_CASEWORKER));
    mockMvc.perform(post(buildPath()).session(session)).andExpect(status().isForbidden());
  }

  private String buildPath() {
    return String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);
  }
}
