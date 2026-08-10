package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;

@WebMvcTest(controllers = AmendmentRequestByController.class)
class AmendmentRequestByControllerTest extends BaseControllerTest {

  private static final String REQUESTED_BY = "COURT";

  @MockitoBean private SystemReferenceService systemReferenceService;

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();
  }

  @Test
  void getRequestedByAsExpected() throws Exception {
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createForms());

    mockMvc
        .perform(get(buildRequestedByPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-request-by"))
        .andExpect(model().attributeExists("claimId", "submissionId", "requestedByForm"))
        .andExpect(model().attribute("amendmentRequestByOptions", Map.of()))
        .andExpect(model().attribute("claimId", claimId))
        .andExpect(model().attribute("submissionId", submissionId));
  }

  @Test
  void postRequestedByRedirectsToRequestedReason() throws Exception {
    var forms = createForms();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    mockMvc
        .perform(
            post(buildRequestedByPath())
                .session(session)
                .with(csrf())
                .param("requestedBy", REQUESTED_BY))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildRequestedReasonPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), forms));
  }

  @Test
  void postRequestedByWithoutValueShowsValidationError() throws Exception {
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createForms());

    mockMvc
        .perform(post(buildRequestedByPath()).session(session).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-request-by"))
        .andExpect(model().attributeExists("requestedByForm"));
  }

  private AmendmentForms createForms() {
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm())
        .costs(new AmendmentForm())
        .requestedBy(new RequestedByForm())
        .build();
  }

  private String buildRequestedByPath() {
    return "/submissions/%s/claims/%s/amendments/requested-by".formatted(submissionId, claimId);
  }

  private String buildRequestedReasonPath() {
    return "/submissions/%s/claims/%s/amendments/requested-reason".formatted(
        submissionId, claimId);
  }
}
