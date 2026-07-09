package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import uk.gov.justice.laa.amend.claim.controllers.BaseControllerTest;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(AmendCaseDetailsController.class)
class AmendCaseDetailsControllerTest extends BaseControllerTest {

  private static final String INPUTS = "inputs[%s]";

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private ClaimDetails claim;

  private static final String FEE_CODE = "feecode";
  private static final String MATTER_TYPE_CODE = "mattertype";

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
  void getCaseDetailsAsExpected() throws Exception {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE);
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    session.setAttribute(claimId.toString(), claim);

    var caseDetailsRows = Map.of("FEE_CODE", FEE_CODE);
    var caseDetailsForm = new AmendmentForm();
    caseDetailsForm.setInputs(caseDetailsRows);

    var updatedForms =
        new AmendmentForms(new AmendmentForm(), new AmendmentForm(), caseDetailsForm);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms);

    var request = get(buildAmendCaseDetailsPath()).session(session).with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("amendments/amend-case-details"))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), updatedForms));
  }

  @Test
  void postCaseDetailsAsExpected() throws Exception {
    var caseDetailsRows = Map.of("FEE_CODE", FEE_CODE);
    var caseDetailsForm = new AmendmentForm();
    caseDetailsForm.setInputs(caseDetailsRows);

    var forms = new AmendmentForms(new AmendmentForm(), new AmendmentForm(), caseDetailsForm);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var request =
        post(buildAmendCaseDetailsPath())
            .param(INPUTS.formatted("FEE_CODE"), "updated")
            .session(session)
            .with(csrf());

    mockMvc
        .perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(buildAmendCaseTabPath()))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), forms));
    AmendmentForms updatedForm =
        (AmendmentForms) session.getAttribute(AMENDMENTS_KEY.formatted(claimId));
    assertThat(updatedForm.getCaseDetailsForm().getCurrent().getInputs().get("FEE_CODE"))
        .isEqualTo("updated");
  }

  public String buildAmendCaseDetailsPath() {
    return "/submissions/%s/claims/%s/amendments/amend-case-details"
        .formatted(submissionId, claimId);
  }

  public String buildAmendCaseTabPath() {
    return "/submissions/%s/claims/%s/amendments/case".formatted(submissionId, claimId);
  }
}
