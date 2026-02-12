package uk.gov.justice.laa.amend.claim.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
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

    @MockitoBean
    private MaintenanceService maintenanceService;

    @BeforeEach
    void setup() {
        submissionId = UUID.randomUUID();
        claimId = UUID.randomUUID();
        session = new MockHttpSession();
        session.setAttribute(claimId.toString(), MockClaimsFunctions.createMockCivilClaim());
    }

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        String uri = String.format("/submissions/%s/claims/%s/discard", submissionId, claimId);

        mockMvc.perform(get(uri).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("discard"))
                .andExpect(model().attribute("submissionId", submissionId.toString()))
                .andExpect(model().attribute("claimId", claimId.toString()));
    }

    @Test
    public void testDiscardRemovesClaimFromSessionAndRedirects() throws Exception {
        String uri = String.format("/submissions/%s/claims/%s/discard", submissionId, claimId);

        String expectedRedirectUrl = "/";

        mockMvc.perform(post(uri).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl))
                .andExpect(flash().attribute("discarded", true))
                .andExpect(result -> Assertions.assertNull(session.getAttribute(claimId.toString())));
    }
}
