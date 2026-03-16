package uk.gov.justice.laa.amend.claim.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Set;
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
import uk.gov.justice.laa.amend.claim.models.Role;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(ConfirmationController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ConfirmationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DummyUserSecurityService dummyUserSecurityService;

    @MockitoBean
    private MaintenanceService maintenanceService;

    private MockHttpSession session;
    private UUID claimId;
    private UUID submissionId;
    private UUID assessmentId;

    @BeforeEach
    public void setup() {
        session = new MockHttpSession();
        claimId = UUID.randomUUID();
        submissionId = UUID.randomUUID();
        assessmentId = UUID.randomUUID();

        dummyUserSecurityService.setRoles(Set.of(Role.ROLE_ESCAPE_CASE_CASEWORKER));
    }

    @Test
    public void testOnPageLoadReturnsViewWhenStoredAssessmentIdMatches() throws Exception {
        session.setAttribute("assessmentId", assessmentId);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmation"))
                .andExpect(model().attribute("submissionId", submissionId))
                .andExpect(model().attribute("claimId", claimId));
    }

    @Test
    public void testOnPageLoadReturnsNotFoundWhenStoredAssessmentIdDoesNotMatch() throws Exception {
        UUID assessmentId1 = UUID.randomUUID();
        UUID assessmentId2 = UUID.randomUUID();

        session.setAttribute("assessmentId", assessmentId1);

        String path = String.format("/submissions/%s/claims/%s/assessments/%s", submissionId, claimId, assessmentId2);

        mockMvc.perform(get(path).session(session)).andExpect(status().isNotFound());
    }

    @Test
    public void testOnPageLoadReturnsNotFoundWhenNoStoredAssessmentId() throws Exception {
        mockMvc.perform(get(buildPath())).andExpect(status().isNotFound());
    }

    @Test
    void testGetRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(Role.allRolesApartFrom(Role.ROLE_ESCAPE_CASE_CASEWORKER));
        mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isForbidden());
    }

    @Test
    void testPostRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(Role.allRolesApartFrom(Role.ROLE_ESCAPE_CASE_CASEWORKER));
        mockMvc.perform(post(buildPath()).session(session)).andExpect(status().isForbidden());
    }

    private String buildPath() {
        return String.format("/submissions/%s/claims/%s/assessments/%s", submissionId, claimId, assessmentId);
    }
}
