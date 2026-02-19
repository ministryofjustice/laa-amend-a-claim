package uk.gov.justice.laa.amend.claim.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(ConfirmationController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ConfirmationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceService maintenanceService;

    private MockHttpSession session;

    @BeforeEach
    public void setup() {
        session = new MockHttpSession();
    }

    @Test
    public void testOnPageLoadReturnsViewWhenStoredAssessmentIdMatches() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();
        String assessmentId = UUID.randomUUID().toString();

        session.setAttribute("assessmentId", assessmentId);

        String uri = String.format("/submissions/%s/claims/%s/assessments/%s", submissionId, claimId, assessmentId);

        mockMvc.perform(get(uri).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmation"))
                .andExpect(model().attribute("submissionId", submissionId))
                .andExpect(model().attribute("claimId", claimId));
    }

    @Test
    public void testOnPageLoadReturnsNotFoundWhenStoredAssessmentIdDoesNotMatch() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();
        String assessmentId1 = UUID.randomUUID().toString();
        String assessmentId2 = UUID.randomUUID().toString();

        session.setAttribute("assessmentId", assessmentId1);

        String uri = String.format("/submissions/%s/claims/%s/assessments/%s", submissionId, claimId, assessmentId2);

        mockMvc.perform(get(uri).session(session)).andExpect(status().isNotFound());
    }

    @Test
    public void testOnPageLoadReturnsNotFoundWhenNoStoredAssessmentId() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();
        String assessmentId = UUID.randomUUID().toString();

        String uri = String.format("/submissions/%s/claims/%s/assessments/%s", submissionId, claimId, assessmentId);

        mockMvc.perform(get(uri)).andExpect(status().isNotFound());
    }
}
