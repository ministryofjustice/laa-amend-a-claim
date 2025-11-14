package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ClaimSummaryController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.Claim2;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaim;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.*;

@ActiveProfiles("local")
@WebMvcTest(ClaimSummaryController.class)
@Import(LocalSecurityConfig.class)
class ClaimSummaryViewTest extends ViewTestBase {

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimMapper claimMapper;

    ClaimSummaryViewTest() {
        super("/submissions/submissionId/claims/claimId");
    }

    @Test
    void testCivilClaimPage() throws Exception {
        CivilClaim claim = new CivilClaim();
        createClaimSummary(claim);
        claim.setMatterTypeCodeOne("IMLB");
        claim.setMatterTypeCodeTwo("AHQS");
        claim.setDetentionTravelWaitingCosts(new ClaimField(DETENTION_TRAVEL_COST, 100, 90, 95));
        claim.setJrFormFillingCost(new ClaimField(JR_FORM_FILLING, 50, 45, 48));
        claim.setAdjournedHearing(new ClaimField(ADJOURNED_FEE, 200, 180, 190));
        claim.setCmrhTelephone(new ClaimField(CMRH_TELEPHONE, 75, 70, 72));
        claim.setCmrhOral(new ClaimField(CMRH_ORAL, 150, 140, 145));
        claim.setHoInterview(new ClaimField(HO_INTERVIEW, 120, 110, 115));
        claim.setSubstantiveHearing(new ClaimField(SUBSTANTIVE_HEARING, 300, 280, 290));
        claim.setCounselsCost(new ClaimField(COUNSELS_COST, 400, 380, 390));


        when(claimService.getClaim(anyString(), anyString())).thenReturn(new ClaimResponse());
        when(claimMapper.mapToCivilClaim(any())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
        assertPageHasH2(doc, "Summary");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasSummaryList(doc);

        assertPageHasSummaryListRow(doc, "Escape case", "Yes");
        assertPageHasSummaryListRow(doc, "Type", "AAP");
        assertPageHasSummaryListRow(doc, "Fee scheme", "CCS");
        assertPageHasSummaryListRow(doc, "Matter type 1", "IMLB");
        assertPageHasSummaryListRow(doc, "Matter type 2", "AHQS");
        assertPageHasSummaryListRow(doc, "Provider Account Number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 Jan 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 Dec 2020");
        assertPageHasSummaryListRow(doc, "Date submitted", claim.getSubmittedDate());
        assertPageHasValuesRow(doc, "Total", claim.getTotalAmount());
        assertPageHasValuesRow(doc, "CMRH oral", claim.getCmrhOral());
        assertPageHasValuesRow(doc, "CMRH telephone", claim.getCmrhTelephone());
        assertPageHasValuesRow(doc, "Counsel's Cost(ex VAT)", claim.getCounselsCost());
    }

    @Test
    void testCrimeClaimPage() throws Exception {
        CrimeClaim claim = new CrimeClaim();
        createClaimSummary(claim);
        claim.setMatterTypeCode("IMLB");
        claim.setTravelCosts(new ClaimField(TRAVEL_COSTS, 100, 90, null));
        claim.setWaitingCosts(new ClaimField(WAITING_COSTS, 50, 45, null));

        ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.feeCalculationResponse(new FeeCalculationPatch().categoryOfLaw("CRIME"));
        when(claimService.getClaim(anyString(), anyString())).thenReturn(claimResponse);
        when(claimMapper.mapToCrimeClaim(any())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
        assertPageHasH2(doc, "Summary");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasSummaryList(doc);

        assertPageHasSummaryListRow(doc, "Escape case", "Yes");
        assertPageHasSummaryListRow(doc, "Type", "AAP");
        assertPageHasSummaryListRow(doc, "Fee scheme", "CCS");
        assertPageHasSummaryListRow(doc, "Legal matter code", "IMLB");
        assertPageHasSummaryListRow(doc, "Provider Account Number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 Jan 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 Dec 2020");
        assertPageHasSummaryListRow(doc, "Date submitted", claim.getSubmittedDate());
        assertPageHasValuesRow(doc, "Total", claim.getTotalAmount());
        assertPageHasValuesRow(doc, "Travel costs", claim.getTravelCosts());
        assertPageHasValuesRow(doc, "Waiting costs", claim.getWaitingCosts());
    }

    private static void createClaimSummary(Claim2 claim) {
        claim.setEscaped(true);
        claim.setCategoryOfLaw("AAP");
        claim.setFeeScheme("CCS");

        claim.setProviderAccountNumber("0P322F");
        claim.setClientForename("John");
        claim.setClientSurname("Doe");
        claim.setCaseStartDate(LocalDate.of(2020, 1, 1));
        claim.setCaseEndDate(LocalDate.of(2020, 12, 31));
        claim.setSubmittedDate("22/10/2025");

        // Set ClaimField fields
        claim.setVatClaimed(new ClaimField(VAT, 80, 75, 78));
        claim.setFixedFee(new ClaimField(FIXED_FEE, 500, 480, 490));
        claim.setNetProfitCost(new ClaimField(NET_PROFIT_COST, 600, 580, 590));
        claim.setNetDisbursementAmount(new ClaimField(NET_DISBURSEMENTS_COST, 200, 190, 195));
        claim.setTotalAmount(new ClaimField(TOTAL, 1380, 1325, 1350));
        claim.setDisbursementVatAmount(new ClaimField(DISBURSEMENT_VAT, 40, 38, 39));
    }

    @Test
    void testPageWhenNullClaim() throws Exception {
        when(claimService.getClaim(anyString(), anyString())).thenReturn(new ClaimResponse());
        when(claimMapper.mapToCivilClaim(any())).thenReturn(null);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasNoSummaryList(doc);
    }

    @Test
    void testPageWhenEmptyClaim() throws Exception {
        when(claimService.getClaim(anyString(), anyString())).thenReturn(new ClaimResponse());
        when(claimMapper.mapToCrimeClaim(any())).thenReturn(new CrimeClaim());

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
    }
}
