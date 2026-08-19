package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.amend.claim.forms.validators.RequestedReasonFormValidator;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;

@WebMvcTest(controllers = AmendmentRequestedReasonController.class)
class AmendmentRequestedReasonControllerTest extends BaseControllerTest {

  private static final String REQUESTED_BY = "COURT";
  private static final String REQUESTED_REASON = "REASON_1";
  private static final String REQUESTED_REASON_FAIL = "REASON_FAIL";

  @MockitoBean private SystemReferenceService systemReferenceService;
  @MockitoBean private RequestedReasonFormValidator requestedReasonFormValidator;

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
  void getAmendReasonAsExpected() throws Exception {

    when(systemReferenceService.getAmendmentRequestReason(any()))
        .thenReturn(Map.of(REQUESTED_REASON, REQUESTED_REASON));
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createForms());

    mockMvc
        .perform(get(buildRequestedReasonPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/amend-request-reason"))
        .andExpect(model().attributeExists("claimId", "submissionId", "requestedReasonForm"))
        .andExpect(
            model()
                .attribute(
                    "amendmentReasonOptions", java.util.Map.of(REQUESTED_REASON, REQUESTED_REASON)))
        .andExpect(model().attribute("claimId", claimId))
        .andExpect(model().attribute("submissionId", submissionId));
  }

  @Test
  void postRequestedReasonRedirectsToClient() throws Exception {
    var forms = createForms();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    mockMvc
        .perform(
            post(buildRequestedReasonPath())
                .session(session)
                .with(csrf())
                .param("requestedReason", REQUESTED_REASON))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildClientPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), forms));
  }

  @Test
  void postRequestedByWithoutValueShowsValidationError() throws Exception {
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createForms());
    doAnswer(
            invocation -> {
              Errors errors = invocation.getArgument(1);
              errors.rejectValue("requestedReason", "amendments.requestReason.invalid");
              return null;
            })
        .when(requestedReasonFormValidator)
        .validate(any(), any(), any());

    var postResult =
        mockMvc
            .perform(
                post(buildRequestedReasonPath())
                    .session(session)
                    .with(csrf())
                    .param("requestedReason", REQUESTED_REASON_FAIL))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(buildRequestedReasonPath()))
            .andExpect(flash().attributeExists("requestedReasonFormErrors"))
            .andReturn();

    mockMvc
        .perform(
            get(buildRequestedReasonPath()).session(session).flashAttrs(postResult.getFlashMap()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("govuk-error-summary")))
        .andExpect(content().string(containsString("Select the reason for the amendment")));
  }

  @Test
  void postRequestedByInvalidValueShowsValidationError() throws Exception {
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createForms());
    doAnswer(
            invocation -> {
              Errors errors = invocation.getArgument(1);
              errors.rejectValue("requestedReason", "amendments.requestReason.required");
              return null;
            })
        .when(requestedReasonFormValidator)
        .validate(any(), any(), any());

    var postResult =
        mockMvc
            .perform(post(buildRequestedReasonPath()).session(session).with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(buildRequestedReasonPath()))
            .andExpect(flash().attributeExists("requestedReasonFormErrors"))
            .andReturn();

    mockMvc
        .perform(
            get(buildRequestedReasonPath()).session(session).flashAttrs(postResult.getFlashMap()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("govuk-error-summary")))
        .andExpect(content().string(containsString("Select the reason for the amendment")));
  }

  private AmendmentForms createForms() {
    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .costs(new AmendmentForm())
            .requestedBy(new RequestedByForm())
            .requestedReason(new RequestedReasonForm())
            .build();
    forms.getRequestedByForm().setRequestedBy(REQUESTED_BY);
    return forms;
  }

  private String buildRequestedReasonPath() {
    return "/submissions/%s/claims/%s/amendments/requested-reason".formatted(submissionId, claimId);
  }

  private String buildClientPath() {
    return "/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }
}
