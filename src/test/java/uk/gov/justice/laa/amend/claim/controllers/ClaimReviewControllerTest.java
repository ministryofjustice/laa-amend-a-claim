package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

@ActiveProfiles("local")
@WebMvcTest(ClaimReviewController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ClaimReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceService maintenanceService;

    @MockitoBean
    private AssessmentService assessmentService;

    private UUID submissionId;
    private UUID claimId;
    private MockHttpSession session;
    private ClaimDetails claim;

    @MockitoBean
    private ClaimStatusHandler claimStatusHandler;

    @BeforeEach
    void setup() {
        submissionId = UUID.randomUUID();
        claimId = UUID.randomUUID();
        session = new MockHttpSession();
        claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setSubmissionId(submissionId.toString());
        claim.setClaimId(claimId.toString());
        MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
        session.setAttribute(claimId.toString(), claim);
    }

    @Test
    public void testOnPageLoadReturnsViewWhenClaimInSession() throws Exception {
        // Given outcome for claim has been selected
        claim.setAssessmentOutcome(OutcomeType.PAID_IN_FULL);
        MockClaimsFunctions.updateStatus(claim, OutcomeType.PAID_IN_FULL);

        session.setAttribute(claimId.toString(), claim);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
        mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("review-and-amend"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("claimId", claimId.toString()))
                .andExpect(model().attribute("submissionId", submissionId.toString()))
                .andExpect(model().attribute("submissionFailed", false))
                .andExpect(model().attribute("validationFailed", false));
    }

    @Test
    public void testOnPageLoadRedirectsToAssessmentOutcomeWhenNotPresent() throws Exception {
        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
        claim.setAssessmentOutcome(null);
        session.setAttribute(claimId.toString(), claim);

        String expectedRedirectUrl =
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    @Test
    public void testSuccessfulSubmitRedirectsToConfirmation() throws Exception {
        UUID assessmentId = UUID.randomUUID();
        String userId = LocalSecurityConfig.userId;

        CreateAssessment201Response response = new CreateAssessment201Response();
        response.setId(assessmentId);

        when(assessmentService.submitAssessment(claim, userId)).thenReturn(response);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
        String redirectUrl =
                String.format("/submissions/%s/claims/%s/assessments/%s", submissionId, claimId, assessmentId);

        mockMvc.perform(post(path).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl))
                .andExpect(request().sessionAttributeDoesNotExist(claimId.toString()))
                .andExpect(request().sessionAttribute("assessmentId", assessmentId.toString()));

        verify(assessmentService).submitAssessment(claim, userId);
    }

    @Test
    public void testUnsuccessfulSubmitReloadsPageWithAlert() throws Exception {
        String userId = LocalSecurityConfig.userId;

        WebClientResponseException exception =
                WebClientResponseException.create(500, "Something went wrong", null, null, null);

        when(assessmentService.submitAssessment(any(), any())).thenThrow(exception);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(post(path).session(session))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("review-and-amend"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("claimId", claimId.toString()))
                .andExpect(model().attribute("submissionId", submissionId.toString()))
                .andExpect(model().attribute("submissionFailed", true))
                .andExpect(model().attribute("validationFailed", false));

        verify(assessmentService).submitAssessment(claim, userId);
    }

    @Test
    public void testUnsuccessfulValidationReloadsPageWithErrorSummary() throws Exception {
        ClaimField claimField = MockClaimsFunctions.createNetProfitCostField();
        claimField.setAssessed(null);
        claim.setNetProfitCost(claimField);

        session.setAttribute(claimId.toString(), claim);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(post(path).session(session))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("review-and-amend"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("claimId", claimId.toString()))
                .andExpect(model().attribute("submissionId", submissionId.toString()))
                .andExpect(model().attribute("submissionFailed", false))
                .andExpect(model().attribute("validationFailed", true));
    }

    @Test
    public void testOnPageLoadWithMultipleClaimsInSession() throws Exception {

        session.clearAttributes();
        ClaimDetails claim1 = MockClaimsFunctions.createMockCivilClaim();
        claim1.setSubmissionId(submissionId.toString());
        String claimId1 = UUID.randomUUID().toString();
        claim1.setClaimId(claimId1);
        claim1.setAssessmentOutcome(OutcomeType.PAID_IN_FULL);
        MockClaimsFunctions.updateStatus(claim1, OutcomeType.PAID_IN_FULL);

        ClaimDetails claim2 = MockClaimsFunctions.createMockCrimeClaim();
        claim2.setSubmissionId(submissionId.toString());
        String claimId2 = UUID.randomUUID().toString();
        claim2.setClaimId(claimId2);
        claim2.setAssessmentOutcome(OutcomeType.NILLED);
        MockClaimsFunctions.updateStatus(claim2, claim2.getAssessmentOutcome());

        session.setAttribute(claimId1, claim1);
        session.setAttribute(claimId2, claim2);

        // Load first claim
        String path1 = String.format("/submissions/%s/claims/%s/review", submissionId, claimId1);

        mockMvc.perform(get(path1).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("claimId", claimId1));

        // Verify both claims still in session
        Assertions.assertNotNull(session.getAttribute(claimId1));
        Assertions.assertNotNull(session.getAttribute(claimId2));
    }
}
