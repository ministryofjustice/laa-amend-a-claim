package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants;
import uk.gov.justice.laa.amend.claim.controllers.ClaimSummaryController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldValue;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
        claim.setDetentionTravelWaitingCosts(createClaimField(AmendClaimConstants.Label.DETENTION_TRAVEL_COST));
        claim.setJrFormFillingCost(createClaimField(AmendClaimConstants.Label.JR_FORM_FILLING));
        claim.setAdjournedHearing(createClaimField(AmendClaimConstants.Label.ADJOURNED_FEE));
        claim.setCmrhTelephone(createClaimField(AmendClaimConstants.Label.CMRH_TELEPHONE));
        claim.setCmrhOral(createClaimField(AmendClaimConstants.Label.CMRH_ORAL));
        claim.setHoInterview(createClaimField(AmendClaimConstants.Label.HO_INTERVIEW));
        claim.setSubstantiveHearing(createClaimField(AmendClaimConstants.Label.SUBSTANTIVE_HEARING));
        claim.setCounselsCost(createClaimField(AmendClaimConstants.Label.COUNSELS_COST));
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
        claim.setTravelCosts(createClaimField(AmendClaimConstants.Label.TRAVEL_COSTS));
        claim.setWaitingCosts(createClaimField(AmendClaimConstants.Label.WAITING_COSTS));

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
        claim.setVatClaimed(createClaimField(AmendClaimConstants.Label.VAT));
        claim.setFixedFee(createClaimField(AmendClaimConstants.Label.FIXED_FEE));
        claim.setNetProfitCost(createClaimField(AmendClaimConstants.Label.NET_PROFIT_COST));
        claim.setNetDisbursementAmount(createClaimField(AmendClaimConstants.Label.NET_DISBURSEMENTS_COST));
        claim.setTotalAmount(createClaimField(AmendClaimConstants.Label.TOTAL));
        claim.setDisbursementVatAmount(createClaimField(AmendClaimConstants.Label.DISBURSEMENT_VAT));
        claim.setAreaOfLaw("CRIME");
    }

    @Test
    void testPageWhenEmptyClaim() throws Exception {
        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(new CrimeClaimDetails());

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
    }

    private static ClaimField createClaimField(String label) {
        return new ClaimField(
            label,
            ClaimFieldValue.of(100),
            ClaimFieldValue.of(200),
            ClaimFieldValue.of(300)
        );
    }
}
