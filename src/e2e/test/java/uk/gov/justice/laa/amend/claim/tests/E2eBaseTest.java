package uk.gov.justice.laa.amend.claim.tests;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

import org.junit.jupiter.api.Assertions;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.pages.AssessAllowedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessAssessedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessProfitCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentCompletePage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

public abstract class E2eBaseTest extends BaseTest {

    protected final String PROVIDER_ACCOUNT = "123456";
    protected final String UFN = generateUfn();

    protected void submitWithAddedProfitCostsAndAllowedTotals(String assessmentOutcome) {
        findClaim();

        addAssessmentOutcome(assessmentOutcome);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);

        addProfitCosts(review, "999.99");
        addAllowedTotals(review, "300", "400");

        review.saveChanges();

        viewAssessedClaim();

        checkAssessedClaim("£300.00", "£400.00", "£300.00", "£400.00");
    }

    protected void submitWithAddedProfitCostsAndAssessedTotalsAndAllowedTotals(String assessmentOutcome) {
        findClaim();

        addAssessmentOutcome(assessmentOutcome);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);

        addProfitCosts(review, "100");
        addAssessedTotals(review, "200", "300");
        addAllowedTotals(review, "400", "500");

        review.saveChanges();

        viewAssessedClaim();

        checkAssessedClaim("£200.00", "£300.00", "£400.00", "£500.00");
    }

    protected void submitNilled() {
        findClaim();

        addAssessmentOutcome("nilled");

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);

        review.saveChanges();

        viewAssessedClaim();

        checkAssessedClaim("£0.00", "£0.00", "£0.00", "£0.00");
    }

    protected void submitWithAddedAllowedTotals(String assessmentOutcome) {
        findClaim();

        addAssessmentOutcome(assessmentOutcome);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);

        addAllowedTotals(review, "300", "400");

        review.saveChanges();

        viewAssessedClaim();

        checkAssessedClaim("£300.00", "£400.00", "£300.00", "£400.00");
    }

    protected void submitWithAddedAssessedTotalsAndAllowedTotals(String assessmentOutcome) {
        findClaim();

        addAssessmentOutcome(assessmentOutcome);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);

        addAssessedTotals(review, "100", "200");
        addAllowedTotals(review, "300", "400");

        review.saveChanges();

        viewAssessedClaim();

        checkAssessedClaim("£100.00", "£200.00", "£300.00", "£400.00");
    }

    private void findClaim() {
        SearchPage search = new SearchPage(page);

        search.searchForClaim(PROVIDER_ACCOUNT, "", "", UFN, "");

        search.clickViewForUfn(UFN);
    }

    private void addAssessmentOutcome(String assessmentOutcome) {
        ClaimDetailsPage details = new ClaimDetailsPage(page);

        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.selectAssessmentOutcome(assessmentOutcome);
        outcome.selectVatLiable(true);
        outcome.saveChanges();
    }

    private void viewAssessedClaim() {
        AssessmentCompletePage complete = new AssessmentCompletePage(page);

        Assertions.assertTrue(complete.getBodyText().contains("Your changes have been submitted"));
        Assertions.assertTrue(complete.goToSearchExists());
        Assertions.assertTrue(complete.viewAssessedClaimExists());

        complete.clickViewAssessedClaim();
    }

    private void checkAssessedClaim(
            String assessedTotalVat, String assessedTotalInclVat, String allowedTotalVat, String allowedTotalInclVat) {
        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.assertUpdateAssessmentOutcomeButtonIsPresent();

        claimDetails.assertAssessedTotals("Assessed total VAT", "Not applicable", "Not applicable", assessedTotalVat);
        claimDetails.assertAssessedTotals(
                "Assessed total incl VAT", "Not applicable", "Not applicable", assessedTotalInclVat);

        claimDetails.assertAllowedTotals("Allowed total VAT", "£127.87", "Not applicable", allowedTotalVat);
        claimDetails.assertAllowedTotals("Allowed total incl VAT", "£767.22", "Not applicable", allowedTotalInclVat);
    }

    private void addProfitCosts(ReviewAndAmendPage review, String profitCosts) {
        review.clickAddProfitCosts();
        AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
        profit.setAssessedValue(profitCosts);
        profit.saveChanges();
    }

    private void addAssessedTotals(ReviewAndAmendPage review, String assessedTotalVat, String assessedTotalInclVat) {
        review.clickAddAssessedTotalVat();
        AssessAssessedTotalsPage assessedTotals = new AssessAssessedTotalsPage(page);
        assessedTotals.setTotalVat(assessedTotalVat);
        assessedTotals.setTotalInclVat(assessedTotalInclVat);
        assessedTotals.saveChanges();
    }

    private void addAllowedTotals(ReviewAndAmendPage review, String allowedTotalVat, String allowedTotalInclVat) {
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
        allowedTotals.setTotalVat(allowedTotalVat);
        allowedTotals.setTotalInclVat(allowedTotalInclVat);
        allowedTotals.saveChanges();
    }
}
