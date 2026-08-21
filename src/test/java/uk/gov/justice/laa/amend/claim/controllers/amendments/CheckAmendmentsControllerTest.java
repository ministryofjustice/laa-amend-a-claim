package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static java.util.UUID.fromString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService.USER_ID;
import static uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService.createAuthToken;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveClaim;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.OriginalAndCurrent;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.CheckAmendmentsService;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcosts.ClaimCostsViewFactory;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;

@WebMvcTest(CheckAmendmentsController.class)
class CheckAmendmentsControllerTest extends BaseControllerTest {

  @MockitoBean private CheckAmendmentsService checkService;
  @MockitoBean private SystemReferenceService systemReferenceService;

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private ClaimDetails claim;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();
    var referenceList = new AmendmentRequestedByReferenceList();
    when(systemReferenceService.getAmendmentRequestedByReferenceList()).thenReturn(referenceList);
    when(systemReferenceService.getAmendmentRequestedByOptions(referenceList))
        .thenReturn(Map.of("requestedBy", "Provider"));
    when(systemReferenceService.getAmendmentRequestReason(eq("requestedBy"), any()))
        .thenReturn(Map.of("requestedReason", "Case reopened / rebilled"));
    setupClaim(MockClaimsFunctions.createMockCrimeClaim());
  }

  @Test
  void getsClientFormAndClientViewIntoModel() throws Exception {
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createCrimeForms());

    mockMvc
        .perform(get(buildCheckPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/check-your-answers"))
        .andExpect(
            model()
                .attributeExists(
                    "forms",
                    "client1Form",
                    "clientView",
                    "caseTypeForm",
                    "caseDetailsForm",
                    "costsForm",
                    "costFields",
                    "claim",
                    "areaOfLaw",
                    "requestedByLabel",
                    "requestedReasonLabel"));
  }

  @Test
  void getsClient2FormIntoModelForMediationClaims() throws Exception {
    setupClaim(MockClaimsFunctions.createMockMediationClaim());

    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createMediationForms());

    mockMvc
        .perform(get(buildCheckPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/check-your-answers"))
        .andExpect(
            model()
                .attributeExists(
                    "forms",
                    "client1Form",
                    "client2Form",
                    "clientView",
                    "caseTypeForm",
                    "caseDetailsForm",
                    "costsForm",
                    "costFields",
                    "claim",
                    "areaOfLaw",
                    "requestedByLabel",
                    "requestedReasonLabel"));
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
  void getCheckPageThrows404WhenMissingRequiredFields() throws Exception {
    var missingRequiredFields = createCrimeForms();
    missingRequiredFields.setRequestedByForm(new RequestedByForm());
    missingRequiredFields.setRequestedReasonForm(new RequestedReasonForm());

    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), missingRequiredFields);

    mockMvc
        .perform(get(buildCheckPath()).session(session))
        .andExpect(status().isNotFound())
        .andExpect(
            result ->
                assertThat(result.getResolvedException() instanceof ResponseStatusException)
                    .isTrue());
  }

  @Test
  void submitAndRedirect() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    saveClaim(session, claimId, claim);

    var originalClientForm = new AmendmentForm();
    originalClientForm.setInputs(Map.of("INITIAL", "OLD"));
    var currentClientForm = new AmendmentForm();
    currentClientForm.setInputs(Map.of("INITIAL", "NEW"));

    var emptyForm = new AmendmentForm();
    emptyForm.setInputs(Map.of());

    var forms =
        new AmendmentForms(
            new OriginalAndCurrent(originalClientForm, currentClientForm),
            new OriginalAndCurrent(emptyForm, emptyForm),
            new OriginalAndCurrent(emptyForm, emptyForm),
            new OriginalAndCurrent(emptyForm, emptyForm),
            new OriginalAndCurrent(emptyForm, emptyForm),
            createRequestedByForm(),
            createRequestedReasonForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    mockMvc
        .perform(
            post(buildCheckPath())
                .session(session)
                .with(csrf())
                .with(authentication(createAuthToken(Set.of()))))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildConfirmationPath()));

    verify(checkService).submitAmendments(submissionId, claimId, fromString(USER_ID), claim, forms);
  }

  private void setupClaim(ClaimDetails claim) {
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setClientForename("forename");
    claim.setClientSurname("surname");
    MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
    saveClaim(session, claimId, claim);
  }

  private AmendmentForms createCrimeForms() {
    var clientView = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    var costsView = ClaimCostsViewFactory.create(claim);
    var client1Form = new AmendmentForm();
    client1Form.setInputs(Map.of("SURNAME", "changedSurname"));

    var amendmentForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm(clientView.client1Rows()))
            .caseType(new AmendmentForm(caseView.caseTypeRows()))
            .caseDetails(new AmendmentForm(caseView.caseDetailsRows()))
            .costs(new AmendmentForm(costsView.costRows()))
            .requestedBy(createRequestedByForm())
            .requestedReason(createRequestedReasonForm())
            .build();
    amendmentForms.getClient1Form().setCurrent(client1Form);
    return amendmentForms;
  }

  private AmendmentForms createEmptyForms() {
    var clientView = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    var costsView = ClaimCostsViewFactory.create(claim);

    return AmendmentForms.builder()
        .client1(new AmendmentForm(clientView.client1Rows()))
        .caseType(new AmendmentForm(caseView.caseTypeRows()))
        .caseDetails(new AmendmentForm(caseView.caseDetailsRows()))
        .costs(new AmendmentForm(costsView.costRows()))
        .requestedBy(createRequestedByForm())
        .requestedReason(createRequestedReasonForm())
        .build();
  }

  private AmendmentForms createMediationForms() {
    var clientView = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    var costsView = ClaimCostsViewFactory.create(claim);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm(clientView.client1Rows()))
            .client2(new AmendmentForm(clientView.client2Rows()))
            .caseType(new AmendmentForm(caseView.caseTypeRows()))
            .caseDetails(new AmendmentForm(caseView.caseDetailsRows()))
            .costs(new AmendmentForm(costsView.costRows()))
            .requestedBy(createRequestedByForm())
            .requestedReason(createRequestedReasonForm())
            .build();
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    forms
        .getClient2Form()
        .getCurrent()
        .getInputs()
        .put("CLIENT_2_SURNAME", "changedClient2Surname");
    return forms;
  }

  private static @NonNull RequestedByForm createRequestedByForm() {
    var requestedByForm = new RequestedByForm();
    requestedByForm.setRequestedBy("requestedBy");
    return requestedByForm;
  }

  private static @NonNull RequestedReasonForm createRequestedReasonForm() {
    var requestedReasonForm = new RequestedReasonForm();
    requestedReasonForm.setRequestedReason("requestedReason");
    return requestedReasonForm;
  }

  private String buildCheckPath() {
    return "/submissions/%s/claims/%s/amendments/check".formatted(submissionId, claimId);
  }

  private String buildConfirmationPath() {
    return "/submissions/%s/claims/%s/amendments/confirmation".formatted(submissionId, claimId);
  }
}
