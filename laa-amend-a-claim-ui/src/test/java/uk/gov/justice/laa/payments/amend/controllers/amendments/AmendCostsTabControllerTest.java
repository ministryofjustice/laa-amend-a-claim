package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.AMENDMENTS_KEY;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveClaim;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.payments.amend.controllers.BaseControllerTest;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.viewmodels.AmendmentsHeaderView;

@WebMvcTest(controllers = AmendCostsTabController.class)
class AmendCostsTabControllerTest extends BaseControllerTest {

  private static final String INPUTS = "inputs[%s]";

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
    saveClaim(session, claimId, claim);
    when(amendmentsHeaderViewFactory.create(any()))
        .thenReturn(new AmendmentsHeaderView(false, null));
  }

  @Test
  void getCostsAsExpected() throws Exception {
    var forms =
        AmendmentForms.builder()
            .client1(new AmendmentForm())
            .caseType(new AmendmentForm())
            .caseDetails(new AmendmentForm())
            .build();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    mockMvc
        .perform(get(buildCostsPath()).session(session).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/view-costs"))
        .andExpect(request().sessionAttribute(AMENDMENTS_KEY.formatted(claimId), forms));
  }

  private String buildCostsPath() {
    return "/submissions/%s/claims/%s/amendments/costs".formatted(submissionId, claimId);
  }

}
