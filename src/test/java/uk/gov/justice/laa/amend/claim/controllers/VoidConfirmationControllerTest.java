package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.allRolesApartFrom;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.VoidClaim201Response;

@WebMvcTest(controllers = VoidConfirmationController.class)
public class VoidConfirmationControllerTest extends BaseControllerTest {

    private static final UUID USER_ID = UUID.fromString(DummyUserSecurityService.USER_ID);

    @MockitoBean
    private ClaimService claimService;

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
        claim.setSubmissionId(submissionId);
        claim.setClaimId(claimId);
        MockClaimsFunctions.updateStatus(claim, claim.getAssessmentOutcome());
        session.setAttribute(claimId.toString(), claim);
    }

    @Test
    public void testOnPageLoadReturnsViewWhenClaimInSession() throws Exception {
        session.setAttribute(claimId.toString(), claim);

        when(claimService.voidClaim(claimId, USER_ID)).thenReturn(new VoidClaim201Response(UUID.randomUUID()));

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("void-confirmation"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("claimId", claimId))
                .andExpect(model().attribute("submissionId", submissionId))
                .andExpect(model().attribute("submissionFailed", false));
    }

    @Test
    public void testSuccessfulSubmitRedirectsToSearch() throws Exception {
        var redirectUrl = "/";

        when(claimService.voidClaim(claimId, USER_ID)).thenReturn(new VoidClaim201Response(UUID.randomUUID()));

        mockMvc.perform(post(buildPath()).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl))
                .andExpect(flash().attribute("voided", true))
                .andExpect(request().sessionAttributeDoesNotExist(claimId.toString()));
    }

    @Test
    public void testSuccessfulSubmitRedirectsToSearchInSession() throws Exception {
        var searchUrl = "/?officeCode=123456";
        session.setAttribute("searchUrl", searchUrl);

        when(claimService.voidClaim(claimId, USER_ID)).thenReturn(new VoidClaim201Response(UUID.randomUUID()));

        mockMvc.perform(post(buildPath()).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(searchUrl))
                .andExpect(flash().attribute("voided", true))
                .andExpect(request().sessionAttributeDoesNotExist(claimId.toString()));
    }

    @Test
    public void testUnsuccessfulSubmitReloadsPageWithAlert() throws Exception {
        when(claimService.voidClaim(claimId, USER_ID)).thenThrow(new RuntimeException());

        mockMvc.perform(post(buildPath()).session(session).with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("void-confirmation"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("claimId", claimId))
                .andExpect(model().attribute("submissionId", submissionId))
                .andExpect(model().attribute("submissionFailed", true));
    }

    @Test
    void testGetRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_CLAIM_AMENDMENTS_CASEWORKER));
        mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isForbidden());
    }

    @Test
    void testPostRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_CLAIM_AMENDMENTS_CASEWORKER));
        mockMvc.perform(post(buildPath()).session(session)).andExpect(status().isForbidden());
    }

    private String buildPath() {
        return String.format("/submissions/%s/claims/%s/void", submissionId, claimId);
    }
}
