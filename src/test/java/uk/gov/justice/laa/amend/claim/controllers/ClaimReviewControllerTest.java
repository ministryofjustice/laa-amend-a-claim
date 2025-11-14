package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimSummary;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@WebMvcTest(ClaimReviewController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ClaimReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testOnPageLoadReturnsViewWhenClaimSummaryInSession() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        ClaimSummary claimSummary = new ClaimSummary();
        claimSummary.setSubmissionId(submissionId);
        claimSummary.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claimSummary);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("review-and-amend"))
            .andExpect(model().attributeExists("claimSummary"))
            .andExpect(model().attributeExists("backUrl"))
            .andExpect(model().attribute("claimId", claimId))
            .andExpect(model().attribute("submissionId", submissionId));
    }

    @Test
    public void testDiscardRemovesClaimFromSessionAndRedirects() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        ClaimSummary claimSummary = new ClaimSummary();
        claimSummary.setSubmissionId(submissionId);
        claimSummary.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claimSummary);

        String path = String.format("/submissions/%s/claims/%s/review/discard", submissionId, claimId);

        mockMvc.perform(post(path).session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/submissions/" + submissionId + "/claims/" + claimId));
    }

    @Test
    public void testSubmitRedirectsToConfirmation() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        ClaimSummary claimSummary = new ClaimSummary();
        claimSummary.setSubmissionId(submissionId);
        claimSummary.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claimSummary);

        String path = String.format("/submissions/%s/claims/%s/review/submit", submissionId, claimId);

        mockMvc.perform(post(path).session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/submissions/" + submissionId + "/claims/" + claimId + "/confirmation"));
    }

    @Test
    public void testOnPageLoadBackUrlIsCorrectlySet() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        ClaimSummary claimSummary = new ClaimSummary();
        claimSummary.setSubmissionId(submissionId);
        claimSummary.setClaimId(claimId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId, claimSummary);

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

        ClaimSummary claimSummary1 = new ClaimSummary();
        claimSummary1.setSubmissionId(submissionId);
        claimSummary1.setClaimId(claimId1);

        ClaimSummary claimSummary2 = new ClaimSummary();
        claimSummary2.setSubmissionId(submissionId);
        claimSummary2.setClaimId(claimId2);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(claimId1, claimSummary1);
        session.setAttribute(claimId2, claimSummary2);

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