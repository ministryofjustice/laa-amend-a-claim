package uk.gov.justice.laa.amend.claim.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
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
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(DiscardController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class DiscardControllerTest {

    private UUID submissionId;
    private UUID claimId;
    private MockHttpSession session;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DummyUserSecurityService dummyUserSecurityService;

    @MockitoBean
    private MaintenanceService maintenanceService;

    @BeforeEach
    void setup() {
        submissionId = UUID.randomUUID();
        claimId = UUID.randomUUID();
        session = new MockHttpSession();
        session.setAttribute(claimId.toString(), MockClaimsFunctions.createMockCivilClaim());

        dummyUserSecurityService.setRoles(Set.of(Role.ROLE_ESCAPE_CASE_CASEWORKER));
    }

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("discard"))
                .andExpect(model().attribute("submissionId", submissionId))
                .andExpect(model().attribute("claimId", claimId));
    }

    @Test
    public void testDiscardRemovesClaimFromSessionAndRedirectsWhenSearchUrlIsCached() throws Exception {
        String searchUrl = "/?page=1";
        session.setAttribute("searchUrl", searchUrl);

        mockMvc.perform(post(buildPath()).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(searchUrl))
                .andExpect(flash().attribute("discarded", true))
                .andExpect(request().sessionAttributeDoesNotExist(claimId.toString()));
    }

    @Test
    public void testDiscardRemovesClaimFromSessionAndRedirectsWhenSearchUrlIsNotCached() throws Exception {
        mockMvc.perform(post(buildPath()).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("discarded", true))
                .andExpect(request().sessionAttributeDoesNotExist(claimId.toString()));
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
        return String.format("/submissions/%s/claims/%s/discard", submissionId, claimId);
    }
}
