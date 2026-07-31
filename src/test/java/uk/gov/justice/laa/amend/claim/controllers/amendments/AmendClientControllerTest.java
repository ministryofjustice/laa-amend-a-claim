package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(controllers = AmendClientController.class)
class AmendClientControllerTest extends BaseControllerTest {

  private static final String INPUTS = "inputs[%s]";

  private static final String FORENAME = "forename";
  private static final String SURNAME = "surname";
  private static final String GENDER = "M";
  private static final String ETHNICITY = "00";
  private static final String DISABILITY = "NCD";

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
  void savesFormsIntoSessionThenRedirectsClient1() throws Exception {
    claim.setClientForename("forename");
    claim.setClientSurname("surname");
    claim.setClientGender("gender");
    claim.setClientEthnicity("ethnicity");
    claim.setClientDisability("disability");

    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
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
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
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

  @Test
  void savesFormsIntoSessionThenRedirectsClient2() throws Exception {
    useMediationClaim();
    claim.setClientForename("forename");
    claim.setClientSurname("surname");
    claim.setClientGender("gender");
    claim.setClientEthnicity("ethnicity");
    claim.setClientDisability("disability");

    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var client2Rows =
        Map.of(
            "CLIENT_2_FORENAME", FORENAME,
            "CLIENT_2_SURNAME", SURNAME,
            "CLIENT_2_GENDER", GENDER,
            "CLIENT_2_ETHNICITY", ETHNICITY,
            "CLIENT_2_DISABILITY", DISABILITY);
    var client2Form = new AmendmentForm();
    client2Form.setInputs(client2Rows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    updatedForms.getClient2Form().setCurrent(client2Form);

    var request = post(buildAmendClient2Path()).session(session).with(csrf());
    for (var entry : client2Rows.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildViewClientPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void persistsDateSubInputsIntoSessionThenRedirects() throws Exception {
    useMediationClaim();

    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var dateInputs =
        Map.of(
            "DATE_OF_BIRTH-day", "14",
            "DATE_OF_BIRTH-month", "5",
            "DATE_OF_BIRTH-year", "2002");
    var client1Form = new AmendmentForm();
    client1Form.setInputs(dateInputs);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    updatedForms.getClient1Form().setCurrent(client1Form);

    var request = post(buildAmendClient1Path()).session(session).with(csrf());
    for (var entry : dateInputs.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildViewClientPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void getAmendClientAsExpected() throws Exception {
    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    mockMvc
        .perform(get(buildAmendClient1Path()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-client-1"))
        .andExpect(model().attributeExists("clientView"))
        .andExpect(model().attribute("client1Form", existingForms.getClient1Form().getCurrent()))
        .andExpect(model().attribute("forms", existingForms));
  }

  @Test
  void getAmendClientTwoAsExpected() throws Exception {
    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    mockMvc
        .perform(get(buildAmendClient2Path()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-client-2"))
        .andExpect(model().attributeExists("clientView"))
        .andExpect(model().attribute("client2Form", existingForms.getClient2Form().getCurrent()))
        .andExpect(model().attribute("forms", existingForms));
  }

  @Test
  void savesClient2FormsIntoSessionThenRedirects() throws Exception {
    useMediationClaim();

    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var client2Rows =
        Map.of(
            "CLIENT_2_FORENAME", FORENAME,
            "CLIENT_2_SURNAME", SURNAME,
            "CLIENT_2_GENDER", GENDER,
            "CLIENT_2_ETHNICITY", ETHNICITY,
            "CLIENT_2_DISABILITY", DISABILITY);
    var client2Form = new AmendmentForm();
    client2Form.setInputs(client2Rows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    updatedForms.getClient2Form().setCurrent(client2Form);

    var request = post(buildAmendClient2Path()).session(session).with(csrf());
    for (var entry : client2Rows.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildViewClientPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void persistsClient2DateSubInputsIntoSessionThenRedirects() throws Exception {
    useMediationClaim();

    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var dateInputs =
        Map.of(
            "CLIENT_2_DATE_OF_BIRTH-day", "14",
            "CLIENT_2_DATE_OF_BIRTH-month", "5",
            "CLIENT_2_DATE_OF_BIRTH-year", "2002");
    var client2Form = new AmendmentForm();
    client2Form.setInputs(dateInputs);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    updatedForms.getClient2Form().setCurrent(client2Form);

    var request = post(buildAmendClient2Path()).session(session).with(csrf());
    for (var entry : dateInputs.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildViewClientPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void postClient1WithInvalidValueRedirectsBackAndPreservesInput() throws Exception {
    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var tooLong = "a".repeat(256);
    var request =
        post(buildAmendClient1Path())
            .param(INPUTS.formatted("SURNAME"), tooLong)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendClient1Path()));

    AmendmentForms updatedForms =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForms.getClient1Form().getCurrent().getInputs().get("SURNAME"))
        .isEqualTo(tooLong);
  }

  @Test
  void postClient2WithInvalidValueRedirectsBackAndPreservesInput() throws Exception {
    useMediationClaim();

    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var tooLong = "a".repeat(256);
    var request =
        post(buildAmendClient2Path())
            .param(INPUTS.formatted("CLIENT_2_SURNAME"), tooLong)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendClient2Path()));

    AmendmentForms updatedForms =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForms.getClient2Form().getCurrent().getInputs().get("CLIENT_2_SURNAME"))
        .isEqualTo(tooLong);
  }

  @Test
  void postClient1WithInvalidValueRendersErrorSummaryAndInlineErrorOnRedirect() throws Exception {
    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var tooLong = "a".repeat(256);
    var postRequest =
        post(buildAmendClient1Path())
            .param(INPUTS.formatted("SURNAME"), tooLong)
            .session(session)
            .with(csrf());

    var postResult =
        mockMvc.perform(postRequest).andExpect(status().is3xxRedirection()).andReturn();

    var getRequest =
        get(buildAmendClient1Path())
            .session(session)
            .flashAttrs(postResult.getFlashMap())
            .with(csrf());

    mockMvc
        .perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("govuk-error-summary")))
        .andExpect(content().string(containsString("govuk-error-message")))
        .andExpect(content().string(containsString("Value exceeds maximum length")));
  }

  @Test
  void postClient2WithInvalidValueRendersErrorSummaryAndInlineErrorOnRedirect() throws Exception {
    useMediationClaim();

    var existingForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .client2(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), existingForms);

    var tooLong = "a".repeat(256);
    var postRequest =
        post(buildAmendClient2Path())
            .param(INPUTS.formatted("CLIENT_2_SURNAME"), tooLong)
            .session(session)
            .with(csrf());

    var postResult =
        mockMvc.perform(postRequest).andExpect(status().is3xxRedirection()).andReturn();

    var getRequest =
        get(buildAmendClient2Path())
            .session(session)
            .flashAttrs(postResult.getFlashMap())
            .with(csrf());

    mockMvc
        .perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("govuk-error-summary")))
        .andExpect(content().string(containsString("govuk-error-message")))
        .andExpect(content().string(containsString("Value exceeds maximum length")));
  }

  private void useMediationClaim() {
    claim = MockClaimsFunctions.createMockMediationClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
    session.setAttribute(claimId.toString(), claim);
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
