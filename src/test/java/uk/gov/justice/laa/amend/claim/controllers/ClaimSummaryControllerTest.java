package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("local")
@WebMvcTest(ClaimSummaryController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ClaimSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private UserRetrievalService userRetrievalService;


    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;


    @MockitoBean
    private AssessmentService assessmentService;
    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        CivilClaimDetails claim = createClaim();

        MockHttpSession session = new MockHttpSession();

        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);

        var lastAssessment = new AssessmentInfo();
        lastAssessment.setLastAssessedBy("test");
        lastAssessment.setLastAssessmentDate(OffsetDateTime.now());
        claim.setLastAssessment(lastAssessment);
        when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);

        String path = String.format("/submissions/%s/claims/%s", submissionId, claimId);
        OAuth2AuthorizedClient mockClient = mock(OAuth2AuthorizedClient.class);
        when(mockClient.getAccessToken()).thenReturn(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "mock-token", Instant.now(), Instant.now().plusSeconds(3600)));
        when(authorizedClientService.loadAuthorizedClient(eq("entra"), anyString()))
                .thenReturn(mockClient);


        mockMvc.perform(get(path).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("claim-summary"))
            .andExpect(model().attributeExists("claim"));

        Assertions.assertEquals(claim, session.getAttribute(claimId));
    }

    private static CivilClaimDetails createClaim() {
        CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
        claim.setUniqueFileNumber("UFN12345");
        claim.setCaseReferenceNumber("CASE98765");
        claim.setClientSurname("Doe");
        claim.setClientForename("Jane");
        claim.setCaseStartDate(LocalDate.now().minusDays(2));
        claim.setCaseEndDate(LocalDate.now());
        claim.setFeeCode("FeeCode");
        claim.setFeeCodeDescription("FeeCodeDesc");
        claim.setAreaOfLaw("Civil");
        claim.setSubmittedDate(LocalDateTime.now().minusDays(10));
        claim.setEscaped(true);
        claim.setProviderAccountNumber("ACC123");
        claim.setProviderName("Provider Ltd");
        claim.setMatterTypeCode("IMM:DET");

        return claim;
    }
}
