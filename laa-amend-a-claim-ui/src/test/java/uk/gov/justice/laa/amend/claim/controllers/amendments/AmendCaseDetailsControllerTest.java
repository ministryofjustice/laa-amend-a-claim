package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
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

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.DateAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.TextAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.amend.claim.resources.MockAmendmentFormsFunctions;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(AmendCaseDetailsController.class)
@Import({TextAmendmentFieldValidator.class, DateAmendmentFieldValidator.class})
class AmendCaseDetailsControllerTest extends BaseControllerTest {

  private static final String INPUTS = "inputs[%s]";

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private ClaimDetails claim;

  private static final String FEE_CODE = "feecode";
  private static final String MATTER_TYPE_CODE = "mattertype";
  private static final String UFN = "010424/820";
  private static final String SCHEME_ID = "schemeid";
  private static final String SCHEDULE_REFERENCE = "schedref";
  private static final LocalDate CASE_START_DATE = LocalDate.of(2024, 3, 5);
  private static final LocalDate CASE_CONCLUDED_DATE = LocalDate.of(2024, 4, 6);

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
  void getCaseDetailsAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    saveClaim(session, claimId, claim);

    var caseDetailsRows = Map.of("FEE_CODE", FEE_CODE);
    var caseDetailsForm = new AmendmentForm();
    caseDetailsForm.setInputs(caseDetailsRows);

    var updatedForms = MockAmendmentFormsFunctions.justCaseDetailsFilled(claim);
    updatedForms.getCaseDetailsForm().setCurrent(caseDetailsForm);

    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request = get(buildAmendCaseDetailsPath()).session(session).with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/amend-case-details"))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void postCaseDetailsAsExpected() throws Exception {
    var updatedForms = MockAmendmentFormsFunctions.justCaseDetailsFilled(claim);

    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request =
        post(buildAmendCaseDetailsPath())
            .param(INPUTS.formatted("MAAT_ID"), "updated")
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendCaseTabPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseDetailsForm().getCurrent().getInputs().get("MAAT_ID"))
        .isEqualTo("updated");
  }

  @Test
  void postCaseDetailsIgnoresInputsNotBelongingToTheForm() throws Exception {
    var updatedForms = MockAmendmentFormsFunctions.justCaseDetailsFilled(claim);

    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request =
        post(buildAmendCaseDetailsPath())
            .param(INPUTS.formatted("FEE_CODE"), "tampered")
            .session(session)
            .with(csrf());

    mockMvc.perform(request).andExpect(status().is3xxRedirection());

    var saved =
        requireNonNull((AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId)));
    assertThat(saved.getCaseDetailsForm().getCurrent().getInputs().get("FEE_CODE")).isNull();
  }

  @Test
  void postCaseDetailsWithInvalidValueRedirectsBackAndPreservesInput() throws Exception {
    var updatedForms = MockAmendmentFormsFunctions.justCaseDetailsFilled(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var tooLong = "a".repeat(51);
    var request =
        post(buildAmendCaseDetailsPath())
            .param(INPUTS.formatted("UNIQUE_FILE_NUMBER"), tooLong)
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendCaseDetailsPath()));

    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseDetailsForm().getCurrent().getInputs().get("UNIQUE_FILE_NUMBER"))
        .isEqualTo(tooLong);
  }

  @Test
  void postCaseDetailsWithInvalidValueRendersErrorSummaryAndInlineErrorOnRedirect()
      throws Exception {
    var caseDetailsRows = Map.of("UNIQUE_FILE_NUMBER", "abc");
    var caseDetailsForm = new AmendmentForm();
    caseDetailsForm.setInputs(caseDetailsRows);

    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(caseDetailsForm)
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var tooLong = "a".repeat(51);
    var postRequest =
        post(buildAmendCaseDetailsPath())
            .param(INPUTS.formatted("UNIQUE_FILE_NUMBER"), tooLong)
            .session(session)
            .with(csrf());

    var postResult =
        mockMvc.perform(postRequest).andExpect(status().is3xxRedirection()).andReturn();

    var getRequest =
        get(buildAmendCaseDetailsPath())
            .session(session)
            .flashAttrs(postResult.getFlashMap())
            .with(csrf());

    mockMvc
        .perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("govuk-error-summary")))
        .andExpect(content().string(containsString("govuk-error-message")))
        .andExpect(
            content()
                .string(containsString("Unique file number (UFN) must be 50 characters or less")));
  }

  @Test
  void postCaseDetailsKeepsLockedFieldValuesWhenClaimAssessed() throws Exception {
    var crimeClaim = MockClaimsFunctions.createMockCrimeClaim();
    crimeClaim.setSubmissionId(submissionId);
    crimeClaim.setClaimId(claimId);
    crimeClaim.setUniqueFileNumber(UFN);
    crimeClaim.setSchemeId(SCHEME_ID);
    crimeClaim.setCaseEndDate(CASE_CONCLUDED_DATE);
    crimeClaim.setHasAssessment(true);
    crimeClaim.setLastAssessment(
        MockClaimsFunctions.createAssessment(AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT));
    session.setAttribute(claimId.toString(), crimeClaim);
    session.setAttribute(
        AMENDMENTS_KEY.formatted(claimId),
        MockAmendmentFormsFunctions.justCaseDetailsFilled(crimeClaim));

    mockMvc
        .perform(
            post(buildAmendCaseDetailsPath())
                .param(INPUTS.formatted("MAAT_ID"), "updated")
                .param(INPUTS.formatted("UNIQUE_FILE_NUMBER"), "tampered")
                .param(INPUTS.formatted("SCHEME_ID"), "tampered")
                .param(INPUTS.formatted("CASE_CONCLUDED_DATE-day"), "9")
                .param(INPUTS.formatted("CASE_CONCLUDED_DATE-month"), "9")
                .param(INPUTS.formatted("CASE_CONCLUDED_DATE-year"), "2030")
                .session(session)
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendCaseTabPath()));

    var saved =
        requireNonNull((AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId)));
    var current = saved.getCaseDetailsForm().getCurrent();

    assertThat(current.getInputs().get("MAAT_ID")).isEqualTo("updated");
    assertThat(current.getInputs().get("UNIQUE_FILE_NUMBER")).isEqualTo(UFN);
    assertThat(current.getInputs().get("SCHEME_ID")).isEqualTo(SCHEME_ID);
    assertThat(current.getInputs().get("CASE_CONCLUDED_DATE-day")).isEqualTo("6");
    assertThat(current.getInputs().get("CASE_CONCLUDED_DATE-month")).isEqualTo("4");
    assertThat(current.getInputs().get("CASE_CONCLUDED_DATE-year")).isEqualTo("2024");
  }

  @Test
  void postCaseDetailsKeepsLockedDateValuesWhenLegalHelpClaimAssessed() throws Exception {
    var civilClaim = MockClaimsFunctions.createMockCivilClaim();
    civilClaim.setSubmissionId(submissionId);
    civilClaim.setClaimId(claimId);
    civilClaim.setScheduleReference(SCHEDULE_REFERENCE);
    civilClaim.setUniqueFileNumber(UFN);
    civilClaim.setCaseStartDate(CASE_START_DATE);
    civilClaim.setCaseConcludedDate(CASE_CONCLUDED_DATE);
    civilClaim.setHasAssessment(true);
    civilClaim.setLastAssessment(
        MockClaimsFunctions.createAssessment(AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT));
    session.setAttribute(claimId.toString(), civilClaim);
    session.setAttribute(
        AMENDMENTS_KEY.formatted(claimId),
        MockAmendmentFormsFunctions.justCaseDetailsFilled(civilClaim));

    mockMvc
        .perform(
            post(buildAmendCaseDetailsPath())
                .param(INPUTS.formatted("SCHEDULE_REFERENCE_CIVIL"), "updated")
                .param(INPUTS.formatted("UNIQUE_FILE_NUMBER"), "010424/999")
                .param(INPUTS.formatted("CASE_START_DATE-day"), "9")
                .param(INPUTS.formatted("CASE_START_DATE-month"), "9")
                .param(INPUTS.formatted("CASE_START_DATE-year"), "2030")
                .param(INPUTS.formatted("CASE_CONCLUDED_CLAIMED_DATE-day"), "9")
                .param(INPUTS.formatted("CASE_CONCLUDED_CLAIMED_DATE-month"), "9")
                .param(INPUTS.formatted("CASE_CONCLUDED_CLAIMED_DATE-year"), "2030")
                .session(session)
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendCaseTabPath()));

    var saved =
        requireNonNull((AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId)));
    var inputs = saved.getCaseDetailsForm().getCurrent().getInputs();

    assertThat(inputs.get("CASE_START_DATE-day")).isEqualTo("5");
    assertThat(inputs.get("CASE_START_DATE-month")).isEqualTo("3");
    assertThat(inputs.get("CASE_START_DATE-year")).isEqualTo("2024");
    assertThat(inputs.get("CASE_CONCLUDED_CLAIMED_DATE-day")).isEqualTo("6");
    assertThat(inputs.get("CASE_CONCLUDED_CLAIMED_DATE-month")).isEqualTo("4");
    assertThat(inputs.get("CASE_CONCLUDED_CLAIMED_DATE-year")).isEqualTo("2024");

    assertThat(inputs.get("SCHEDULE_REFERENCE_CIVIL")).isEqualTo("updated");
    assertThat(inputs.get("UNIQUE_FILE_NUMBER")).isEqualTo("010424/999");
  }

  public String buildAmendCaseDetailsPath() {
    return "/submissions/%s/claims/%s/amendments/amend-case-details"
        .formatted(submissionId, claimId);
  }

  public String buildAmendCaseTabPath() {
    return "/submissions/%s/claims/%s/amendments/case".formatted(submissionId, claimId);
  }
}
