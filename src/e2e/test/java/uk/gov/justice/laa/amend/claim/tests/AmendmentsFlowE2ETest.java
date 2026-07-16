package uk.gov.justice.laa.amend.claim.tests;

import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.assertSummaryListRow;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendCaseDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendClient1Page;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendFeeCodePage;
import uk.gov.justice.laa.amend.claim.pages.amendments.AmendMatterTypePage;
import uk.gov.justice.laa.amend.claim.pages.amendments.ViewCasePage;
import uk.gov.justice.laa.amend.claim.pages.amendments.ViewClientPage;

public class AmendmentsFlowE2ETest extends BaseTest {

  private static final String PROVIDER_ACCOUNT = "123456";
  private static final String LEGAL_HELP_UFN = generateUfn();
  private static final String MEDIATION_UFN = generateUfn(1L);
  private static final String LEGAL_HELP_BULK_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String LEGAL_HELP_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String LEGAL_HELP_CLAIM_ID = UUID.randomUUID().toString();
  private static final String LEGAL_HELP_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private static final String LEGAL_HELP_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_BULK_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_CLAIM_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private static final String MEDIATION_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();
  private static final String STAGE_REACHED = "INVA";

  @Override
  protected List<Insert> inserts() {
    return new ArrayList<>() {
      {
        addAll(buildLegalHelpObjects());
        addAll(buildMediationObjects());
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
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(LEGAL_HELP_CALCULATED_FEE_DETAIL_ID)
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
            .userId(USER_ID)
            .build(),
        ClaimSummaryFeeInsert.builder()
            .id(MEDIATION_CLAIM_SUMMARY_FEE_ID)
            .claimId(MEDIATION_CLAIM_ID)
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(MEDIATION_CALCULATED_FEE_DETAIL_ID)
            .claimSummaryFeeId(MEDIATION_CLAIM_SUMMARY_FEE_ID)
            .claimId(MEDIATION_CLAIM_ID)
            .feeCode("MDAS2S")
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
            → View Case → (TODO) Change case details → View Case
          """)
  void fullLegalHelpAmendmentFlow() {
    var search = new SearchPage(page);

    search.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", LEGAL_HELP_UFN, "", "", "");

    search.clickViewForUfn(LEGAL_HELP_UFN);

    // View Client → Change Client Details → View Client
    var details = new ClaimDetailsPage(page);
    details.clickAmendClaim();

    var viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client details", "Last name", "Not applicable");
    viewAmendClient.clickChangeLink();

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
    assertSummaryListRow(page, "Case details", "Stage reached", "Not applicable");

    viewAmendCase.clickChangeCaseTypeLink();
    var amendFeeCode = new AmendFeeCodePage(page);
    amendFeeCode.fillFeeCodeInput("IAXC");
    amendFeeCode.clickContinueButton();

    var amendMatterType = new AmendMatterTypePage(page);
    amendMatterType.fillMatterTypeCodeOne("NEW_MONE");
    amendMatterType.fillMatterTypeCodeTwo("NEW_MTWO");
    amendMatterType.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case type", "Fee code", "IMCA", "IAXC");
    assertSummaryListRow(page, "Case type", "Matter type 1", "IMCB", "NEW_MONE");
    assertSummaryListRow(page, "Case type", "Matter type 2", "IRVL", "NEW_MTWO");

    viewAmendCase.clickChangeCaseDetailsLink();
    var viewAmendCaseDetails = new AmendCaseDetailsPage(page);
    viewAmendCaseDetails.fillTypeaheadInput("STAGE_REACHED", STAGE_REACHED);
    viewAmendCaseDetails.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case details", "Stage reached", "Not applicable", STAGE_REACHED);
  }

  @Test
  @DisplayName(
      """
          E2E: Mediation Claim Amendment Flow – Search → View → View Client → Amend Claim Details
            → View Client → Change Client Details → View Client
            → View Case → Change case type → Change Fee code → Change Matter Type → View Case Type
            → View Case → (TODO) Change case details → View Case
          """)
  void fullMediationAmendmentFlow() {
    var search = new SearchPage(page);

    search.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", MEDIATION_UFN, "", "", "");

    search.clickViewForUfn(MEDIATION_UFN);

    // View Client → Change Client Details → View Client
    var details = new ClaimDetailsPage(page);
    details.clickAmendClaim();

    var viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client 1 details", "Last name", "Not applicable");
    viewAmendClient.clickChangeLink();

    var amendClient1 = new AmendClient1Page(page);
    amendClient1.fillInput("SURNAME", "changed");
    amendClient1.clickContinueButton();

    viewAmendClient = new ViewClientPage(page);
    assertSummaryListRow(page, "Client 1 details", "Last name", "Not applicable", "changed");
    viewAmendClient.clickCaseTab();

    // View Case → Change case type → View Case
    var viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case type", "Fee code", "MDAS2S");
    assertSummaryListRow(page, "Case type", "Matter type", "IMCB");
    assertSummaryListRow(page, "Case details", "Case start date", "01 August 2020");

    viewAmendCase.clickChangeCaseTypeLink();
    var amendFeeCode = new AmendFeeCodePage(page);
    amendFeeCode.fillFeeCodeInput("MDPS1B");
    amendFeeCode.clickContinueButton();

    var amendMatterType = new AmendMatterTypePage(page);
    amendMatterType.fillMatterTypeCode("NEW_MONE");
    amendMatterType.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case type", "Fee code", "MDAS2S", "MDPS1B");
    assertSummaryListRow(page, "Case type", "Matter type", "IMCB", "NEW_MONE");

    viewAmendCase.clickChangeCaseDetailsLink();
    var viewAmendCaseDetails = new AmendCaseDetailsPage(page);
    viewAmendCaseDetails.fillInput("CLAIM_ID", "changed");
    viewAmendCaseDetails.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case details", "Claim ID", "Not applicable", "changed");
  }
}
