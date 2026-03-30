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
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.allRolesApartFrom;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@WebMvcTest(DiscardController.class)
public class DiscardControllerTest extends BaseControllerTest {

    private UUID submissionId;
    private UUID claimId;
    private MockHttpSession session;

    @BeforeEach
    void setup() {
        submissionId = UUID.randomUUID();
        claimId = UUID.randomUUID();
        session = new MockHttpSession();
        session.setAttribute(claimId.toString(), MockClaimsFunctions.createMockCivilClaim());
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
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_CASEWORKER));
        mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isForbidden());
    }

    @Test
    void testPostRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_CASEWORKER));
        mockMvc.perform(post(buildPath()).session(session)).andExpect(status().isForbidden());
    }

    private String buildPath() {
        return String.format("/submissions/%s/claims/%s/discard", submissionId, claimId);
    }
}
