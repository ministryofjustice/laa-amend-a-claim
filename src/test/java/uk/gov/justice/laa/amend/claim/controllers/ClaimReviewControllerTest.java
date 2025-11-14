package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;

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
    public void testOnPageLoadRedirectsWhenClaimSummaryNotInSession() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        MockHttpSession session = new MockHttpSession();
        // No claim summary in session

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/submissions/" + submissionId + "/claims/" + claimId));
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
}