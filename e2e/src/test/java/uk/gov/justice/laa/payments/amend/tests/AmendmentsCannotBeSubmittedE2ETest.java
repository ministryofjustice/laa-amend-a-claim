package uk.gov.justice.laa.payments.amend.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static uk.gov.justice.laa.payments.amend.utils.TestDataUtils.generateUfn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.payments.amend.base.BaseTest;
import uk.gov.justice.laa.payments.amend.models.BulkSubmissionInsert;
import uk.gov.justice.laa.payments.amend.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.payments.amend.models.ClaimCaseInsert;
import uk.gov.justice.laa.payments.amend.models.ClaimInsert;
import uk.gov.justice.laa.payments.amend.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.payments.amend.models.ClientInsert;
import uk.gov.justice.laa.payments.amend.models.Insert;
import uk.gov.justice.laa.payments.amend.models.SubmissionInsert;
import uk.gov.justice.laa.payments.amend.pages.ClaimDetailsPage;
import uk.gov.justice.laa.payments.amend.pages.SearchPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.AmendCaseDetailsPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.AmendClient1Page;
import uk.gov.justice.laa.payments.amend.pages.amendments.AmendmentRequestedByPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.AmendmentRequestedReasonPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.CannotBeSubmittedPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.CheckPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.ClaimModifiedPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.ViewCasePage;
import uk.gov.justice.laa.payments.amend.pages.amendments.ViewClientPage;

public class AmendmentsCannotBeSubmittedE2ETest extends BaseTest {

  private static final String PROVIDER_ACCOUNT = "0P322F";
  private static final String SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String CLAIM_ID = UUID.randomUUID().toString();
  private static final String CLAIM_UFN = generateUfn(-1);
  private static final String DUPLICATE_CLAIM_ID = UUID.randomUUID().toString();
  private static final String DUPLICATE_CLAIM_UFN = generateUfn();

  @Override
  protected List<Insert> inserts() {
    return new ArrayList<>() {
      {
        addAll(buildSubmissionObjects());
        addAll(buildLegalHelpClaimObjects(CLAIM_ID, CLAIM_UFN, 1));
        addAll(buildLegalHelpClaimObjects(DUPLICATE_CLAIM_ID, DUPLICATE_CLAIM_UFN, 2));
      }
    };
  }

  private @NonNull List<Insert> buildSubmissionObjects() {
    return List.of(
        BulkSubmissionInsert.builder().id(BULK_SUBMISSION_ID).userId(USER_ID).build(),
        SubmissionInsert.builder()
            .id(SUBMISSION_ID)
            .bulkSubmissionId(BULK_SUBMISSION_ID)
            .officeAccountNumber(PROVIDER_ACCOUNT)
            .submissionPeriod("MAR-2020")
            .areaOfLaw("LEGAL_HELP")
            .userId(USER_ID)
            .build());
  }

  private @NonNull List<Insert> buildLegalHelpClaimObjects(
      String claimId, String ufn, int lineNumber) {
    var claimSummaryFeeId = UUID.randomUUID().toString();
    return List.of(
        ClaimInsert.builder()
            .id(claimId)
            .submissionId(SUBMISSION_ID)
            .lineNumber(lineNumber)
            .uniqueFileNumber(ufn)
            .userId(USER_ID)
            .build(),
        ClaimSummaryFeeInsert.builder()
            .id(claimSummaryFeeId)
            .claimId(claimId)
            .adviceTime(60)
            .travelTime(10)
            .waitingTime(5)
            .netCounselCostsAmount(BigDecimal.valueOf(100))
            .userId(USER_ID)
            .build(),
        ClientInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimId(claimId)
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
            .claimId(claimId)
            .caseId("711")
            .uniqueCaseId("UCID123456")
            .outcomeCode("BB")
            .stageReachedCode("AB")
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimSummaryFeeId(claimSummaryFeeId)
            .claimId(claimId)
            .feeCode("IMCA")
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  @Test
  @DisplayName(
      """
          E2E: Legal Help Amendment Validation Error Flow – Search → View → Amend Claim Details
            → View Case → Change case details → View Case
            → Check Page → Submit amendments → Cannot Be Submitted page
            → Amend claim → View Client → Check Page → Submit amendments → Cannot Be Submitted page
            → Cancel amendments → View
          """)
  void duplicateClaimShowsValidationErrors() {
    startAmendment();

    new ViewClientPage(page).clickCaseTab();
    var viewCase = new ViewCasePage(page);
    viewCase.clickChangeCaseDetailsLink();

    var amendCaseDetails = new AmendCaseDetailsPage(page);
    amendCaseDetails.fillInput("UNIQUE_FILE_NUMBER", DUPLICATE_CLAIM_UFN);
    amendCaseDetails.clickContinueButton();

    new ViewCasePage(page).clickContinue();
    new CheckPage(page).clickSubmitButton();

    var cannotBeSubmitted = new CannotBeSubmittedPage(page);
    cannotBeSubmitted.assertErrorRow(
        "Submission", "A duplicate claim was found within the same submission");

    cannotBeSubmitted.clickAmendClaim();
    new ViewClientPage(page).clickContinue();
    new CheckPage(page).clickSubmitButton();

    var cannotBeSubmittedAgain = new CannotBeSubmittedPage(page);
    cannotBeSubmittedAgain.assertErrorRow(
        "Submission", "A duplicate claim was found within the same submission");

    cannotBeSubmittedAgain.clickCancelAmendments();
    new ClaimDetailsPage(page);
  }

  @Test
  @DisplayName(
      """
          E2E: Legal Help Claim Modified By Another User Flow – Search → View → Amend Claim Details
            → View Client → Change Client Details → View Client
            → Check Page → Claim amended by another user → Submit amendments → Claim Modified page
            → Search results → Search
          """)
  void modifiedClaimShowsClaimModifiedPage() {
    startAmendment();

    var viewClient = new ViewClientPage(page);
    viewClient.getChangeClientOneLink().click();

    var amendClient1 = new AmendClient1Page(page);
    amendClient1.fillInput("SURNAME", "changed");
    amendClient1.clickContinueButton();

    new ViewClientPage(page).clickContinue();
    var checkPage = new CheckPage(page);

    dqe.incrementVersion("claim", CLAIM_ID);
    checkPage.clickSubmitButton();

    var claimModified = new ClaimModifiedPage(page);
    claimModified.assertClaimModifiedMessage();

    claimModified.clickSearchResults();
    var search = new SearchPage(page);
    search.waitForResults();
    assertThat(search.getResultRows()).containsText(CLAIM_UFN);
  }

  private void startAmendment() {
    var search = new SearchPage(page);
    search.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", CLAIM_UFN, "", "", "");
    search.clickViewForUfn(CLAIM_UFN);

    new ClaimDetailsPage(page).clickAmendClaim();

    var requestedBy = new AmendmentRequestedByPage(page);
    requestedBy.getProviderRadio().click();
    requestedBy.getContinueButton().click();

    var requestedReason = new AmendmentRequestedReasonPage(page);
    requestedReason.getProviderErrorRadio().click();
    requestedReason.getContinueButton().click();
  }
}
