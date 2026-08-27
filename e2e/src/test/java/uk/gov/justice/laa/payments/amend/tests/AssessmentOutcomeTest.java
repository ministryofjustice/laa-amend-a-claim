package uk.gov.justice.laa.payments.amend.tests;

import static uk.gov.justice.laa.payments.amend.utils.TestDataUtils.generateUfn;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.payments.amend.base.BaseTest;
import uk.gov.justice.laa.payments.amend.models.BulkSubmissionInsert;
import uk.gov.justice.laa.payments.amend.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.payments.amend.models.ClaimInsert;
import uk.gov.justice.laa.payments.amend.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.payments.amend.models.Insert;
import uk.gov.justice.laa.payments.amend.models.SubmissionInsert;
import uk.gov.justice.laa.payments.amend.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.payments.amend.pages.ClaimDetailsPage;
import uk.gov.justice.laa.payments.amend.pages.SearchPage;

public class AssessmentOutcomeTest extends BaseTest {

  private final String OFFICE_CODE = "123456";
  private final String UFN = generateUfn();
  private final String SUBMISSION_ID = UUID.randomUUID().toString();
  private final String CLAIM_ID = UUID.randomUUID().toString();
  private final String CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private final String CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

  @Override
  protected List<Insert> inserts() {
    return List.of(
        BulkSubmissionInsert.builder().id(BULK_SUBMISSION_ID).userId(USER_ID).build(),
        SubmissionInsert.builder()
            .id(SUBMISSION_ID)
            .bulkSubmissionId(BULK_SUBMISSION_ID)
            .officeAccountNumber(OFFICE_CODE)
            .submissionPeriod("APR-2025")
            .areaOfLaw("CRIME_LOWER")
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
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  private void navigateToAssessmentOutcome() {
    SearchPage search = new SearchPage(page);

    search.searchForClaim(OFFICE_CODE, "04", "2025", UFN, "", "", "");

    search.clickViewForUfn(UFN);

    ClaimDetailsPage details = new ClaimDetailsPage(page);
    details.clickAddUpdateAssessmentOutcome();
  }

  @Test
  @DisplayName("Assessment outcome page loads: shows 4 outcome radios + VAT question + Continue")
  void assessmentOutcomePageLoadsSuccessfully() {
    navigateToAssessmentOutcome();

    AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
    outcome.assertPageLoaded();

    assertUrlEndsWith("/assessment-outcome");
  }

  @Test
  @DisplayName(
      "Assessment outcome validation: Continue without selecting outcome shows GOV.UK error summary")
  void continueWithoutSelectingOutcomeShowsError() {
    navigateToAssessmentOutcome();

    AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);

    outcome.saveChanges();

    assertUrlEndsWith("/assessment-outcome");
    outcome.assertAssessmentOutcomeRequiredError();
  }
}
