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
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
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
    private ClaimMapper claimMapper;



    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        CivilClaimSummary claim = createClaim();

        MockHttpSession session = new MockHttpSession();

        when(claimService.getClaim(anyString(), anyString())).thenReturn(new ClaimResponse());
        when(claimMapper.mapToCivilClaimSummary(any())).thenReturn(claim);

        String path = String.format("/submissions/%s/claims/%s", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("claim-summary"))
            .andExpect(model().attributeExists("claim"));

        Assertions.assertEquals(claim, session.getAttribute(claimId));
    }

    private static CivilClaimSummary createClaim() {
        CivilClaimSummary claim = new CivilClaimSummary();
        claim.setUniqueFileNumber("UFN12345");
        claim.setCaseReferenceNumber("CASE98765");
        claim.setClientSurname("Doe");
        claim.setClientForename("Jane");
        claim.setCaseStartDate(LocalDate.now().minusDays(2));
        claim.setCaseEndDate(LocalDate.now());
        claim.setFeeScheme("FeeSchemeX");
        claim.setCategoryOfLaw("Civil");
        claim.setSubmittedDate("2025-02-20");
        claim.setEscaped(true);
        claim.setProviderAccountNumber("ACC123");
        claim.setProviderName("Provider Ltd");

        claim.setVatClaimed(new ClaimFieldRow(VAT, 80, 75, 78));
        claim.setFixedFee(new ClaimFieldRow(FIXED_FEE, 500, 480, 490));
        claim.setNetProfitCost(new ClaimFieldRow(NET_PROFIT_COST, 600, 580, 590));
        claim.setNetDisbursementAmount(new ClaimFieldRow(NET_DISBURSEMENTS_COST, 200, 190, 195));
        claim.setTotalAmount(new ClaimFieldRow(TOTAL, 1380, 1325, 1350));
        claim.setDisbursementVatAmount(new ClaimFieldRow(DISBURSEMENT_VAT, 40, 38, 39));

// Civil-specific fields
        claim.setDetentionTravelWaitingCosts(new ClaimFieldRow(DETENTION_TRAVEL_COST, BigDecimal.valueOf(50), BigDecimal.valueOf(55), null));
        claim.setJrFormFillingCost(new ClaimFieldRow(JR_FORM_FILLING, BigDecimal.valueOf(30), BigDecimal.valueOf(35), null));
        claim.setAdjournedHearing(new ClaimFieldRow(ADJOURNED_FEE, BigDecimal.valueOf(20), BigDecimal.valueOf(25), null));
        claim.setCmrhTelephone(new ClaimFieldRow(CMRH_TELEPHONE, BigDecimal.valueOf(15), BigDecimal.valueOf(18), null));
        claim.setCmrhOral(new ClaimFieldRow(CMRH_ORAL, BigDecimal.valueOf(40), BigDecimal.valueOf(45), null));
        claim.setHoInterview(new ClaimFieldRow(HO_INTERVIEW, BigDecimal.valueOf(60), BigDecimal.valueOf(65), null));
        claim.setSubstantiveHearing(new ClaimFieldRow(SUBSTANTIVE_HEARING, BigDecimal.valueOf(100), BigDecimal.valueOf(110), null));
        claim.setCounselsCost(new ClaimFieldRow(COUNSELS_COST, BigDecimal.valueOf(200), BigDecimal.valueOf(220), null));
        claim.setMatterTypeCodeOne("IMM");
        claim.setMatterTypeCodeTwo("DET");

        return claim;
    }
}
