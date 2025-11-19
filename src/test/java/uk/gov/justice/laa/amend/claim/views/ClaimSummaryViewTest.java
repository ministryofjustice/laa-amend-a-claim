package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ClaimSummaryController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.*;
import static uk.gov.justice.laa.amend.claim.utils.DateUtils.displayDateValue;

@ActiveProfiles("local")
@WebMvcTest(ClaimSummaryController.class)
@Import(LocalSecurityConfig.class)
class ClaimSummaryViewTest extends ViewTestBase {

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimMapper claimMapper;


    @MockitoBean
    private AssessmentService assessmentService;

    ClaimSummaryViewTest() {
        super("/submissions/submissionId/claims/claimId");
    }

    @Test
    void testCivilClaimPage() throws Exception {
        CivilClaimDetails claim = new CivilClaimDetails();
        createClaimSummary(claim);
        claim.setMatterTypeCode("IMLB:AHQS");
        claim.setDetentionTravelWaitingCosts(new ClaimField(DETENTION_TRAVEL_COST, 100, 90, 95));
        claim.setJrFormFillingCost(new ClaimField(JR_FORM_FILLING, 50, 45, 48));
        claim.setAdjournedHearing(new ClaimField(ADJOURNED_FEE, 200, 180, 190));
        claim.setCmrhTelephone(new ClaimField(CMRH_TELEPHONE, 75, 70, 72));
        claim.setCmrhOral(new ClaimField(CMRH_ORAL, 150, 140, 145));
        claim.setHoInterview(new ClaimField(HO_INTERVIEW, 120, 110, 115));
        claim.setSubstantiveHearing(new ClaimField(SUBSTANTIVE_HEARING, 300, 280, 290));
        claim.setCounselsCost(new ClaimField(COUNSELS_COST, 400, 380, 390));
        claim.setAreaOfLaw("CIVIL");
        claim.setCategoryOfLaw("TEST");


        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
        assertPageHasH2(doc, "Summary");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasSummaryList(doc);

        assertPageHasSummaryListRow(doc, "Escape case", "Yes");
        assertPageHasSummaryListRow(doc, "Area of law", "CIVIL");
        assertPageHasSummaryListRow(doc, "Category of law", "TEST");
        assertPageHasSummaryListRow(doc, "Fee scheme", "CCS");
        assertPageHasSummaryListRow(doc, "Matter type 1", "IMLB");
        assertPageHasSummaryListRow(doc, "Matter type 2", "AHQS");
        assertPageHasSummaryListRow(doc, "Provider Account Number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 Jan 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 Dec 2020");
        assertPageHasSummaryListRow(doc, "Date submitted", displayDateValue(claim.getSubmittedDate()));
        assertPageHasValuesRow(doc, "Total", claim.getTotalAmount());
        assertPageHasValuesRow(doc, "CMRH oral", claim.getCmrhOral());
        assertPageHasValuesRow(doc, "CMRH telephone", claim.getCmrhTelephone());
        assertPageHasValuesRow(doc, "Counsel's costs (ex VAT)", claim.getCounselsCost());
    }

    @Test
    void testCrimeClaimPage() throws Exception {
        CrimeClaimDetails claim = new CrimeClaimDetails();
        createClaimSummary(claim);
        claim.setMatterTypeCode("IMLB");
        claim.setTravelCosts(new ClaimField(TRAVEL_COSTS, 100, 90, null));
        claim.setWaitingCosts(new ClaimField(WAITING_COSTS, 50, 45, null));

        claim.setAreaOfLaw("CRIME");

        ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.feeCalculationResponse(new FeeCalculationPatch().categoryOfLaw("CRIME"));
        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
        assertPageHasH2(doc, "Summary");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasSummaryList(doc);

        assertPageHasSummaryListRow(doc, "Escape case", "Yes");
        assertPageHasSummaryListRow(doc, "Area of law", "CRIME");
        assertPageHasSummaryListRow(doc, "Fee scheme", "CCS");
        assertPageHasSummaryListRow(doc, "Matter type", "IMLB");
        assertPageHasSummaryListRow(doc, "Provider Account Number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 Jan 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 Dec 2020");
        assertPageHasSummaryListRow(doc, "Date submitted",displayDateValue(claim.getSubmittedDate()));
        assertPageHasValuesRow(doc, "Total", claim.getTotalAmount());
        assertPageHasValuesRow(doc, "Travel costs", claim.getTravelCosts());
        assertPageHasValuesRow(doc, "Waiting costs", claim.getWaitingCosts());
    }

    private static void createClaimSummary(ClaimDetails claim) {
        claim.setEscaped(true);
        claim.setAreaOfLaw("AAP");
        claim.setFeeScheme("CCS");

        claim.setProviderAccountNumber("0P322F");
        claim.setClientForename("John");
        claim.setClientSurname("Doe");
        claim.setCaseStartDate(LocalDate.of(2020, 1, 1));
        claim.setCaseEndDate(LocalDate.of(2020, 12, 31));
        claim.setSubmittedDate(LocalDate.now());

        // Set ClaimFieldRow fields
        claim.setVatClaimed(new ClaimField(VAT, 80, 75, 78));
        claim.setFixedFee(new ClaimField(FIXED_FEE, 500, 480, 490));
        claim.setNetProfitCost(new ClaimField(NET_PROFIT_COST, 600, 580, 590));
        claim.setNetDisbursementAmount(new ClaimField(NET_DISBURSEMENTS_COST, 200, 190, 195));
        claim.setTotalAmount(new ClaimField(TOTAL, 1380, 1325, 1350));
        claim.setDisbursementVatAmount(new ClaimField(DISBURSEMENT_VAT, 40, 38, 39));
        claim.setAreaOfLaw("CRIME");
    }

    @Test
    void testPageWhenEmptyClaim() throws Exception {
        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(new CrimeClaimDetails());

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
    }
}
