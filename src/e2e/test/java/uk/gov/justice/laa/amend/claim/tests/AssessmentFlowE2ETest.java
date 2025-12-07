package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.pages.*;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;

public class AssessmentFlowE2ETest extends BaseTest {

    private static final String UFN = "290419/711";

    @Test
    @DisplayName("E2E: Full Assessment Flow – Search → View → Outcome → Amend All → Submit")
    void fullAssessmentFlow() {

        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim(
                "123456",     // Provider account
                "10", "2025", // Month/year
                UFN,          // UFN
                ""            // CRN
        );

        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();

        details.clickAddAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();

        outcome.selectAssessmentOutcome("assessed in full");
        outcome.selectVatLiable(true);
        outcome.clickContinue();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        Assertions.assertEquals("Review and amend", review.getHeadingText());

        review.clickChangeProfitCosts();
        AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
        profit.waitForPage();
        profit.setAssessedValue("999.99");
        profit.saveChanges();

        review.waitForPage();
        review.clickChangeDisbursements();
        AssessDisbursementsPage disb = new AssessDisbursementsPage(page);
        disb.waitForPage();
        disb.setAssessedValue("111.11");
        disb.saveChanges();

        review.waitForPage();
        review.clickChangeDisbursementsVat();
        AssessDisbursementsVatPage disbVat = new AssessDisbursementsVatPage(page);
        disbVat.waitForPage();
        disbVat.setAssessedValue("22.22");
        disbVat.saveChanges();

        review.waitForPage();
        review.clickChangeTravelCosts();
        AssessTravelCostsPage travel = new AssessTravelCostsPage(page);
        travel.waitForPage();
        travel.setAssessedValue("10.00");
        travel.saveChanges();

        review.waitForPage();
        review.clickChangeWaitingCosts();
        AssessWaitingCostsPage waiting = new AssessWaitingCostsPage(page);
        waiting.waitForPage();
        waiting.setAssessedValue("12.34");
        waiting.saveChanges();

        review.waitForPage();
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowed = new AssessAllowedTotalsPage(page);
        allowed.waitForPage();
        allowed.setAllowedTotalVat("5.00");
        allowed.setAllowedTotalInclVat("1000.00");
        allowed.saveChanges();

        review.waitForPage();
        review.submitAdjustments();

        AssessmentCompletePage complete = new AssessmentCompletePage(page);
        complete.waitForPage();

        Assertions.assertEquals("Assessment complete", complete.getHeadingText());
        Assertions.assertTrue(complete.getBodyText().contains("Your changes have been submitted"));
        Assertions.assertTrue(complete.goToSearchExists());
        Assertions.assertTrue(complete.viewAssessedClaimExists());
    }
}