package uk.gov.justice.laa.payments.amend.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
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
import uk.gov.justice.laa.payments.amend.pages.AssessAllowedTotalsPage;
import uk.gov.justice.laa.payments.amend.pages.AssessAssessedTotalsPage;
import uk.gov.justice.laa.payments.amend.pages.AssessDisbursementsPage;
import uk.gov.justice.laa.payments.amend.pages.AssessDisbursementsVatPage;
import uk.gov.justice.laa.payments.amend.pages.AssessProfitCostsPage;
import uk.gov.justice.laa.payments.amend.pages.AssessTravelCostsPage;
import uk.gov.justice.laa.payments.amend.pages.AssessWaitingCostsPage;
import uk.gov.justice.laa.payments.amend.pages.AssessmentCompletePage;
import uk.gov.justice.laa.payments.amend.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.payments.amend.pages.ClaimDetailsPage;
import uk.gov.justice.laa.payments.amend.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.payments.amend.pages.SearchPage;

public class AssessmentFlowE2ETest extends BaseTest {

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
            .submissionPeriod("MAR-2020")
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
            .feeCode("INVC")
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  @Test
  @DisplayName("E2E: Full Crime Assessment Flow – Search → View → Outcome → Amend All → Submit")
  void fullAssessmentFlow() {
    SearchPage search = new SearchPage(page);

    search.searchForClaim(OFFICE_CODE, "03", "2020", UFN, "", "", "");

    search.clickViewForUfn(UFN);

    ClaimDetailsPage details = new ClaimDetailsPage(page);
    details.clickAddUpdateAssessmentOutcome();

    AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
    outcome.selectAssessmentOutcome("assessed in full");
    outcome.selectContingencyAssessment(true);
    outcome.saveChanges();

    ReviewAndAmendPage review = new ReviewAndAmendPage(page);

    // -------- Claim costs --------

    review.clickChangeProfitCosts();
    AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
    profit.setAssessedValue("999.99");
    profit.saveChanges();

    review.clickChangeDisbursements();
    AssessDisbursementsPage disb = new AssessDisbursementsPage(page);
    disb.setAssessedValue("111.11");
    disb.saveChanges();

    review.clickChangeDisbursementsVat();
    AssessDisbursementsVatPage disbVat = new AssessDisbursementsVatPage(page);
    disbVat.setAssessedValue("22.22");
    disbVat.saveChanges();

    review.clickChangeTravelCosts();
    AssessTravelCostsPage travel = new AssessTravelCostsPage(page);
    travel.setAssessedValue("10.00");
    travel.saveChanges();

    review.clickChangeWaitingCosts();
    AssessWaitingCostsPage waiting = new AssessWaitingCostsPage(page);
    waiting.setAssessedValue("12.34");
    waiting.saveChanges();

    // -------- Total claim value (assessed totals) --------

    review.clickAddAssessedTotalVat();
    AssessAssessedTotalsPage assessedTotals = new AssessAssessedTotalsPage(page);
    assessedTotals.setTotalVat("5.00");
    assessedTotals.setTotalInclVat("1000.00");
    assessedTotals.saveChanges();

    // -------- Total allowed value --------

    review.clickAddAllowedTotalVat();
    AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
    allowedTotals.setTotalVat("6.00");
    allowedTotals.setTotalInclVat("1100.00");
    allowedTotals.saveChanges();

    // -------- Submit --------

    review.saveChanges();

    AssessmentCompletePage complete = new AssessmentCompletePage(page);

    assertThat(complete.getBodyText()).hasText("Your changes have been submitted");
    assertThat(complete.getGoToSearchButton()).isVisible();
    assertThat(complete.getViewAssessedClaimButton()).isVisible();
  }
}
