package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import uk.gov.justice.laa.amend.claim.models.GraphApiUser;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

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
        CivilClaimDetails claim = new CivilClaimDetails();
        createClaimSummary(claim);
        claim.setHasAssessment(true);
        claim.setMatterTypeCode("IMLB:AHQS");
        claim.setDetentionTravelWaitingCosts(new ClaimField(DETENTION_TRAVEL_COST, 100, 90, 95, 90));
        claim.setJrFormFillingCost(new ClaimField(JR_FORM_FILLING, 50, 45, 48, 45));
        claim.setAdjournedHearing(new ClaimField(ADJOURNED_FEE, 200, 180, 190, 180));
        claim.setCmrhTelephone(new ClaimField(CMRH_TELEPHONE, 75, 70, 72));
        claim.setCmrhOral(new ClaimField(CMRH_ORAL, 150, 140, 145, 140));
        claim.setHoInterview(new ClaimField(HO_INTERVIEW, 120, 110, 115, 120));
        claim.setSubstantiveHearing(new ClaimField(SUBSTANTIVE_HEARING, 300, 280, 290, 300));
        claim.setCounselsCost(new ClaimField(COUNSELS_COST, 400, 380, 390, 400));
        claim.setAreaOfLaw("LEGAL_HELP");
        claim.setCategoryOfLaw("TEST");

        OAuth2AuthorizedClient mockClient = mock(OAuth2AuthorizedClient.class);
        when(mockClient.getAccessToken()).thenReturn(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "mock-token", Instant.now(), Instant.now().plusSeconds(3600)));
        when(authorizedClientService.loadAuthorizedClient(eq("entra"), anyString()))
                .thenReturn(mockClient);


        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);
        var lastAssessment = new AssessmentInfo();
        lastAssessment.setLastAssessedBy("test");
        lastAssessment.setLastAssessmentDate(OffsetDateTime.now());
        claim.setLastAssessment(lastAssessment);
        when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);
        when(userRetrievalService.getGraphUser(any(), any())).thenReturn(new GraphApiUser("test","test"));

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
        assertPageHasH2(doc, "Summary");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasBackLink(doc);

        assertPageHasSummaryList(doc);

        assertPageHasInfoBanner(doc);
        assertPageHasSummaryListRow(doc, "Escape case", "Yes");
        assertPageHasSummaryListRow(doc, "Area of law", "LEGAL_HELP");
        assertPageHasSummaryListRow(doc, "Category of law", "TEST");
        assertPageHasSummaryListRow(doc, "Fee code", "FC");
        assertPageHasSummaryListRow(doc, "Fee code description", "FCD");
        assertPageHasSummaryListRow(doc, "Matter type 1", "IMLB");
        assertPageHasSummaryListRow(doc, "Matter type 2", "AHQS");
        assertPageHasSummaryListRow(doc, "Provider account number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 January 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 December 2020");
        assertPageHasSummaryListRow(doc, "Date submitted", "15 June 2020 at 09:30:00");
        assertPageHasValuesRow(doc, "Total", claim.getTotalAmount(), false);
        assertPageHasValuesRow(doc, "Oral CMRH", claim.getCmrhOral(), true);
        assertPageHasValuesRow(doc, "Telephone CMRH", claim.getCmrhTelephone(), true);
        assertPageHasValuesRow(doc, "Counsel costs", claim.getCounselsCost(), true);
    }

    @Test
    void testCrimeClaimPage() throws Exception {
        CrimeClaimDetails claim = new CrimeClaimDetails();
        createClaimSummary(claim);
        claim.setMatterTypeCode("IMLB");
        claim.setTravelCosts(new ClaimField(TRAVEL_COSTS, 100, 90));
        claim.setWaitingCosts(new ClaimField(WAITING_COSTS, 50, 45));

        claim.setAreaOfLaw("CRIME");

        ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.feeCalculationResponse(new FeeCalculationPatch().categoryOfLaw("CRIME"));
        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
        assertPageHasH2(doc, "Summary");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasBackLink(doc);

        assertPageHasSummaryList(doc);

        assertPageHasSummaryListRow(doc, "Escape case", "Yes");
        assertPageHasSummaryListRow(doc, "Area of law", "CRIME");
        assertPageHasSummaryListRow(doc, "Fee code", "FC");
        assertPageHasSummaryListRow(doc, "Fee code description", "FCD");
        assertPageHasSummaryListRow(doc, "Matter type", "IMLB");
        assertPageHasSummaryListRow(doc, "Provider account number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 January 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 December 2020");
        assertPageHasSummaryListRow(doc, "Date submitted", "15 June 2020 at 09:30:00");
        assertPageHasValuesRow(doc, "Total", claim.getTotalAmount(), false);
        assertPageHasValuesRow(doc, "Travel costs", claim.getTravelCosts(), false);
        assertPageHasValuesRow(doc, "Waiting costs", claim.getWaitingCosts(), false);
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
        claim.setVatClaimed(new ClaimField(VAT, 80, 75, 78, 75));
        claim.setFixedFee(new ClaimField(FIXED_FEE, 500, 480, 490, 500));
        claim.setNetProfitCost(new ClaimField(NET_PROFIT_COST, 600, 580, 590, 600));
        claim.setNetDisbursementAmount(new ClaimField(NET_DISBURSEMENTS_COST, 200, 190, 195, 200));
        claim.setTotalAmount(new ClaimField(TOTAL, 1380, 1325, 1350, 1325));
        claim.setDisbursementVatAmount(new ClaimField(DISBURSEMENT_VAT, 40, 38, 39, 40));
        claim.setAreaOfLaw("CRIME");
    }

    @Test
    void testPageWhenEmptyClaim() throws Exception {
        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(new CrimeClaimDetails());

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim details");

        assertPageHasHeading(doc, "Claim details");
    }


    @ParameterizedTest
    @MethodSource("claimFieldValuesProvider")
    void testHiddenFieldsAreDisplayed(String fieldLabel, CivilClaimDetails claimDetails, boolean display) throws Exception {
        when(claimService.getClaimDetails(anyString(), anyString())).thenReturn(claimDetails);
        Document doc = renderDocument();
        Assertions.assertEquals(pageHasLabel(doc, fieldLabel), display);
    }

    static Stream<Arguments> claimFieldValuesProvider() {
        return Stream.of(
            Arguments.of("Adjourned hearing fee", withField(ADJOURNED_FEE, "setAdjournedHearing", 0), false),
            Arguments.of("Adjourned hearing fee", withField(ADJOURNED_FEE, "setAdjournedHearing", 22), true),
            Arguments.of("Adjourned hearing fee", withField(ADJOURNED_FEE, "setAdjournedHearing", null), false),
            Arguments.of("Telephone CMRH", withField(CMRH_TELEPHONE, "setCmrhTelephone",  null), false),
            Arguments.of("Telephone CMRH", withField(CMRH_TELEPHONE, "setCmrhTelephone",  3), true),
            Arguments.of("Oral CMRH", withField(CMRH_ORAL, "setCmrhOral",  0), false),
            Arguments.of("Home office interview", withField(HO_INTERVIEW, "setHoInterview",  0), false),
            Arguments.of("Home office interview", withField(HO_INTERVIEW, "setHoInterview",  null), false),
            Arguments.of("Substantive hearing", withField(SUBSTANTIVE_HEARING, "setSubstantiveHearing", 0), false),
            Arguments.of("Substantive hearing", withField(SUBSTANTIVE_HEARING, "setSubstantiveHearing", 4), true),
            Arguments.of("JR and form filling", withField(JR_FORM_FILLING, "setJrFormFillingCost", BigDecimal.ZERO), false),
            Arguments.of("JR and form filling", withField(JR_FORM_FILLING, "setJrFormFillingCost", null), false),
            Arguments.of("JR and form filling", withField(JR_FORM_FILLING, "setJrFormFillingCost", new BigDecimal(20)), true)
        );
    }

    private static CivilClaimDetails withField(String label, String methodName, Object value) {
        CivilClaimDetails claimDetails = new CivilClaimDetails();
        claimDetails.setMatterTypeCode("IMLB:AHQS");
        claimDetails.setAreaOfLaw("CIVIL");
        claimDetails.setCategoryOfLaw("TEST");
        createClaimSummary(claimDetails);
        try {
            Method method = CivilClaimDetails.class.getMethod(methodName, ClaimField.class);
            method.invoke(claimDetails, new ClaimField(label, value, 10, 5));
            return claimDetails;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + methodName, e);
        }

    }
}
