package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveClaim;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.exceptions.FeeCodeNotFoundException;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.EnumAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FeeCodeAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.TextAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AvailableFeeCodesService;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@WebMvcTest(AmendCaseTypeController.class)
@Import({
  FeeCodeAmendmentFieldValidator.class,
  TextAmendmentFieldValidator.class,
  EnumAmendmentFieldValidator.class
})
class AmendCaseTypeControllerTest extends BaseControllerTest {

  private static final String INPUTS = "inputs[%s]";

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private ClaimDetails claim;

  private static final String FEE_CODE = "feecode";
  private static final String STAGE_REACHED = "INVC";
  private static final String MATTER_TYPE_CODE_1 = "mattertype1";
  private static final String MATTER_TYPE_CODE_2 = "mattertype2";

  @MockitoBean AvailableFeeCodesService availableFeeCodesService;

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
  }

  @Test
  void getAmendFeeCodeAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request = get(buildAmendFeeCodePath()).session(session).with(csrf());

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));
    mockMvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-fee-code"))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void getFeeCodeCatchMissingFeeCode() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request = get(buildAmendFeeCodePath()).session(session).with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is5xxServerError())
        .andExpect(
            result -> assertThat(result.getResolvedException() instanceof FeeCodeNotFoundException))
        .andExpect(
            result ->
                assertThat(result.getResolvedException().getMessage())
                    .isEqualTo("Fee code not found: feecode"));
  }

  @Test
  void postFeeCodeForCrimeLowerAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    claim.setStageReached(STAGE_REACHED);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var request = post(buildAmendFeeCodePath()).session(session).with(csrf());
    for (var entry : caseTypeRows.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendStageReachedPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void postFeeCodeForCivilAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.LEGAL_HELP))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var request = post(buildAmendFeeCodePath()).session(session).with(csrf());
    for (var entry : caseTypeRows.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendMatterTypeCodePath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void getAmendStageReachedAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setStageReached(STAGE_REACHED);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE, "STAGE_REACHED", STAGE_REACHED);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request = get(buildAmendStageReachedPath()).session(session).with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-stage-reached"))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void postAmendStageReachedAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setStageReached(STAGE_REACHED);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE, "STAGE_REACHED", STAGE_REACHED);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var request = post(buildAmendStageReachedPath()).session(session).with(csrf());
    for (var entry : caseTypeRows.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendCasePath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void postAmendStageReachedWithInvalidValueRedirectsBackAndPreservesInput() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setStageReached(STAGE_REACHED);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    session.setAttribute(claimId.toString(), claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE, "STAGE_REACHED", STAGE_REACHED);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var invalidStageReached = "NOT_A_VALID_STAGE";
    var request =
        post(buildAmendStageReachedPath())
            .param(INPUTS.formatted("FEE_CODE"), FEE_CODE)
            .param(INPUTS.formatted("STAGE_REACHED"), invalidStageReached)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendStageReachedPath()));

    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseTypeForm().getCurrent().getInputs().get("STAGE_REACHED"))
        .isEqualTo(invalidStageReached);
  }

  @Test
  void postAmendStageReachedWithInvalidValueRendersErrorSummaryAndInlineErrorOnRedirect()
      throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setStageReached(STAGE_REACHED);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    session.setAttribute(claimId.toString(), claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE, "STAGE_REACHED", STAGE_REACHED);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var invalidStageReached = "NOT_A_VALID_STAGE";
    var postRequest =
        post(buildAmendStageReachedPath())
            .param(INPUTS.formatted("FEE_CODE"), FEE_CODE)
            .param(INPUTS.formatted("STAGE_REACHED"), invalidStageReached)
            .session(session)
            .with(csrf());

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var postResult =
        mockMvc.perform(postRequest).andExpect(status().is3xxRedirection()).andReturn();

    var getRequest =
        get(buildAmendStageReachedPath())
            .session(session)
            .flashAttrs(postResult.getFlashMap())
            .with(csrf());

    mockMvc
        .perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("govuk-error-summary")))
        .andExpect(content().string(containsString("govuk-error-message")))
        .andExpect(content().string(containsString("Stage reached must be a valid option")));
  }

  @Test
  void postFeeCodeWithInvalidValueRedirectsBackAndPreservesInput() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var tooLong = "a".repeat(51);
    var request =
        post(buildAmendFeeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), tooLong)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendFeeCodePath()));

    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseTypeForm().getCurrent().getInputs().get("FEE_CODE"))
        .isEqualTo(tooLong);
  }

  @Test
  void postFeeCodeWithInvalidValueRendersErrorSummaryAndInlineErrorOnRedirect() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var tooLong = "a".repeat(51);
    var postRequest =
        post(buildAmendFeeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), tooLong)
            .session(session)
            .with(csrf());

    var postResult =
        mockMvc.perform(postRequest).andExpect(status().is3xxRedirection()).andReturn();

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var getRequest =
        get(buildAmendFeeCodePath())
            .session(session)
            .flashAttrs(postResult.getFlashMap())
            .with(csrf());

    mockMvc
        .perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("govuk-error-summary")))
        .andExpect(content().string(containsString("govuk-error-message")))
        .andExpect(content().string(containsString("Fee code must be 50 characters or less")));
  }

  @Test
  void postFeeCodeWithValueNotInFspListRedirectsBackAndPreservesInput() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of("OTHER_CODE", "OTHER_CODE"));

    var request =
        post(buildAmendFeeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), "NOT_A_VALID_CODE")
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendFeeCodePath()));

    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseTypeForm().getCurrent().getInputs().get("FEE_CODE"))
        .isEqualTo("NOT_A_VALID_CODE");
  }

  @Test
  void postFeeCodeWithBlankValueRedirectsBackAndPreservesInput() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var request =
        post(buildAmendFeeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), "")
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendFeeCodePath()));

    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseTypeForm().getCurrent().getInputs().get("FEE_CODE"))
        .isNullOrEmpty();
  }

  @Test
  void postFeeCodeSurfacesAvailableFeeCodesServiceFailureWhenFeeCodeNotFound() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.CRIME_LOWER))
        .thenThrow(new FeeCodeNotFoundException(AreaOfLaw.CRIME_LOWER));

    var request =
        post(buildAmendFeeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), FEE_CODE)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is5xxServerError())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(FeeCodeNotFoundException.class));
  }

  @Test
  void postMatterTypeCodeSurfacesAvailableFeeCodesServiceFailureLikeGetTimeFailure()
      throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.LEGAL_HELP))
        .thenThrow(new FeeCodeNotFoundException(AreaOfLaw.LEGAL_HELP));

    var request =
        post(buildAmendMatterTypeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), FEE_CODE)
            .param(INPUTS.formatted("MATTER_TYPE_CODE_1"), MATTER_TYPE_CODE_1)
            .param(INPUTS.formatted("MATTER_TYPE_CODE_2"), MATTER_TYPE_CODE_2)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is5xxServerError())
        .andExpect(
            result ->
                assertThat(result.getResolvedException())
                    .isInstanceOf(FeeCodeNotFoundException.class));
  }

  @Test
  void postMatterTypeCodeWithInvalidValueRedirectsBackAndPreservesInput() throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var tooLong = "a".repeat(51);
    var request =
        post(buildAmendMatterTypeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), FEE_CODE)
            .param(INPUTS.formatted("MATTER_TYPE_CODE_1"), tooLong)
            .param(INPUTS.formatted("MATTER_TYPE_CODE_2"), MATTER_TYPE_CODE_2)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendMatterTypeCodePath()));

    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseTypeForm().getCurrent().getInputs().get("MATTER_TYPE_CODE_1"))
        .isEqualTo(tooLong);
  }

  @Test
  void postMatterTypeCodeWithInvalidValueRendersErrorSummaryAndInlineErrorOnRedirect()
      throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var tooLong = "a".repeat(51);
    var postRequest =
        post(buildAmendMatterTypeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), FEE_CODE)
            .param(INPUTS.formatted("MATTER_TYPE_CODE_1"), tooLong)
            .param(INPUTS.formatted("MATTER_TYPE_CODE_2"), MATTER_TYPE_CODE_2)
            .session(session)
            .with(csrf());

    var postResult =
        mockMvc.perform(postRequest).andExpect(status().is3xxRedirection()).andReturn();

    var getRequest =
        get(buildAmendMatterTypeCodePath())
            .session(session)
            .flashAttrs(postResult.getFlashMap())
            .with(csrf());

    mockMvc
        .perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("govuk-error-summary")))
        .andExpect(content().string(containsString("govuk-error-message")))
        .andExpect(content().string(containsString("Matter type 1 must be 50 characters or less")));
  }

  @Test
  void postMatterTypeCodeWithInvalidHiddenFeeCodeRedirectsBackToMatterTypeAndPreservesInput()
      throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.LEGAL_HELP))
        .thenReturn(Map.of("OTHER_CODE", "OTHER_CODE"));

    var request =
        post(buildAmendMatterTypeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), "TAMPERED_CODE")
            .param(INPUTS.formatted("MATTER_TYPE_CODE_1"), MATTER_TYPE_CODE_1)
            .param(INPUTS.formatted("MATTER_TYPE_CODE_2"), MATTER_TYPE_CODE_2)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendMatterTypeCodePath()));

    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseTypeForm().getCurrent().getInputs().get("FEE_CODE"))
        .isEqualTo("TAMPERED_CODE");
  }

  @Test
  void postMatterTypeCodeWithBlankHiddenFeeCodeRedirectsBackToMatterTypeAndPreservesInput()
      throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.LEGAL_HELP))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var request =
        post(buildAmendMatterTypeCodePath())
            .param(INPUTS.formatted("FEE_CODE"), "")
            .param(INPUTS.formatted("MATTER_TYPE_CODE_1"), MATTER_TYPE_CODE_1)
            .param(INPUTS.formatted("MATTER_TYPE_CODE_2"), MATTER_TYPE_CODE_2)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendMatterTypeCodePath()));

    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseTypeForm().getCurrent().getInputs().get("FEE_CODE"))
        .isNullOrEmpty();
  }

  @Test
  void getAmendMatterStartsLegalHelpAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request = get(buildAmendMatterTypeCodePath()).session(session).with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-matter-type"))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void getAmendMatterStartsMediationAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    claim.setAreaOfLaw(AreaOfLaw.MEDIATION);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request = get(buildAmendMatterTypeCodePath()).session(session).with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-matter-type"))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void postMatterTypeCodeLegalHelpAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.LEGAL_HELP))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var request = post(buildAmendMatterTypeCodePath()).session(session).with(csrf());
    for (var entry : caseTypeRows.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendCasePath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void postMatterTypeCodeMediationAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    saveClaim(session, claimId, claim);

    var caseTypeRows =
        Map.of(
            "FEE_CODE",
            FEE_CODE,
            "MATTER_TYPE_CODE_1",
            MATTER_TYPE_CODE_1,
            "MATTER_TYPE_CODE_2",
            MATTER_TYPE_CODE_2);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(caseTypeForm))
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    when(availableFeeCodesService.getAvailableFeeCodes(AreaOfLaw.MEDIATION))
        .thenReturn(Map.of(FEE_CODE, FEE_CODE));

    var request = post(buildAmendMatterTypeCodePath()).session(session).with(csrf());
    for (var entry : caseTypeRows.entrySet()) {
      request.param(INPUTS.formatted(entry.getKey()), entry.getValue());
    }

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendCasePath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void getAmendFeeCodeReturnsNotFoundWhenClaimAssessed() throws Exception {
    markAssessed(claim);
    session.setAttribute(claimId.toString(), claim);

    mockMvc.perform(get(buildAmendFeeCodePath()).session(session)).andExpect(status().isNotFound());
  }

  @Test
  void postAmendFeeCodeReturnsNotFoundWithoutSavingWhenClaimAssessed() throws Exception {
    markAssessed(claim);
    session.setAttribute(claimId.toString(), claim);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    mockMvc
        .perform(
            post(buildAmendFeeCodePath())
                .param(INPUTS.formatted("FEE_CODE"), "NEWFEE")
                .session(session)
                .with(csrf()))
        .andExpect(status().isNotFound());

    var unchanged =
        requireNonNull((AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId)));
    assertThat(unchanged.getCaseTypeForm().getCurrent().getInputs().get("FEE_CODE")).isNull();
  }

  @Test
  void getAmendStageReachedStillServedForAnAssessedCrimeClaim() throws Exception {
    markAssessed(claim);
    session.setAttribute(claimId.toString(), claim);

    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(new HashMap<>(Map.of("STAGE_REACHED", STAGE_REACHED)));
    session.setAttribute(
        AMENDMENTS_KEY.formatted(claimId),
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(caseTypeForm)
            .caseDetails(new AmendmentForm())
            .build());

    mockMvc
        .perform(get(buildAmendStageReachedPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-stage-reached"));
  }

  @Test
  void postAmendStageReachedKeepsTheLockedFeeCodeWhenClaimAssessed() throws Exception {
    claim.setFeeCode(FEE_CODE);
    claim.setStageReached(STAGE_REACHED);
    markAssessed(claim);
    session.setAttribute(claimId.toString(), claim);

    var view = ClaimCaseViewFactory.create(claim);
    session.setAttribute(
        AMENDMENTS_KEY.formatted(claimId),
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(view.caseTypeRows()))
            .caseDetails(new AmendmentForm())
            .build());

    mockMvc
        .perform(
            post(buildAmendStageReachedPath())
                .param(INPUTS.formatted("STAGE_REACHED"), "INVB")
                .param(INPUTS.formatted("FEE_CODE"), "tampered")
                .session(session)
                .with(csrf()))
        .andExpect(status().is3xxRedirection());

    var saved =
        requireNonNull((AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId)));
    var current = saved.getCaseTypeForm().getCurrent();

    assertThat(current.getInputs().get("STAGE_REACHED")).isEqualTo("INVB");
    assertThat(current.getInputs().get("FEE_CODE")).isEqualTo(FEE_CODE);
  }

  @Test
  void postAmendMatterTypeKeepsTheLockedFeeCodeWhenClaimAssessed() throws Exception {
    var civilClaim = MockClaimsFunctions.createMockCivilClaim();
    civilClaim.setSubmissionId(submissionId);
    civilClaim.setClaimId(claimId);
    civilClaim.setFeeCode(FEE_CODE);
    civilClaim.setMatterType1(MATTER_TYPE_CODE_1);
    civilClaim.setMatterType2(MATTER_TYPE_CODE_2);
    markAssessed(civilClaim);
    session.setAttribute(claimId.toString(), civilClaim);

    var view = ClaimCaseViewFactory.create(civilClaim);
    session.setAttribute(
        AMENDMENTS_KEY.formatted(claimId),
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm(view.caseTypeRows()))
            .caseDetails(new AmendmentForm())
            .build());

    mockMvc
        .perform(
            post(buildAmendMatterTypeCodePath())
                .param(INPUTS.formatted("MATTER_TYPE_CODE_1"), "NEW1")
                .param(INPUTS.formatted("MATTER_TYPE_CODE_2"), "NEW2")
                .param(INPUTS.formatted("FEE_CODE"), "tampered")
                .session(session)
                .with(csrf()))
        .andExpect(status().is3xxRedirection());

    var saved =
        requireNonNull((AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId)));
    var current = saved.getCaseTypeForm().getCurrent();

    assertThat(current.getInputs().get("MATTER_TYPE_CODE_1")).isEqualTo("NEW1");
    assertThat(current.getInputs().get("FEE_CODE")).isEqualTo(FEE_CODE);
  }

  @Test
  void getAmendMatterTypeReturnsNotFoundWhenClaimNotValid() throws Exception {
    claim.setStatus(ClaimStatus.VOID);
    session.setAttribute(claimId.toString(), claim);
    session.setAttribute(
        AMENDMENTS_KEY.formatted(claimId),
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build());

    mockMvc
        .perform(get(buildAmendMatterTypeCodePath()).session(session))
        .andExpect(status().isNotFound());
  }

  private static void markAssessed(ClaimDetails claim) {
    claim.setHasAssessment(true);
    claim.setLastAssessment(
        MockClaimsFunctions.createAssessment(AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT));
  }

  private String buildAmendCasePath() {
    return "/submissions/%s/claims/%s/amendments/case".formatted(submissionId, claimId);
  }

  private String buildAmendFeeCodePath() {
    return "/submissions/%s/claims/%s/amendments/amend-fee-code".formatted(submissionId, claimId);
  }

  private String buildAmendMatterTypeCodePath() {
    return "/submissions/%s/claims/%s/amendments/amend-matter-type"
        .formatted(submissionId, claimId);
  }

  private String buildAmendStageReachedPath() {
    return "/submissions/%s/claims/%s/amendments/amend-stage-reached"
        .formatted(submissionId, claimId);
  }
}
