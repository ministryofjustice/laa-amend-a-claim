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
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.allRolesApartFrom;

import java.time.OffsetDateTime;
import java.util.Set;
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
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Role;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@ActiveProfiles("local")
@WebMvcTest(ClaimSummaryController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ClaimSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DummyUserSecurityService dummyUserSecurityService;

    @MockitoBean
    private MaintenanceService maintenanceService;

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private UserRetrievalService userRetrievalService;

    @MockitoBean
    private AssessmentService assessmentService;

    @MockitoBean
    private FeatureFlagsConfig featureFlagsConfig;

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

        mockMvc.perform(get(buildPath()).session(session))
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

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("claim-summary"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attribute("searchUrl", "/?providerAccountNumber=12345&page=1"))
                .andExpect(request().sessionAttribute(claimId.toString(), claim));
    }

    @ParameterizedTest
    @EnumSource(
            value = ClaimStatus.class,
            names = {"VALID", "VOID"},
            mode = EnumSource.Mode.EXCLUDE)
    public void testOnPageLoadReturnsNotFoundForNonValidStatus(ClaimStatus status) throws Exception {
        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setStatus(status);

        when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

        mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
    }

    @Test
    public void testOnSubmitRedirectsWhenClaimHasAnAssessment() throws Exception {
        dummyUserSecurityService.setRoles(Set.of(ROLE_ESCAPE_CASE_CASEWORKER));

        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

        claim.setHasAssessment(true);
        session.setAttribute(claimId.toString(), claim);

        String expectedRedirectUrl = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(post(buildPath()).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    @Test
    public void testOnSubmitRedirectsWhenClaimHasNoAssessment() throws Exception {
        dummyUserSecurityService.setRoles(Set.of(ROLE_ESCAPE_CASE_CASEWORKER));

        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();

        claim.setHasAssessment(false);
        session.setAttribute(claimId.toString(), claim);

        String expectedRedirectUrl =
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);

        mockMvc.perform(post(buildPath()).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    @Test
    void testIsAssessmentButtonPresentTrueForValidEscapeClaim() throws Exception {
        dummyUserSecurityService.setRoles(Set.of(ROLE_ESCAPE_CASE_CASEWORKER));

        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setHasAssessment(false);
        when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isAssessmentButtonPresent", true));
    }

    @Test
    void testIsAssessmentButtonPresentFalseForVoidClaim() throws Exception {
        dummyUserSecurityService.setRoles(Set.of(ROLE_ESCAPE_CASE_CASEWORKER));

        var user = MockClaimsFunctions.createUser();
        var claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setStatus(ClaimStatus.VOID);
        claim.setLastUpdatedUser(user.getId());
        claim.setLastUpdatedDateTime(OffsetDateTime.now());

        when(userRetrievalService.getMicrosoftApiUser(user.getId())).thenReturn(user);
        when(claimService.getClaimDetails(submissionId, claimId)).thenReturn(claim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isAssessmentButtonPresent", false));
    }

    @Test
    void testIsAssessmentButtonPresentFalseForNonEscapeClaim() throws Exception {
        dummyUserSecurityService.setRoles(Set.of(ROLE_ESCAPE_CASE_CASEWORKER));

        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setHasAssessment(false);
        claim.setEscaped(false);

        when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isAssessmentButtonPresent", false));
    }

    @Test
    void testIsAssessmentButtonPresentFalseWithoutRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_CASEWORKER));

        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setHasAssessment(false);

        when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isAssessmentButtonPresent", false));
    }

    @Test
    void testIsVoidButtonPresentTrueForValidClaim() throws Exception {
        dummyUserSecurityService.setRoles(Set.of(Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER));

        when(featureFlagsConfig.getIsVoidingEnabled()).thenReturn(true);

        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setStatus(ClaimStatus.VALID);

        when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isVoidButtonPresent", true));
    }

    @Test
    void testIsVoidButtonPresentFalseForVoidClaim() throws Exception {
        dummyUserSecurityService.setRoles(Set.of(Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER));

        when(featureFlagsConfig.getIsVoidingEnabled()).thenReturn(true);

        var user = MockClaimsFunctions.createUser();
        var claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setStatus(ClaimStatus.VOID);
        claim.setLastUpdatedUser(user.getId());
        claim.setLastUpdatedDateTime(OffsetDateTime.now());

        when(userRetrievalService.getMicrosoftApiUser(user.getId())).thenReturn(user);
        when(claimService.getClaimDetails(submissionId, claimId)).thenReturn(claim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isVoidButtonPresent", false));
    }

    @Test
    void testIsVoidButtonPresentFalseWithoutRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_CLAIM_AMENDMENTS_CASEWORKER));

        when(featureFlagsConfig.getIsVoidingEnabled()).thenReturn(true);

        var user = MockClaimsFunctions.createUser();
        var claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setStatus(ClaimStatus.VALID);
        claim.setLastUpdatedUser(user.getId());
        claim.setLastUpdatedDateTime(OffsetDateTime.now());

        when(userRetrievalService.getMicrosoftApiUser(user.getId())).thenReturn(user);
        when(claimService.getClaimDetails(submissionId, claimId)).thenReturn(claim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isVoidButtonPresent", false));
    }

    private String buildPath() {
        return String.format("/submissions/%s/claims/%s", submissionId, claimId);
    }
}
