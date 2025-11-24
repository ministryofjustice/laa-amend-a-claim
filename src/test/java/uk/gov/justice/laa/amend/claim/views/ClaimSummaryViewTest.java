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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
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
        claim.setJrFormFillingCost(createClaimField(JR_FORM_FILLING));
        claim.setAdjournedHearing(createClaimField(ADJOURNED_FEE));
        claim.setCmrhTelephone(createClaimField(CMRH_TELEPHONE));
        claim.setCmrhOral(createClaimField(CMRH_ORAL));
        claim.setHoInterview(createClaimField(HO_INTERVIEW));
        claim.setSubstantiveHearing(createClaimField(SUBSTANTIVE_HEARING));
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
            Arguments.of("CMRH telephone", withField(CMRH_TELEPHONE, "setCmrhTelephone",  null), false),
            Arguments.of("CMRH telephone", withField(CMRH_TELEPHONE, "setCmrhTelephone",  3), true),
            Arguments.of("CMRH oral", withField(CMRH_ORAL, "setCmrhOral",  0), false),
            Arguments.of("Home office interview", withField(HO_INTERVIEW, "setHoInterview",  0), false),
            Arguments.of("Home office interview", withField(HO_INTERVIEW, "setHoInterview",  null), false),
            Arguments.of("Substantive hearing", withField(SUBSTANTIVE_HEARING, "setSubstantiveHearing", 0), false),
            Arguments.of("Substantive hearing", withField(SUBSTANTIVE_HEARING, "setSubstantiveHearing", 4), true),
            Arguments.of("JR/Form filling", withField(JR_FORM_FILLING, "setJrFormFillingCost", BigDecimal.ZERO), false),
            Arguments.of("JR/Form filling", withField(JR_FORM_FILLING, "setJrFormFillingCost", null), false),
            Arguments.of("JR/Form filling", withField(JR_FORM_FILLING, "setJrFormFillingCost", new BigDecimal(20)), true)
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
            method.invoke(claimDetails, new ClaimField(label, ClaimFieldValue.of(value), ClaimFieldValue.of(10), ClaimFieldValue.of(5)));
            return claimDetails;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + methodName, e);
        }

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
