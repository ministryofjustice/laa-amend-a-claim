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
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldType;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.*;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DISBURSEMENT_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_DISBURSEMENTS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;

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
        CivilClaimDetails claim = new CivilClaimDetails();
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

        claim.setVatClaimed(new ClaimField(VAT, 80, 75, 78, ClaimFieldType.NORMAL));
        claim.setFixedFee(new ClaimField(FIXED_FEE, 500, 480, 490, ClaimFieldType.NORMAL));
        claim.setNetProfitCost(new ClaimField(NET_PROFIT_COST, 600, 580, 590, ClaimFieldType.NORMAL));
        claim.setNetDisbursementAmount(new ClaimField(NET_DISBURSEMENTS_COST, 200, 190, 195, ClaimFieldType.NORMAL));
        claim.setTotalAmount(new ClaimField(TOTAL, 1380, 1325, 1350, ClaimFieldType.TOTAL));
        claim.setDisbursementVatAmount(new ClaimField(DISBURSEMENT_VAT, 40, 38, 39, ClaimFieldType.NORMAL));

        // Civil-specific fields
        claim.setDetentionTravelWaitingCosts(new ClaimField(DETENTION_TRAVEL_COST, BigDecimal.valueOf(50), BigDecimal.valueOf(55), ClaimFieldType.NORMAL));
        claim.setJrFormFillingCost(new ClaimField(JR_FORM_FILLING, BigDecimal.valueOf(30), BigDecimal.valueOf(35), ClaimFieldType.NORMAL));
        claim.setAdjournedHearing(new ClaimField(ADJOURNED_FEE, BigDecimal.valueOf(20), BigDecimal.valueOf(25), ClaimFieldType.BOLT_ON));
        claim.setCmrhTelephone(new ClaimField(CMRH_TELEPHONE, BigDecimal.valueOf(15), BigDecimal.valueOf(18), ClaimFieldType.BOLT_ON));
        claim.setCmrhOral(new ClaimField(CMRH_ORAL, BigDecimal.valueOf(40), BigDecimal.valueOf(45), ClaimFieldType.BOLT_ON));
        claim.setHoInterview(new ClaimField(HO_INTERVIEW, BigDecimal.valueOf(60), BigDecimal.valueOf(65), ClaimFieldType.BOLT_ON));
        claim.setSubstantiveHearing(new ClaimField(SUBSTANTIVE_HEARING, BigDecimal.valueOf(100), BigDecimal.valueOf(110), ClaimFieldType.BOLT_ON));
        claim.setCounselsCost(new ClaimField(COUNSELS_COST, BigDecimal.valueOf(200), BigDecimal.valueOf(220), ClaimFieldType.NORMAL));
        claim.setMatterTypeCode("IMM:DET");

        return claim;
    }
}
