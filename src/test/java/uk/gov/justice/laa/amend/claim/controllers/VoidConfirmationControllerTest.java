package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
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
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(controllers = VoidConfirmationController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class VoidConfirmationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceService maintenanceService;

    @MockitoBean
    private AssessmentService assessmentService;

    @MockitoBean
    private FeatureFlagsConfig featureFlagsConfig;

    private UUID submissionId;
    private UUID claimId;
    private MockHttpSession session;
    private ClaimDetails claim;

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
        when(featureFlagsConfig.getIsVoidingEnabled()).thenReturn(true);
    }

    @Test
    public void testOnPageLoadReturnsViewWhenClaimInSession() throws Exception {
        session.setAttribute(claimId.toString(), claim);

        var path = String.format("/submissions/%s/claims/%s/void", submissionId, claimId);
        mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("void-confirmation"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("claimId", claimId))
                .andExpect(model().attribute("submissionId", submissionId))
                .andExpect(model().attribute("submissionFailed", false));
    }

    @Test
    public void testSuccessfulSubmitRedirectsToSearch() throws Exception {
        var path = String.format("/submissions/%s/claims/%s/void", submissionId, claimId);
        var redirectUrl = "/";

        // TODO: BC-382: Mock void request

        mockMvc.perform(post(path).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl))
                .andExpect(request().sessionAttributeDoesNotExist(claimId.toString()));
    }

    @Test
    public void testSuccessfulSubmitRedirectsToSearchInSession() throws Exception {
        var path = String.format("/submissions/%s/claims/%s/void", submissionId, claimId);
        var searchUrl = "/?providerAccountNumber=123456";
        session.setAttribute("searchUrl", searchUrl);

        // TODO: BC-382: Mock void request

        mockMvc.perform(post(path).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(searchUrl))
                .andExpect(request().sessionAttributeDoesNotExist(claimId.toString()));
    }

    @Test
    public void testUnsuccessfulSubmitReloadsPageWithAlert() {
        // TODO: BC-382: Mock void request to throw error and and test for submissionFailed
    }
}
