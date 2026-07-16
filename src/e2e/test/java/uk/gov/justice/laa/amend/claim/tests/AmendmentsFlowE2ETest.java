package uk.gov.justice.laa.amend.claim.tests;

import static uk.gov.justice.laa.amend.claim.helpers.PageHelper.assertSummaryListRow;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

import java.util.List;
import java.util.UUID;
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
  private static final String UFN = generateUfn();
  private static final String SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String CLAIM_ID = UUID.randomUUID().toString();
  private static final String CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private static final String CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

  @Override
  protected List<Insert> inserts() {
    return List.of(
        BulkSubmissionInsert.builder().id(BULK_SUBMISSION_ID).userId(USER_ID).build(),
        SubmissionInsert.builder()
            .id(SUBMISSION_ID)
            .bulkSubmissionId(BULK_SUBMISSION_ID)
            .officeAccountNumber(PROVIDER_ACCOUNT)
            .submissionPeriod("MAR-2020")
            .areaOfLaw("LEGAL_HELP")
            .userId(USER_ID)
            .build(),
        ClaimInsert.builder()
            .id(CLAIM_ID)
            .submissionId(SUBMISSION_ID)
            .uniqueFileNumber(UFN)
            .userId(USER_ID)
            .build(),
        ClaimSummaryFeeInsert.builder()
            .id(CLAIM_SUMMARY_FEE_ID)
            .claimId(CLAIM_ID)
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(CALCULATED_FEE_DETAIL_ID)
            .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID)
            .claimId(CLAIM_ID)
            .feeCode("IMCA")
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  @Test
  @DisplayName(
      """
          E2E: Claim Amendment Flow – Search → View → View Client → Amend Claim Details
            → View Client → Change Client Details → View Client
            → View Case → Change case type → Change Fee code → Change Matter Type → View Case Type
            → View Case → (TODO) Change case details → View Case
          """)
  void fullAmendmentFlow() {
    var search = new SearchPage(page);

    search.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", UFN, "", "", "");

    search.clickViewForUfn(UFN);

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
    viewAmendCaseDetails.fillInput("STAGE_REACHED", "changed");
    viewAmendCaseDetails.clickContinueButton();

    viewAmendCase = new ViewCasePage(page);
    assertSummaryListRow(page, "Case details", "Stage reached", "Not applicable", "changed");
  }
}
