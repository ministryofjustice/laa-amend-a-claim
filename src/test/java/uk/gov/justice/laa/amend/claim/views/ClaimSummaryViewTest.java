package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
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

        assertPageHasBackLink(doc);

        assertPageHasSummaryList(doc);

        assertPageHasSummaryListRow(doc, "Escape case", "Yes");
        assertPageHasSummaryListRow(doc, "Area of law", "CIVIL");
        assertPageHasSummaryListRow(doc, "Category of law", "TEST");
        assertPageHasSummaryListRow(doc, "Fee scheme", "CCS");
        assertPageHasSummaryListRow(doc, "Matter type 1", "IMLB");
        assertPageHasSummaryListRow(doc, "Matter type 2", "AHQS");
        assertPageHasSummaryListRow(doc, "Provider account number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 January 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 December 2020");
        assertPageHasSummaryListRow(doc, "Date submitted", "15 June 2020");
        assertPageHasValuesRow(doc, "Total", claim.getTotalAmount());
        assertPageHasValuesRow(doc, "Oral CMRH", claim.getCmrhOral());
        assertPageHasValuesRow(doc, "Telephone CMRH", claim.getCmrhTelephone());
        assertPageHasValuesRow(doc, "Counsel costs", claim.getCounselsCost());
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
        assertPageHasSummaryListRow(doc, "Fee scheme", "CCS");
        assertPageHasSummaryListRow(doc, "Matter type", "IMLB");
        assertPageHasSummaryListRow(doc, "Provider account number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 January 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 December 2020");
        assertPageHasSummaryListRow(doc, "Date submitted", "15 June 2020");
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
        claim.setSubmittedDate(LocalDate.of(2020, 6, 15));

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
