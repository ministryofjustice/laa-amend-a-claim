package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("local")
@WebMvcTest(ClaimReviewController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ClaimReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssessmentService assessmentService;

    @Test
    public void testOnPageLoadReturnsViewWhenClaimInSession() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        Claim claim = new CivilClaimDetails();
        claim.setSubmissionId(submissionId);
        claim.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claim);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("review-and-amend"))
            .andExpect(model().attributeExists("claim"))
            .andExpect(model().attributeExists("backUrl"))
            .andExpect(model().attribute("claimId", claimId))
            .andExpect(model().attribute("submissionId", submissionId));
    }

    @Test
    public void testDiscardRemovesClaimFromSessionAndRedirects() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        Claim claim = new CivilClaimDetails();
        claim.setSubmissionId(submissionId);
        claim.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claim);

        String path = String.format("/submissions/%s/claims/%s/review/discard", submissionId, claimId);

        mockMvc.perform(post(path).session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/submissions/" + submissionId + "/claims/" + claimId));
    }

    @Test
    public void testSuccessfulSubmitRedirectsToConfirmation() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();
        String userId = LocalSecurityConfig.userId;

        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setSubmissionId(submissionId);
        claim.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claim);

        when(assessmentService.submitAssessment(claim, userId))
            .thenReturn(ResponseEntity.ok(new CreateAssessment201Response()));

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
        String redirectUrl = String.format("/submissions/%s/claims/%s/confirmation", submissionId, claimId);

        mockMvc.perform(post(path).session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl))
            .andExpect(MockMvcResultMatchers.request().sessionAttributeDoesNotExist(claimId));

        verify(assessmentService).submitAssessment(claim, userId);
    }

    @Test
    public void testUnsuccessfulSubmitReloadsPageWithAlert() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();
        String userId = LocalSecurityConfig.userId;

        CivilClaimDetails claim = new CivilClaimDetails();
        claim.setSubmissionId(submissionId);
        claim.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claim);

        when(assessmentService.submitAssessment(claim, userId))
            .thenThrow(WebClientResponseException.InternalServerError.class);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(post(path).session(session))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("review-and-amend"))
            .andExpect(model().attributeExists("claim"))
            .andExpect(model().attributeExists("backUrl"))
            .andExpect(model().attribute("claimId", claimId))
            .andExpect(model().attribute("submissionId", submissionId))
            .andExpect(model().attribute("submissionFailed", true));

        verify(assessmentService).submitAssessment(claim, userId);
    }

    @Test
    public void testOnPageLoadBackUrlIsCorrectlySet() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        Claim claim = new CivilClaimDetails();
        claim.setSubmissionId(submissionId);
        claim.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claim);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
        String expectedBackUrl = String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().isOk())
            .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    @Test
    public void testOnPageLoadWithMultipleClaimsInSession() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId1 = UUID.randomUUID().toString();
        String claimId2 = UUID.randomUUID().toString();

        Claim claim1 = new CivilClaimDetails();
        claim1.setSubmissionId(submissionId);
        claim1.setClaimId(claimId1);

        Claim claim2 = new CrimeClaimDetails();
        claim2.setSubmissionId(submissionId);
        claim2.setClaimId(claimId2);

        MockHttpSession session = new MockHttpSession();
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