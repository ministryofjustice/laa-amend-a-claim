package uk.gov.justice.laa.amend.claim.views;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ClaimSummaryController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.COUNSELS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DETENTION_TRAVEL_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DISBURSEMENT_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_DISBURSEMENTS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TRAVEL_COSTS;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.WAITING_COSTS;

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

    @MockitoBean
    private UserRetrievalService userRetrievalService;


    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;


    ClaimSummaryViewTest() {
        super("/submissions/submissionId/claims/claimId");
    }

    @Test
    void testCivilClaimPage() throws Exception {
        CivilClaimDetails claim = getCivilClaimDetails();

        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasBackLink(doc);

        List<List<Element>> summaryList1 = getSummaryList(doc, "Summary");
        Assertions.assertEquals(16, summaryList1.size());
        assertSummaryListRowContainsValues(summaryList1.getFirst(), "Client name", "John Doe");
        assertSummaryListRowContainsValues(summaryList1.get(1), "Unique file number (UFN)", "Not applicable");
        assertSummaryListRowContainsValues(summaryList1.get(2), "Unique client number (UCN)", "Not applicable");
        assertSummaryListRowContainsValues(summaryList1.get(3), "Provider name", "Not applicable");
        assertSummaryListRowContainsValues(summaryList1.get(4), "Provider account number", "0P322F");
        assertSummaryListRowContainsValues(summaryList1.get(5), "Date submitted", "15 June 2020 at 09:30:00");
        assertSummaryListRowContainsValues(summaryList1.get(6), "Area of law", "LEGAL_HELP");
        assertSummaryListRowContainsValues(summaryList1.get(7), "Category of law", "TEST");
        assertSummaryListRowContainsValues(summaryList1.get(8), "Fee code", "FC");
        assertSummaryListRowContainsValues(summaryList1.get(9), "Fee code description", "FCD");
        assertSummaryListRowContainsValues(summaryList1.get(10), "Matter type 1", "IMLB");
        assertSummaryListRowContainsValues(summaryList1.get(11), "Matter type 2", "AHQS");
        assertSummaryListRowContainsValues(summaryList1.get(12), "Case start date", "01 January 2020");
        assertSummaryListRowContainsValues(summaryList1.get(13), "Case end date", "31 December 2020");
        assertSummaryListRowContainsValues(summaryList1.get(14), "Escape case", "Yes");
        assertSummaryListRowContainsValues(summaryList1.get(15), "VAT requested", "Not applicable");

        List<List<Element>> summaryList2 = getSummaryList(doc, "Values");
        Assertions.assertEquals(15, summaryList2.size());
        assertSummaryListRowContainsValues(summaryList2.get(1), "Fixed fee", "480", "500");
        assertSummaryListRowContainsValues(summaryList2.get(2), "Profit costs", "580", "600");
        assertSummaryListRowContainsValues(summaryList2.get(3), "Disbursements", "190", "200");
        assertSummaryListRowContainsValues(summaryList2.get(4), "Disbursement VAT", "38", "40");
        assertSummaryListRowContainsValues(summaryList2.get(5), "Detention travel and waiting costs", "90", "100");
        assertSummaryListRowContainsValues(summaryList2.get(6), "JR and form filling", "45", "50");
        assertSummaryListRowContainsValues(summaryList2.get(7), "Counsel costs", "380", "400");
        assertSummaryListRowContainsValues(summaryList2.get(8), "Oral CMRH", "140", "150");
        assertSummaryListRowContainsValues(summaryList2.get(9), "Telephone CMRH", "70", "75");
        assertSummaryListRowContainsValues(summaryList2.get(10), "Home office interview", "110", "120");
        assertSummaryListRowContainsValues(summaryList2.get(11), "Substantive hearing", "280", "300");
        assertSummaryListRowContainsValues(summaryList2.get(12), "Adjourned hearing fee", "180", "200");
        assertSummaryListRowContainsValues(summaryList2.get(13), "VAT", "75", "80");
        assertSummaryListRowContainsValues(summaryList2.get(14), "Total", "1325", "1380");
    }

    @Test
    void testAssessedClaimPage() throws Exception {
        CivilClaimDetails claim = getCivilClaimDetails();
        claim.setHasAssessment(true);

        OAuth2AuthorizedClient mockClient = mock(OAuth2AuthorizedClient.class);
        when(mockClient.getAccessToken()).thenReturn(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "mock-token", Instant.now(), Instant.now().plusSeconds(3600)));
        when(authorizedClientService.loadAuthorizedClient(eq("entra"), anyString()))
                .thenReturn(mockClient);

        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);
        var lastAssessment = new AssessmentInfo();
        lastAssessment.setLastAssessedBy("test");
        LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 18, 16, 11, 27);
        lastAssessment.setLastAssessmentDate(OffsetDateTime.of(localDateTime, ZoneOffset.UTC));
        lastAssessment.setLastAssessmentOutcome(OutcomeType.NILLED);
        claim.setLastAssessment(lastAssessment);
        when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);
        when(userRetrievalService.getMicrosoftApiUser(any(), any())).thenReturn(new MicrosoftApiUser("test","test"));

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");

        assertPageHasInformationAlert(
            doc,
            "This claim has been assessed",
            "Last edited by test on 18 December 2025 at 16:11:27 Nilled."
        );

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasBackLink(doc);

        assertPageHasSummaryCard(doc, "Summary");

        List<List<Element>> summaryList2 = getSummaryList(doc, "Values");
        Assertions.assertEquals(14, summaryList2.size());
        assertSummaryListRowContainsValues(summaryList2.get(1), "Fixed fee", "480", "500", "500");
        assertSummaryListRowContainsValues(summaryList2.get(2), "Profit costs", "580", "600", "600");
        assertSummaryListRowContainsValues(summaryList2.get(3), "Disbursements", "190", "200", "200");
        assertSummaryListRowContainsValues(summaryList2.get(4), "Disbursement VAT", "38", "40", "40");
        assertSummaryListRowContainsValues(summaryList2.get(5), "Detention travel and waiting costs", "90", "100", "90");
        assertSummaryListRowContainsValues(summaryList2.get(6), "JR and form filling", "45", "50", "45");
        assertSummaryListRowContainsValues(summaryList2.get(7), "Counsel costs", "380", "400", "400");
        assertSummaryListRowContainsValues(summaryList2.get(8), "Oral CMRH", "140", "150", "140");
        assertSummaryListRowContainsValues(summaryList2.get(9), "Telephone CMRH", "70", "75", "72");
        assertSummaryListRowContainsValues(summaryList2.get(10), "Home office interview", "110", "120", "120");
        assertSummaryListRowContainsValues(summaryList2.get(11), "Substantive hearing", "280", "300", "300");
        assertSummaryListRowContainsValues(summaryList2.get(12), "Adjourned hearing fee", "180", "200", "180");
        assertSummaryListRowContainsValues(summaryList2.get(13), "VAT", "75", "80", "75");
    }

    private static @NotNull CivilClaimDetails getCivilClaimDetails() {
        CivilClaimDetails claim = new CivilClaimDetails();
        createClaimSummary(claim);
        claim.setMatterTypeCode("IMLB:AHQS");
        claim.setDetentionTravelWaitingCosts(new ClaimField(DETENTION_TRAVEL_COST, 100, 90, 95, 90, ClaimFieldType.OTHER));
        claim.setJrFormFillingCost(new ClaimField(JR_FORM_FILLING, 50, 45, 48, 45, ClaimFieldType.OTHER));
        claim.setAdjournedHearing(new ClaimField(ADJOURNED_FEE, 200, 180, 190, 180, ClaimFieldType.BOLT_ON));
        claim.setCmrhTelephone(new ClaimField(CMRH_TELEPHONE, 75, 70, 72, ClaimFieldType.BOLT_ON));
        claim.setCmrhOral(new ClaimField(CMRH_ORAL, 150, 140, 145, 140, ClaimFieldType.BOLT_ON));
        claim.setHoInterview(new ClaimField(HO_INTERVIEW, 120, 110, 115, 120, ClaimFieldType.BOLT_ON));
        claim.setSubstantiveHearing(new ClaimField(SUBSTANTIVE_HEARING, 300, 280, 290, 300, ClaimFieldType.BOLT_ON));
        claim.setCounselsCost(new ClaimField(COUNSELS_COST, 400, 380, 390, 400, ClaimFieldType.OTHER));
        claim.setAreaOfLaw("LEGAL_HELP");
        claim.setCategoryOfLaw("TEST");
        return claim;
    }

    @Test
    void testCrimeClaimPage() throws Exception {
        CrimeClaimDetails claim = new CrimeClaimDetails();
        createClaimSummary(claim);
        claim.setMatterTypeCode("IMLB");
        claim.setTravelCosts(new ClaimField(TRAVEL_COSTS, 100, 90, ClaimFieldType.OTHER));
        claim.setWaitingCosts(new ClaimField(WAITING_COSTS, 50, 45,  ClaimFieldType.OTHER));

        claim.setAreaOfLaw("CRIME");
        claim.setSchemeId("SCHEME");
        claim.setPoliceStationCourtPrisonId("POLICE_STATION_COURT_PRISON");

        ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.feeCalculationResponse(new FeeCalculationPatch().categoryOfLaw("CRIME"));
        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasBackLink(doc);

        List<List<Element>> summaryList1 = getSummaryList(doc, "Summary");
        Assertions.assertEquals(16, summaryList1.size());
        assertSummaryListRowContainsValues(summaryList1.getFirst(), "Client name", "John Doe");
        assertSummaryListRowContainsValues(summaryList1.get(1), "Unique file number (UFN)", "Not applicable");
        assertSummaryListRowContainsValues(summaryList1.get(2), "Provider name", "Not applicable");
        assertSummaryListRowContainsValues(summaryList1.get(3), "Provider account number", "0P322F");
        assertSummaryListRowContainsValues(summaryList1.get(4), "Date submitted", "15 June 2020 at 09:30:00");
        assertSummaryListRowContainsValues(summaryList1.get(5), "Area of law", "CRIME");
        assertSummaryListRowContainsValues(summaryList1.get(6), "Category of law", "Not applicable");
        assertSummaryListRowContainsValues(summaryList1.get(7), "Fee code", "FC");
        assertSummaryListRowContainsValues(summaryList1.get(8), "Fee code description", "FCD");
        assertSummaryListRowContainsValues(summaryList1.get(9), "Police Station / Court / Prison ID", "POLICE_STATION_COURT_PRISON");
        assertSummaryListRowContainsValues(summaryList1.get(10), "Scheme ID", "SCHEME");
        assertSummaryListRowContainsValues(summaryList1.get(11), "Matter type", "IMLB");
        assertSummaryListRowContainsValues(summaryList1.get(12), "Case start date", "01 January 2020");
        assertSummaryListRowContainsValues(summaryList1.get(13), "Case end date", "31 December 2020");
        assertSummaryListRowContainsValues(summaryList1.get(14), "Escape case", "Yes");
        assertSummaryListRowContainsValues(summaryList1.get(15), "VAT requested", "Not applicable");

        List<List<Element>> summaryList2 = getSummaryList(doc, "Values");
        Assertions.assertEquals(9, summaryList2.size());
        assertSummaryListRowContainsValues(summaryList2.get(1), "Fixed fee", "480", "500");
        assertSummaryListRowContainsValues(summaryList2.get(2), "Profit costs", "580", "600");
        assertSummaryListRowContainsValues(summaryList2.get(3), "Disbursements", "190", "200");
        assertSummaryListRowContainsValues(summaryList2.get(4), "Disbursement VAT", "38", "40");
        assertSummaryListRowContainsValues(summaryList2.get(5), "Travel costs", "90", "100");
        assertSummaryListRowContainsValues(summaryList2.get(6), "Waiting costs", "45", "50");
        assertSummaryListRowContainsValues(summaryList2.get(7), "VAT", "75", "80");
        assertSummaryListRowContainsValues(summaryList2.get(8), "Total", "1325", "1380");
    }

    @Test
    void testNonEscapedClaimPage() throws Exception {
        claim.setEscaped(false);

        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasPrimaryButtonDisabled(doc, "Add assessment outcome");
    }

    private static void createClaimSummary(ClaimDetails claim) {
        claim.setEscaped(true);
        claim.setAreaOfLaw("AAP");
        claim.setFeeCode("FC");
        claim.setFeeCodeDescription("FCD");

        claim.setProviderAccountNumber("0P322F");
        claim.setClientForename("John");
        claim.setClientSurname("Doe");
        claim.setCaseStartDate(LocalDate.of(2020, 1, 1));
        claim.setCaseEndDate(LocalDate.of(2020, 12, 31));
        claim.setSubmittedDate(LocalDateTime.of(2020, 6, 15, 9, 30, 0));

        // Set ClaimFieldRow fields
        claim.setVatClaimed(new ClaimField(VAT, 80, 75, 78, 75,  ClaimFieldType.OTHER));
        claim.setFixedFee(new ClaimField(FIXED_FEE, 500, 480, 490, 500,  ClaimFieldType.FIXED_FEE));
        claim.setNetProfitCost(new ClaimField(NET_PROFIT_COST, 600, 580, 590, 600,  ClaimFieldType.OTHER));
        claim.setNetDisbursementAmount(new ClaimField(NET_DISBURSEMENTS_COST, 200, 190, 195, 200,  ClaimFieldType.OTHER));
        claim.setTotalAmount(new ClaimField(TOTAL, 1380, 1325, 1350, 1325,  ClaimFieldType.CALCULATED_TOTAL));
        claim.setDisbursementVatAmount(new ClaimField(DISBURSEMENT_VAT, 40, 38, 39, 40,  ClaimFieldType.OTHER));
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
