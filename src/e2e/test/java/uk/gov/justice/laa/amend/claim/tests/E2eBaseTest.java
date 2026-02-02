package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.Assertions;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.pages.AssessAllowedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessAssessedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessProfitCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentCompletePage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

public abstract class E2eBaseTest extends BaseTest {

    protected final String PROVIDER_ACCOUNT = "123456";
    protected final String UFN = generateUfn();

    protected void submitWithAddedProfitCostsAndAllowedTotals(String assessmentOutcome) {
        findClaim();

        addAssessmentOutcome(assessmentOutcome);

        ReviewAndAmendPage review = getReviewPage();

        addProfitCosts(review, "999.99");
        addAllowedTotals(review, "300", "400");

        submit(review);

        viewAssessedClaim();

        checkAssessedClaim("£300.00", "£400.00", "£300.00", "£400.00");
    }

    protected void submitWithAddedProfitCostsAndAssessedTotalsAndAllowedTotals(String assessmentOutcome) {
        findClaim();

        addAssessmentOutcome(assessmentOutcome);

        ReviewAndAmendPage review = getReviewPage();

        addProfitCosts(review, "100");
        addAssessedTotals(review, "200", "300");
        addAllowedTotals(review, "400", "500");

        submit(review);

        viewAssessedClaim();

        checkAssessedClaim("£200.00", "£300.00", "£400.00", "£500.00");
    }

    protected void submitNilled() {
        findClaim();

        addAssessmentOutcome("nilled");

        ReviewAndAmendPage review = getReviewPage();

        submit(review);

        viewAssessedClaim();

        checkAssessedClaim("£0.00", "£0.00", "£0.00", "£0.00");
    }

    protected void submitWithAddedAllowedTotals(String assessmentOutcome) {
        findClaim();

        addAssessmentOutcome(assessmentOutcome);

        ReviewAndAmendPage review = getReviewPage();

        addAllowedTotals(review, "300", "400");

        submit(review);

        viewAssessedClaim();

        checkAssessedClaim("£300.00", "£400.00", "£300.00", "£400.00");
    }

    protected void submitWithAddedAssessedAndAllowedTotals(String assessmentOutcome) {
        findClaim();

        addAssessmentOutcome(assessmentOutcome);

        ReviewAndAmendPage review = getReviewPage();

        addAssessedTotals(review, "100", "200");
        addAllowedTotals(review, "300", "400");

        submit(review);

        viewAssessedClaim();

        checkAssessedClaim("£100.00", "£200.00", "£300.00", "£400.00");
    }

    private void findClaim() {
        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            "",
            "",
            UFN,
            ""
        );

        search.clickViewForUfn(UFN);
    }

    private void addAssessmentOutcome(String assessmentOutcome) {
        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();

        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome(assessmentOutcome);
        outcome.selectVatLiable(true);
        outcome.clickContinue();
    }

    private ReviewAndAmendPage getReviewPage() {
        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        Assertions.assertEquals("Review and amend", review.getHeadingText());
        return review;
    }

    private void viewAssessedClaim() {
        AssessmentCompletePage complete = new AssessmentCompletePage(page);
        complete.waitForPage();

        Assertions.assertEquals("Assessment complete", complete.getHeadingText());
        Assertions.assertTrue(complete.getBodyText().contains("Your changes have been submitted"));
        Assertions.assertTrue(complete.goToSearchExists());
        Assertions.assertTrue(complete.viewAssessedClaimExists());

        complete.clickViewAssessedClaim();
    }

    private void checkAssessedClaim(String assessedTotalVat, String assessedTotalInclVat, String allowedTotalVat, String allowedTotalInclVat) {
        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.waitForPage();
        claimDetails.assertUpdateAssessmentOutcomeButtonIsPresent();

        claimDetails.assertAssessedTotals("Assessed total VAT", "Not applicable", "Not applicable", assessedTotalVat);
        claimDetails.assertAssessedTotals("Assessed total incl VAT", "Not applicable", "Not applicable", assessedTotalInclVat);

        claimDetails.assertAllowedTotals("Allowed total VAT", "£127.87", "Not applicable", allowedTotalVat);
        claimDetails.assertAllowedTotals("Allowed total incl VAT", "£767.22", "Not applicable", allowedTotalInclVat);
    }

    private void addProfitCosts(ReviewAndAmendPage review, String profitCosts) {
        review.clickAddProfitCosts();
        AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
        profit.waitForPage();
        profit.setAssessedValue(profitCosts);
        profit.saveChanges();
    }

    private void addAssessedTotals(ReviewAndAmendPage review, String assessedTotalVat, String assessedTotalInclVat) {
        review.waitForPage();
        review.clickAddAssessedTotalVat();
        AssessAssessedTotalsPage assessedTotals = new AssessAssessedTotalsPage(page);
        assessedTotals.waitForPage();
        assessedTotals.setAssessedTotalVat(assessedTotalVat);
        assessedTotals.setAssessedTotalInclVat(assessedTotalInclVat);
        assessedTotals.saveChanges();
    }

    private void addAllowedTotals(ReviewAndAmendPage review, String allowedTotalVat, String allowedTotalInclVat) {
        review.waitForPage();
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
        allowedTotals.waitForPage();
        allowedTotals.setAllowedTotalVat(allowedTotalVat);
        allowedTotals.setAllowedTotalInclVat(allowedTotalInclVat);
        allowedTotals.saveChanges();
    }

    private void submit(ReviewAndAmendPage review) {
        review.waitForPage();
        review.submitAdjustments();
    }
}
