package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldValue;
import uk.gov.justice.laa.amend.claim.service.ClaimService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        CivilClaimDetails claim = createClaim();

        MockHttpSession session = new MockHttpSession();

        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);

        String path = String.format("/submissions/%s/claims/%s", submissionId, claimId);

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
        claim.setFeeScheme("FeeSchemeX");
        claim.setAreaOfLaw("Civil");
        claim.setSubmittedDate(LocalDate.now().minusDays(10));
        claim.setEscaped(true);
        claim.setProviderAccountNumber("ACC123");
        claim.setProviderName("Provider Ltd");

        claim.setVatClaimed(createClaimField());
        claim.setFixedFee(createClaimField());
        claim.setNetProfitCost(createClaimField());
        claim.setNetDisbursementAmount(createClaimField());
        claim.setTotalAmount(createClaimField());
        claim.setDisbursementVatAmount(createClaimField());

// Civil-specific fields
        claim.setDetentionTravelWaitingCosts(createClaimField());
        claim.setJrFormFillingCost(createClaimField());
        claim.setAdjournedHearing(createClaimField());
        claim.setCmrhTelephone(createClaimField());
        claim.setCmrhOral(createClaimField());
        claim.setHoInterview(createClaimField());
        claim.setSubstantiveHearing(createClaimField());
        claim.setCounselsCost(createClaimField());
        claim.setMatterTypeCode("IMM:DET");

        return claim;
    }

    private static ClaimField createClaimField() {
        return new ClaimField(
            "foo",
            ClaimFieldValue.of(BigDecimal.valueOf(100)),
            ClaimFieldValue.of(BigDecimal.valueOf(200)),
            ClaimFieldValue.of(BigDecimal.valueOf(300))
        );
    }
}
