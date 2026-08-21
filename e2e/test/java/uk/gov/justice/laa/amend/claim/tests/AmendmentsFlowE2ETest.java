package uk.gov.justice.laa.amend.claim.tests;

import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.assertSummaryListRow;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimCaseInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.ClientInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendCaseDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendClient1Page;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendClient2Page;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendCostsPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendFeeCodePage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendMatterTypePage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendStageReachedPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendmentRequestedByPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendmentRequestedReasonPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.CheckPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.ConfirmationPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.ViewCasePage;
import uk.gov.justice.laa.amend.claim.pages.amendments.ViewClientPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.ViewCostsPage;

public class AmendmentsFlowE2ETest extends BaseTest {

  private static final String PROVIDER_ACCOUNT = "0P322F";
  private static final String LEGAL_HELP_UFN = generateUfn();
  private static final String MEDIATION_UFN = "121019/002";
  private static final String LEGAL_HELP_BULK_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String LEGAL_HELP_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String LEGAL_HELP_CLAIM_ID = UUID.randomUUID().toString();
  private static final String LEGAL_HELP_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_BULK_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_CLAIM_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private static final String CRIME_UFN = "121019/003";
  private static final String CRIME_BULK_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String CRIME_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String CRIME_CLAIM_ID = UUID.randomUUID().toString();
  private static final String CRIME_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private static final String LEGAL_HELP_STAGE_REACHED = "AB";
  private static final String CRIME_STAGE_REACHED = "INVC";
  private static final String CRIME_STAGE_REACHED_LABEL = "INVC - Police station: attendance";
  private static final String AMENDED_CRIME_STAGE_REACHED = "PROD";
  private static final String AMENDED_CRIME_STAGE_REACHED_LABEL =
      "PROD - Advice and Assistance and Advocacy Assistance by a court Duty Solicitor";

  @Override
  protected List<Insert> inserts() {
    return new ArrayList<>() {
      {
        addAll(buildLegalHelpObjects());
        addAll(buildMediationObjects());
        addAll(buildCrimeObjects());
      }
    };
  }

  private @NonNull List<Insert> buildLegalHelpObjects() {
    return List.of(
        BulkSubmissionInsert.builder().id(LEGAL_HELP_BULK_SUBMISSION_ID).userId(USER_ID).build(),
        SubmissionInsert.builder()
            .id(LEGAL_HELP_SUBMISSION_ID)
            .bulkSubmissionId(LEGAL_HELP_BULK_SUBMISSION_ID)
            .officeAccountNumber(PROVIDER_ACCOUNT)
            .submissionPeriod("MAR-2020")
            .areaOfLaw("LEGAL_HELP")
            .userId(USER_ID)
            .build(),
        ClaimInsert.builder()
            .id(LEGAL_HELP_CLAIM_ID)
            .submissionId(LEGAL_HELP_SUBMISSION_ID)
            .uniqueFileNumber(LEGAL_HELP_UFN)
            .userId(USER_ID)
            .build(),
        ClaimSummaryFeeInsert.builder()
            .id(LEGAL_HELP_CLAIM_SUMMARY_FEE_ID)
            .claimId(LEGAL_HELP_CLAIM_ID)
            .adviceTime(60)
            .travelTime(10)
            .waitingTime(5)
            .netCounselCostsAmount(BigDecimal.valueOf(100))
            .userId(USER_ID)
            .build(),
        ClientInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimId(LEGAL_HELP_CLAIM_ID)
            .clientForename("Francesca")
            .clientDateOfBirth("1996-08-07")
            .uniqueClientNumber("07081996/S/MCLE")
            .clientPostcode("SE61LG")
            .genderCode("F")
            .ethnicityCode("03")
            .disabilityCode("NCD")
            .userId(USER_ID)
            .build(),
        ClaimCaseInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimId(LEGAL_HELP_CLAIM_ID)
            .caseId("711")
            .uniqueCaseId("UCID123456")
            .outcomeCode("BB")
            .stageReachedCode("--")
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimSummaryFeeId(LEGAL_HELP_CLAIM_SUMMARY_FEE_ID)
            .claimId(LEGAL_HELP_CLAIM_ID)
            .feeCode("IMCA")
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  private @NonNull List<Insert> buildMediationObjects() {
    return List.of(
        BulkSubmissionInsert.builder().id(MEDIATION_BULK_SUBMISSION_ID).userId(USER_ID).build(),
        SubmissionInsert.builder()
            .id(MEDIATION_SUBMISSION_ID)
            .bulkSubmissionId(MEDIATION_BULK_SUBMISSION_ID)
            .officeAccountNumber(PROVIDER_ACCOUNT)
            .submissionPeriod("MAR-2020")
            .areaOfLaw("MEDIATION")
            .userId(USER_ID)
            .build(),
        ClaimInsert.builder()
            .id(MEDIATION_CLAIM_ID)
            .submissionId(MEDIATION_SUBMISSION_ID)
            .uniqueFileNumber(MEDIATION_UFN)
            .outreachLocation("001")
            .referralSource("08")
            .userId(USER_ID)
            .build(),
        ClientInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimId(MEDIATION_CLAIM_ID)
            .clientForename("Francesca")
            .clientSurname("Elonga")
            .clientDateOfBirth("1996-08-07")
            .uniqueClientNumber("07081996/S/MCLE")
            .clientPostcode("SE61LG")
            .genderCode("F")
            .ethnicityCode("03")
            .disabilityCode("NCD")
            .isLegallyAided(true)
            .userId(USER_ID)
            .build(),
        ClaimCaseInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimId(MEDIATION_CLAIM_ID)
            .uniqueCaseId("UCID123456")
            .caseId("711")
            .userId(USER_ID)
            .build(),
        ClaimSummaryFeeInsert.builder()
            .id(MEDIATION_CLAIM_SUMMARY_FEE_ID)
            .claimId(MEDIATION_CLAIM_ID)
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimSummaryFeeId(MEDIATION_CLAIM_SUMMARY_FEE_ID)
            .claimId(MEDIATION_CLAIM_ID)
            .feeCode("MDAS2S")
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  private @NonNull List<Insert> buildCrimeObjects() {
    return List.of(
        BulkSubmissionInsert.builder().id(CRIME_BULK_SUBMISSION_ID).userId(USER_ID).build(),
        SubmissionInsert.builder()
            .id(CRIME_SUBMISSION_ID)
            .bulkSubmissionId(CRIME_BULK_SUBMISSION_ID)
            .officeAccountNumber(PROVIDER_ACCOUNT)
            .submissionPeriod("MAR-2020")
            .areaOfLaw("CRIME_LOWER")
            .userId(USER_ID)
            .build(),
        ClaimInsert.builder()
            .id(CRIME_CLAIM_ID)
            .submissionId(CRIME_SUBMISSION_ID)
            .uniqueFileNumber(CRIME_UFN)
            .matterType("01")
            .crimeMatterType("01")
            .feeCode("INVC")
            .userId(USER_ID)
            .build(),
        ClaimCaseInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimId(CRIME_CLAIM_ID)
            .stageReachedCode(CRIME_STAGE_REACHED)
            .userId(USER_ID)
            .build(),
        ClaimSummaryFeeInsert.builder()
            .id(CRIME_CLAIM_SUMMARY_FEE_ID)
            .claimId(CRIME_CLAIM_ID)
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimSummaryFeeId(CRIME_CLAIM_SUMMARY_FEE_ID)
            .claimId(CRIME_CLAIM_ID)
            .feeCode("INVC")
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  @Test
  @DisplayName(
      """
          E2E: Legal Help Claim Amendment Flow – Search → View → View Client → Amend Claim Details
            → View Client → Change Client Details → View Client
            → View Case → Change case type → Change Fee code → Change Matter Type → View Case Type
            → View Case → Change case details → View Case
            → Check Page → Submit amendments → Confirmation page
          """)
  void fullLegalHelpAmendmentFlow() {
    var search = new SearchPage(page);

    search.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", LEGAL_HELP_UFN, "", "", "");

    search.clickViewForUfn(LEGAL_HELP_UFN);

    // View Client → Change Client Details → View Client
    var details = new ClaimDetailsPage(page);
    details.clickAmendClaim();

    var amendmentRequestedByPage = new AmendmentRequestedByPage(page);
    amendmentRequestedByPage.getProviderRadio().click();
    amendmentRequestedByPage.getContinueButton().click();

    var amendmentRequestReason = new AmendmentRequestedReasonPage(page);
    amendmentRequestReason.getProviderErrorRadio().click();
    amendmentRequestReason.getContinueButton().click();

    var viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client details", "Last name", "Not applicable");
    viewAmendClient.getChangeClientOneLink().click();

    var amendClient1 = new AmendClient1Page(page);
    amendClient1.fillInput("SURNAME", "changed");
    amendClient1.clickContinueButton();

    viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client details", "Last name", "Not applicable", "changed");
    viewAmendClient.clickCaseTab();

    // View Case → Change case type → View Case
    var viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case type", "Fee code", "IMCA");
    assertSummaryListRow(page, "Case type", "Matter type 1", "IMCB");
    assertSummaryListRow(page, "Case type", "Matter type 2", "IRVL");
    assertSummaryListRow(page, "Case details", "Stage reached", "--");

    viewAmendCase.clickChangeCaseTypeLink();
    var amendFeeCode = new AmendFeeCodePage(page);
    amendFeeCode.fillFeeCodeInput("IAXC");
    amendFeeCode.clickContinueButton();

    var amendMatterType = new AmendMatterTypePage(page);
    amendMatterType.fillMatterTypeCodeOne("MONE");
    amendMatterType.fillMatterTypeCodeTwo("MTWO");
    amendMatterType.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case type", "Fee code", "IMCA", "IAXC");
    assertSummaryListRow(page, "Case type", "Matter type 1", "IMCB", "MONE");
    assertSummaryListRow(page, "Case type", "Matter type 2", "IRVL", "MTWO");

    viewAmendCase.clickChangeCaseDetailsLink();
    var viewAmendCaseDetails = new AmendCaseDetailsPage(page);
    viewAmendCaseDetails.fillInput("STAGE_REACHED", LEGAL_HELP_STAGE_REACHED);
    viewAmendCaseDetails.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case details", "Stage reached", "--", LEGAL_HELP_STAGE_REACHED);

    // View Case → Costs tab → Change costs → View Costs
    viewAmendCase.clickCostsTab();
    var viewAmendCosts = new ViewCostsPage(page);
    viewAmendCosts.clickChangeCostsLink();
    var amendCosts = new AmendCostsPage(page);
    amendCosts.fillCostInput("DISBURSEMENTS", "999.99");
    amendCosts.clickContinueButton();

    viewAmendCosts = new ViewCostsPage(page);
    viewAmendCosts.assertAmendedCost("Net disbursements", "£999.99");

    viewAmendCosts.clickContinue();

    var checkPage = new CheckPage(page);
    assertSummaryListRow(page, "Client details", "Last name", "Not applicable", "changed");
    assertSummaryListRow(page, "Case type", "Fee code", "IMCA", "IAXC");
    assertSummaryListRow(page, "Case type", "Matter type 1", "IMCB", "MONE");
    assertSummaryListRow(page, "Case type", "Matter type 2", "IRVL", "MTWO");
    assertSummaryListRow(page, "Reported costs", "Net disbursements", "£400.00", "£999.99");

    checkPage.clickSubmitButton();
    new ConfirmationPage(page);
  }

  @Test
  @DisplayName(
      """
          E2E: Mediation Claim Amendment Flow – Search → View → View Client → Amend Claim Details
            → View Client → Change Client Details → View Client
            → View Case → Change case type → Change Fee code → Change Matter Type → View Case Type
            → View Case → Change case details → View Case
            → Check Page → Submit amendments → Confirmation page
          """)
  void fullMediationAmendmentFlow() {
    var search = new SearchPage(page);

    search.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", MEDIATION_UFN, "", "", "");

    search.clickViewForUfn(MEDIATION_UFN);

    // View Client → Change Client Details → View Client
    var details = new ClaimDetailsPage(page);
    details.clickAmendClaim();

    var amendmentRequestedBy = new AmendmentRequestedByPage(page);
    amendmentRequestedBy.getProviderRadio().click();
    amendmentRequestedBy.getContinueButton().click();

    var amendmentRequestedReason = new AmendmentRequestedReasonPage(page);
    amendmentRequestedReason.getProviderErrorRadio().click();
    amendmentRequestedReason.getContinueButton().click();

    var viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client 1 details", "Last name", "Elonga");
    viewAmendClient.getChangeClientOneLink().click();

    var amendClient1 = new AmendClient1Page(page);
    amendClient1.fillInput("SURNAME", "changed");
    amendClient1.clickContinueButton();

    viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client 1 details", "Last name", "Elonga", "changed");
    viewAmendClient.getChangeClientTwoLink().click();

    var amendClient2 = new AmendClient2Page(page);
    amendClient2.fillInput("CLIENT_2_SURNAME", "changedTwo");
    amendClient2.clickContinueButton();

    viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client 2 details", "Last name", "Not applicable", "changedTwo");
    viewAmendClient.clickCaseTab();

    // View Case → Change case type → View Case
    var viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case type", "Fee code", "MDAS2S");
    assertSummaryListRow(page, "Case type", "Matter type 1", "IMCB");
    assertSummaryListRow(page, "Case type", "Matter type 2", "IRVL");
    assertSummaryListRow(page, "Case details", "Case start date", "01 August 2020");

    viewAmendCase.clickChangeCaseTypeLink();
    var amendFeeCode = new AmendFeeCodePage(page);
    amendFeeCode.fillFeeCodeInput("MDPS1B");
    amendFeeCode.clickContinueButton();

    var amendMatterType = new AmendMatterTypePage(page);
    amendMatterType.fillMatterTypeCodeOne("MONE");
    amendMatterType.fillMatterTypeCodeTwo("MTWO");
    amendMatterType.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case type", "Fee code", "MDAS2S", "MDPS1B");
    assertSummaryListRow(page, "Case type", "Matter type 1", "IMCB", "MONE");
    assertSummaryListRow(page, "Case type", "Matter type 2", "IRVL", "MTWO");

    viewAmendCase.clickChangeCaseDetailsLink();
    var viewAmendCaseDetails = new AmendCaseDetailsPage(page);
    viewAmendCaseDetails.fillInput("CLAIM_ID", "123");
    viewAmendCaseDetails.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case details", "Claim ID", "711", "123");

    // View Case → Costs tab → Change costs → View Costs
    viewAmendCase.clickCostsTab();
    var viewAmendCosts = new ViewCostsPage(page);
    viewAmendCosts.clickChangeCostsLink();
    var amendCosts = new AmendCostsPage(page);
    amendCosts.fillCostInput("DISBURSEMENTS", "200.00");
    amendCosts.clickContinueButton();

    viewAmendCosts = new ViewCostsPage(page);
    viewAmendCosts.assertAmendedCost("Net disbursements", "£200.00");

    viewAmendCosts.clickContinue();

    var checkPage = new CheckPage(page);
    assertSummaryListRow(page, "Client 1 details", "Last name", "Elonga", "changed");
    assertSummaryListRow(page, "Client 2 details", "Last name", "Not applicable", "changedTwo");
    assertSummaryListRow(page, "Case details", "Claim ID", "711", "123");
    assertSummaryListRow(page, "Case type", "Fee code", "MDAS2S", "MDPS1B");
    assertSummaryListRow(page, "Case type", "Matter type 1", "IMCB", "MONE");
    assertSummaryListRow(page, "Case type", "Matter type 2", "IRVL", "MTWO");
    assertSummaryListRow(page, "Reported costs", "Net disbursements", "£400.00", "£200.00");

    checkPage.clickSubmitButton();
    new ConfirmationPage(page);
  }

  @Test
  @DisplayName(
      """
          E2E: Crime Claim Amendment Flow – Search → View → Amend Claim Details
            → Costs tab → Change costs → View Costs
            → View Client → Change Client Details → View Client
            → View Case → Change case type → Change Fee code → Change Matter Type → View Case Type
            → View Case → Change case details → View Case
            → Check Page → Submit amendments → Confirmation page
          """)
  void fullCrimeAmendmentFlow() {
    // TODO:  When case start date bug is fixed on DSTEW side we need to remove cas estart date from
    // the test data here
    var search = new SearchPage(page);

    search.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", CRIME_UFN, "", "", "");

    search.clickViewForUfn(CRIME_UFN);

    var details = new ClaimDetailsPage(page);
    details.clickAmendClaim();

    // View Costs → Change Client Details → View Client
    var amendmentRequestedBy = new AmendmentRequestedByPage(page);
    amendmentRequestedBy.getProviderRadio().click();
    amendmentRequestedBy.getContinueButton().click();

    var amendmentRequestedReason = new AmendmentRequestedReasonPage(page);
    amendmentRequestedReason.getProviderErrorRadio().click();
    amendmentRequestedReason.getContinueButton().click();

    var viewAmendClient = new ViewClientPage(page);
    viewAmendClient.clickCostsTab();

    var viewAmendCosts = new ViewCostsPage(page);
    viewAmendCosts.clickChangeCostsLink();

    var amendCosts = new AmendCostsPage(page);
    amendCosts.fillCostInput("DISBURSEMENTS", "150.00");
    amendCosts.clickContinueButton();

    viewAmendCosts = new ViewCostsPage(page);
    viewAmendCosts.assertAmendedCost("Net disbursements", "£150.00");

    // View Client → Change Client Details → View Client

    viewAmendClient = new ViewClientPage(page);
    viewAmendClient.clickClientTab();

    assertSummaryListRow(page, "Client details", "Last name", "Not applicable");
    viewAmendClient.getChangeClientOneLink().click();

    var amendClient1 = new AmendClient1Page(page);
    amendClient1.fillInput("SURNAME", "changed");
    amendClient1.clickContinueButton();

    viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client details", "Last name", "Not applicable", "changed");

    // View Case → Change case type → View Case

    var viewAmendCase = new ViewCasePage(page);
    viewAmendClient.clickCaseTab();
    assertSummaryListRow(page, "Case type", "Fee code", "INVC");
    assertSummaryListRow(page, "Case type", "Stage reached", CRIME_STAGE_REACHED_LABEL);

    viewAmendCase.clickChangeCaseTypeLink();
    var amendFeeCode = new AmendFeeCodePage(page);
    amendFeeCode.clickContinueButton();

    var amendStageReached = new AmendStageReachedPage(page);
    amendStageReached.fillStageReachedInput(AMENDED_CRIME_STAGE_REACHED);
    amendStageReached.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(
        page,
        "Case type",
        "Stage reached",
        CRIME_STAGE_REACHED_LABEL,
        AMENDED_CRIME_STAGE_REACHED_LABEL);

    viewAmendCase.clickChangeCaseDetailsLink();
    var viewAmendCaseDetails = new AmendCaseDetailsPage(page);
    viewAmendCaseDetails.fillDateInput("CASE_CONCLUDED_DATE", "31", "January", "2020");
    viewAmendCaseDetails.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(
        page, "Case details", "Case concluded date", "30 January 2020", "31 January 2020");

    viewAmendCase.clickContinue();

    var checkPage = new CheckPage(page);
    assertSummaryListRow(page, "Client details", "Last name", "Not applicable", "changed");
    assertSummaryListRow(page, "Reported costs", "Net disbursements", "£400.00", "£150.00");
    assertSummaryListRow(
        page,
        "Case type",
        "Stage reached",
        CRIME_STAGE_REACHED_LABEL,
        AMENDED_CRIME_STAGE_REACHED_LABEL);
    assertSummaryListRow(
        page, "Case details", "Case concluded date", "30 January 2020", "31 January 2020");

    checkPage.clickSubmitButton();
    new ConfirmationPage(page);
  }
}
