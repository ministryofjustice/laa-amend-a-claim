package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import uk.gov.justice.laa.amend.claim.exceptions.FeeCodeNotFoundException;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AvailableFeeCodesService;

@WebMvcTest(CaseTypeController.class)
class CaseTypeControllerTest extends BaseControllerTest {

  private static final String INPUTS = "inputs[%s]";

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private ClaimDetails claim;

  private static final String FEE_CODE = "feecode";
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
    session.setAttribute(claimId.toString(), claim);
  }

  @Test
  void getAmendFeeCodeAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    session.setAttribute(claimId.toString(), claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        new AmendmentForms(
            new AmendmentForm(), new AmendmentForm(caseTypeForm), new AmendmentForm());
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
    session.setAttribute(claimId.toString(), claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        new AmendmentForms(
            new AmendmentForm(), new AmendmentForm(caseTypeForm), new AmendmentForm());
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
  void postFeeCodeAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE_1);
    session.setAttribute(claimId.toString(), claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        new AmendmentForms(
            new AmendmentForm(), new AmendmentForm(caseTypeForm), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

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
  void getAmendMatterStartsLegalHelpAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    session.setAttribute(claimId.toString(), claim);

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
        new AmendmentForms(
            new AmendmentForm(), new AmendmentForm(caseTypeForm), new AmendmentForm());
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
    claim.setMatterType(MATTER_TYPE_CODE_1);
    claim.setAreaOfLaw(AreaOfLaw.MEDIATION);
    session.setAttribute(claimId.toString(), claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE, "MATTER_TYPE_CODE", MATTER_TYPE_CODE_1);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        new AmendmentForms(
            new AmendmentForm(), new AmendmentForm(caseTypeForm), new AmendmentForm());
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
    session.setAttribute(claimId.toString(), claim);

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
        new AmendmentForms(
            new AmendmentForm(), new AmendmentForm(caseTypeForm), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

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
    claim.setMatterType(MATTER_TYPE_CODE_1);
    session.setAttribute(claimId.toString(), claim);

    var caseTypeRows = Map.of("FEE_CODE", FEE_CODE, "MATTER_TYPE_CODE", MATTER_TYPE_CODE_1);
    var caseTypeForm = new AmendmentForm();
    caseTypeForm.setInputs(caseTypeRows);

    var updatedForms =
        new AmendmentForms(
            new AmendmentForm(), new AmendmentForm(caseTypeForm), new AmendmentForm());
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

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
}
