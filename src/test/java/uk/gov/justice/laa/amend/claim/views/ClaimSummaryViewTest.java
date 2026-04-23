package uk.gov.justice.laa.amend.claim.views;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.DISPLAY_NAME;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.ClaimSummaryController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.AssessmentTypeEnum;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.MicrosoftUserRetrievalService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

@WebMvcTest(ClaimSummaryController.class)
class ClaimSummaryViewTest extends ViewTestBase {

  @MockitoBean private ClaimService claimService;

  @MockitoBean private ClaimMapper claimMapper;

  @MockitoBean private AssessmentService assessmentService;

  @MockitoBean private MicrosoftUserRetrievalService userRetrievalService;

  @MockitoBean private OAuth2AuthorizedClientService authorizedClientService;

  ClaimSummaryViewTest() {
    this.mapping = String.format("/submissions/%s/claims/%s", submissionId, claimId);
  }

  @BeforeEach
  void setUp() {
    when(featureFlagsConfig.getIsClaimHistoryEnabled()).thenReturn(true);
  }

  @Test
  void testCivilClaimPage() {
    CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
    createClaimSummary(claim);
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    claim.setCategoryOfLaw("TEST");
    claim.setMatterTypeCode("IMLB:AHQS");

    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

    Document doc = renderDocument();
    assertCommonPageContent(doc);

    List<List<Element>> summaryList1 = getSummaryListInCard(doc, "Summary");
    Assertions.assertEquals(16, summaryList1.size());
    assertSummaryListRowContainsValues(summaryList1.getFirst(), "Client name", "John Doe");
    assertSummaryListRowContainsValues(
        summaryList1.get(1), "Unique file number (UFN)", "Not applicable");
    assertSummaryListRowContainsValues(
        summaryList1.get(2), "Unique client number (UCN)", "Not applicable");
    assertSummaryListRowContainsValues(
        summaryList1.get(3), "Provider name", "Currently not available");
    assertSummaryListRowContainsValues(summaryList1.get(4), "Office code", "0P322F");
    assertSummaryListRowContainsValues(
        summaryList1.get(5), "Date submitted", "15 June 2020 at 10:30:00");
    assertSummaryListRowContainsValues(summaryList1.get(6), "Area of law", "Legal help");
    assertSummaryListRowContainsValues(summaryList1.get(7), "Category of law", "TEST");
    assertSummaryListRowContainsValues(summaryList1.get(8), "Fee code", "FC");
    assertSummaryListRowContainsValues(summaryList1.get(9), "Fee code description", "FCD");
    assertSummaryListRowContainsValues(summaryList1.get(10), "Matter type 1", "IMLB");
    assertSummaryListRowContainsValues(summaryList1.get(11), "Matter type 2", "AHQS");
    assertSummaryListRowContainsValues(summaryList1.get(12), "Case start date", "01 January 2020");
    assertSummaryListRowContainsValues(summaryList1.get(13), "Case end date", "31 December 2020");
    assertSummaryListRowContainsValues(summaryList1.get(14), "Escape case", "Yes");
    assertSummaryListRowContainsValues(summaryList1.get(15), "VAT requested", "Not applicable");

    List<List<Element>> summaryList2 = getSummaryListInCard(doc, "Values");
    Assertions.assertEquals(15, summaryList2.size());
    assertSummaryListRowContainsValues(
        summaryList2.get(1), "Fixed fee", "£200.00", "Not applicable");
    assertSummaryListRowContainsValues(
        summaryList2.get(2), "Profit costs", "Not applicable", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(3), "Disbursements", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(4), "Disbursement VAT", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(5), "Detention travel and waiting costs", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(6), "JR and form filling", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(7), "Counsel costs", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(8), "Oral CMRH", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(9), "Telephone CMRH", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(10), "Home office interview", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(11), "Substantive hearing", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(12), "Adjourned hearing fee", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(13), "VAT", "No", "Yes");
    assertSummaryListRowContainsValues(summaryList2.get(14), "Total", "£200.00", "Not applicable");
  }

  @Test
  void testAssessedClaimPage() {
    CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setHasAssessment(true);

    OAuth2AuthorizedClient mockClient = mock(OAuth2AuthorizedClient.class);
    when(mockClient.getAccessToken())
        .thenReturn(
            new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "mock-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)));

    when(authorizedClientService.loadAuthorizedClient(eq("entra"), any())).thenReturn(mockClient);

    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

    var lastAssessment =
        AssessmentInfo.builder()
            .lastAssessedBy("test")
            .lastAssessmentDate(
                OffsetDateTime.of(LocalDateTime.of(2025, 12, 18, 16, 11, 27), ZoneOffset.UTC))
            .lastAssessmentOutcome(OutcomeType.NILLED)
            .build();
    claim.setLastAssessment(lastAssessment);
    claim.setLastUpdatedDateTime(lastAssessment.lastAssessmentDate());
    claim.setLastUpdatedUser(lastAssessment.lastAssessedBy());

    when(assessmentService.getLatestAssessmentByClaim(claim)).thenReturn(claim);

    when(userRetrievalService.getUser(any()))
        .thenReturn(new MicrosoftApiUser("test-id", "Bloggs, Joe", "Joe", "Bloggs"));

    Document doc = renderDocument();
    assertCommonPageContent(doc);

    assertPageHasInformationAlert(
        doc,
        "This claim has been assessed",
        "Last edited by Joe Bloggs on 18 December 2025 at 16:11:27 Nilled.");

    assertPageHasSummaryCard(doc, "Summary");

    List<List<Element>> summaryList2 = getSummaryListInCard(doc, "Values");
    Assertions.assertEquals(14, summaryList2.size());
    assertSummaryListRowContainsValues(
        summaryList2.get(1), "Fixed fee", "£200.00", "Not applicable", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(2), "Profit costs", "Not applicable", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(3), "Disbursements", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(4), "Disbursement VAT", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(5), "Detention travel and waiting costs", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(6), "JR and form filling", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(7), "Counsel costs", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(8), "Oral CMRH", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(9), "Telephone CMRH", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(10), "Home office interview", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(11), "Substantive hearing", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(12), "Adjourned hearing fee", "£200.00", "£100.00", "£300.00");
    assertSummaryListRowContainsValues(summaryList2.get(13), "VAT", "No", "Yes", "Yes");
  }

  @Test
  void testCrimeClaimPage() {
    CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
    createClaimSummary(claim);
    claim.setMatterTypeCode("IMLB");
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    claim.setSchemeId("SCHEME");
    claim.setPoliceStationCourtPrisonId("POLICE_STATION_COURT_PRISON");

    ClaimResponse claimResponse = new ClaimResponse();
    claimResponse.feeCalculationResponse(new FeeCalculationPatch().categoryOfLaw("CRIME"));
    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

    Document doc = renderDocument();
    assertCommonPageContent(doc);

    List<List<Element>> summaryList1 = getSummaryListInCard(doc, "Summary");
    Assertions.assertEquals(16, summaryList1.size());
    assertSummaryListRowContainsValues(summaryList1.getFirst(), "Client name", "John Doe");
    assertSummaryListRowContainsValues(
        summaryList1.get(1), "Unique file number (UFN)", "Not applicable");
    assertSummaryListRowContainsValues(
        summaryList1.get(2), "Provider name", "Currently not available");
    assertSummaryListRowContainsValues(summaryList1.get(3), "Office code", "0P322F");
    assertSummaryListRowContainsValues(
        summaryList1.get(4), "Date submitted", "15 June 2020 at 10:30:00");
    assertSummaryListRowContainsValues(summaryList1.get(5), "Area of law", "Crime lower");
    assertSummaryListRowContainsValues(summaryList1.get(6), "Category of law", "Not applicable");
    assertSummaryListRowContainsValues(summaryList1.get(7), "Fee code", "FC");
    assertSummaryListRowContainsValues(summaryList1.get(8), "Fee code description", "FCD");
    assertSummaryListRowContainsValues(
        summaryList1.get(9), "Police Station / Court / Prison ID", "POLICE_STATION_COURT_PRISON");
    assertSummaryListRowContainsValues(summaryList1.get(10), "Scheme ID", "SCHEME");
    assertSummaryListRowContainsValues(summaryList1.get(11), "Matter type", "IMLB");
    assertSummaryListRowContainsValues(summaryList1.get(12), "Case start date", "01 January 2020");
    assertSummaryListRowContainsValues(summaryList1.get(13), "Case end date", "31 December 2020");
    assertSummaryListRowContainsValues(summaryList1.get(14), "Escape case", "Yes");
    assertSummaryListRowContainsValues(summaryList1.get(15), "VAT requested", "Not applicable");

    List<List<Element>> summaryList2 = getSummaryListInCard(doc, "Values");
    Assertions.assertEquals(9, summaryList2.size());
    assertSummaryListRowContainsValues(
        summaryList2.get(1), "Fixed fee", "£200.00", "Not applicable");
    assertSummaryListRowContainsValues(
        summaryList2.get(2), "Profit costs", "Not applicable", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(3), "Disbursements", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(
        summaryList2.get(4), "Disbursement VAT", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(5), "Travel costs", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(6), "Waiting costs", "£200.00", "£100.00");
    assertSummaryListRowContainsValues(summaryList2.get(7), "VAT", "No", "Yes");
    assertSummaryListRowContainsValues(summaryList2.get(8), "Total", "£200.00", "Not applicable");
  }

  @Test
  void testNonEscapedClaimPage() {
    claim.setEscaped(false);

    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

    Document doc = renderDocument();

    assertPageHasPrimaryButtonHidden(doc, "Add assessment outcome");

    assertPageHasLink(doc, "back-to-search", "Back to search", "/");
  }

  @Test
  void testPageWithCachedSearchUrl() {
    session.setAttribute("searchUrl", "/?officeCode=0P322F&page=1");

    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

    Document doc = renderDocument();

    assertPageHasLink(doc, "back-to-search", "Back to search", "/?officeCode=0P322F&page=1");
  }

  @ParameterizedTest
  @MethodSource("claimTypes")
  void testVoidClaimPageWithEscapeAssessmentShowsVoidBanner(ClaimDetails claim) {
    createClaimSummary(claim);

    claim.setStatus(ClaimStatus.VOID);
    var lastAssessment = MockClaimsFunctions.createAssessment(AssessmentTypeEnum.VOID);
    claim.setLastAssessment(lastAssessment);
    claim.setLastUpdatedDateTime(lastAssessment.lastAssessmentDate());
    claim.setLastUpdatedUser(lastAssessment.lastAssessedBy());

    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

    Document doc = renderDocument();

    assertPageHasTitle(doc, "Claim details");
    assertPageHasHeading(doc, "Claim details");

    Element banner = doc.selectFirst(".moj-alert.moj-alert--error");
    assertNotNull(banner, "Expected VOID banner to be visible");

    assertTrue(
        banner
            .text()
            .matches(
                ".*This claim has been voided.*Last edited by "
                    + DISPLAY_NAME
                    + ".*You can no longer make changes\\..*"),
        "VOID banner text is not in the expected order");

    Element assessmentButton = doc.selectFirst("[data-testid=claim-details-assessment-button]");
    assertNull(assessmentButton, "Expected assessment button to be hidden for VOID claims");

    Element voidButton = getButtonByLabel(doc, "Void claim");
    assertNull(voidButton, "Expected Void button to be hidden for VOID claims");
  }

  @ParameterizedTest
  @MethodSource("claimTypes")
  void testVoidClaimPageWithNoPreviousAssessmentShowsVoidBanner(ClaimDetails claim) {
    createClaimSummary(claim);

    claim.setStatus(ClaimStatus.VOID);
    claim.setLastAssessment(null);
    var lastAssessment = MockClaimsFunctions.createAssessment(AssessmentTypeEnum.VOID);
    claim.setLastAssessment(lastAssessment);
    claim.setLastUpdatedDateTime(lastAssessment.lastAssessmentDate());
    claim.setLastUpdatedUser(lastAssessment.lastAssessedBy());

    when(claimService.getClaimDetails(any(), any())).thenReturn(claim);

    Document doc = renderDocument();

    Element banner = doc.selectFirst(".moj-alert.moj-alert--error");
    assertNotNull(banner, "Expected VOID banner to be visible even without previous assessment");
    assertTrue(
        banner
            .text()
            .matches(
                ".*This claim has been voided.*Last edited by "
                    + DISPLAY_NAME
                    + ".*You can no longer make changes\\..*"),
        "VOID banner text is not in the expected order");
    Element assessmentButton = doc.selectFirst("[data-testid=claim-details-assessment-button]");
    assertNull(assessmentButton, "Expected assessment button to be hidden for VOID claims");

    Element voidButton = getButtonByLabel(doc, "Void claim");
    assertNull(voidButton, "Expected Void button to be hidden for VOID claims");
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Claim details");

    assertPageHasHeading(doc, "Claim details");

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasActiveSubNavigationItem(doc, "Overview", mapping);
    assertPageHasInactiveSubNavigationItem(doc, "Claim history", mapping + "/history");

    assertH2Exists(doc, "Overview");

    assertPageDoesNotHaveBackLink(doc);
  }

  private static Stream<ClaimDetails> claimTypes() {
    return Stream.of(
        MockClaimsFunctions.createMockCivilClaim(), MockClaimsFunctions.createMockCrimeClaim());
  }

  private static void createClaimSummary(ClaimDetails claim) {
    claim.setEscaped(true);
    claim.setFeeCode("FC");
    claim.setFeeCodeDescription("FCD");
    claim.setOfficeCode("0P322F");
    claim.setClientForename("John");
    claim.setClientSurname("Doe");
    claim.setCaseStartDate(LocalDate.of(2020, 1, 1));
    claim.setCaseEndDate(LocalDate.of(2020, 12, 31));
    claim.setSubmittedDate(OffsetDateTime.of(2020, 6, 15, 9, 30, 0, 0, ZoneOffset.UTC));
  }
}
