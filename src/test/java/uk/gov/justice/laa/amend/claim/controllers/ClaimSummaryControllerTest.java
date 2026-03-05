package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@ActiveProfiles("local")
@WebMvcTest(ClaimSummaryController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ClaimSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceService maintenanceService;

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private UserRetrievalService userRetrievalService;

    @MockitoBean
    private AssessmentService assessmentService;

    private UUID submissionId;
    private UUID claimId;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        submissionId = UUID.randomUUID();
        claimId = UUID.randomUUID();
        session = new MockHttpSession();
    }

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

        when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

        var lastAssessment = new AssessmentInfo();
        lastAssessment.setLastAssessedBy("test");
        lastAssessment.setLastAssessmentDate(OffsetDateTime.now());
        claim.setLastAssessment(lastAssessment);
        when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);

        String path = String.format("/submissions/%s/claims/%s", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("claim-summary"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("searchUrl", "/"))
                .andExpect(request().sessionAttribute(claimId.toString(), claim));
    }

    @Test
    public void testOnPageLoadWithCachedSearchUrlReturnsView() throws Exception {
        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

        session.setAttribute("searchUrl", "/?providerAccountNumber=12345&page=1");

        when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

        var lastAssessment = new AssessmentInfo();
        lastAssessment.setLastAssessedBy("test");
        lastAssessment.setLastAssessmentDate(OffsetDateTime.now());
        claim.setLastAssessment(lastAssessment);
        when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);

        String path = String.format("/submissions/%s/claims/%s", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("claim-summary"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("searchUrl", "/?providerAccountNumber=12345&page=1"))
                .andExpect(request().sessionAttribute(claimId.toString(), claim));
    }

    @ParameterizedTest
    @EnumSource(
            value = ClaimStatus.class,
            names = {"VALID"},
            mode = EnumSource.Mode.EXCLUDE)
    public void testOnPageLoadReturnsNotFoundForNonValidStatus(ClaimStatus status) throws Exception {
        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setStatus(status);

        when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

        String path = String.format("/submissions/%s/claims/%s", submissionId, claimId);

        mockMvc.perform(get(path).session(session)).andExpect(status().isNotFound());
    }

    @Test
    public void testOnSubmitRedirectsWhenClaimHasAnAssessment() throws Exception {
        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

        claim.setHasAssessment(true);
        session.setAttribute(claimId.toString(), claim);

        String path = String.format("/submissions/%s/claims/%s", submissionId, claimId);

        String expectedRedirectUrl = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(post(path).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    @Test
    public void testOnSubmitRedirectsWhenClaimHasNoAssessment() throws Exception {
        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

        claim.setHasAssessment(false);
        session.setAttribute(claimId.toString(), claim);

        String path = String.format("/submissions/%s/claims/%s", submissionId, claimId);

        String expectedRedirectUrl =
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);

        mockMvc.perform(post(path).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }
}
