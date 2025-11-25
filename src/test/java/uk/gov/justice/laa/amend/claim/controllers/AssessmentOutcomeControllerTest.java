package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@WebMvcTest(AssessmentOutcomeController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class AssessmentOutcomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssessmentService assessmentService;

    @Test
    public void testGetAssessmentOutcome_ReturnsView() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        MockHttpSession session = new MockHttpSession();
        String path = String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);

        mockMvc.perform(
                get(path).session(session)
            )
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("assessmentOutcomeForm"))
            .andExpect(view().name("assessment-outcome"));
    }

    @Test
    public void testOnSubmitReturnsBadRequestWithViewForInvalidForm() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        MockHttpSession session = new MockHttpSession();
        String path = String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);

        mockMvc.perform(
                post(path).session(session)
                    .with(csrf())
                    .param("assessmentOutcome", "")
                    .param("liabilityForVat", "")
            )
            .andExpect(status().isBadRequest())
            .andExpect(view().name("assessment-outcome"));
    }

    @Test
    public void testOnSubmitRedirects() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        MockHttpSession session = new MockHttpSession();
        String path = String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);

        String redirectUrl = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(
                post(path).session(session)
                    .with(csrf())
                    .param("assessmentOutcome", "paid-in-full")
                    .param("liabilityForVat", "true")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl));
    }
}