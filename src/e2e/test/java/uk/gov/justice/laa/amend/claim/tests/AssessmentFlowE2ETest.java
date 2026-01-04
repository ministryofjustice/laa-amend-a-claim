// package uk.gov.justice.laa.amend.claim.tests;

// import base.BaseTest;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import uk.gov.justice.laa.amend.claim.pages.*;
// import uk.gov.justice.laa.amend.claim.utils.EnvConfig;

// public class AssessmentFlowE2ETest extends BaseTest {

//     private static final String CRIME_PROVIDER_ACCOUNT = "0P322F";
//     private static final String CRIME_UFN = "111018/001";

//     @Test
//     @DisplayName("E2E: Full Crime Assessment Flow – Search → View → Outcome → Amend All → Submit")
//     void fullAssessmentFlow() {
//         String baseUrl = EnvConfig.baseUrl();

//         SearchPage search = new SearchPage(page).navigateTo(baseUrl);

//         search.searchForClaim(
//                 CRIME_PROVIDER_ACCOUNT,
//                 "03",
//                 "2020",
//                 CRIME_UFN,
//                 ""
//         );

//         search.clickViewForUfn(CRIME_UFN);

//         ClaimDetailsPage details = new ClaimDetailsPage(page);
//         details.waitForPage();
//         details.clickAddAssessmentOutcome();

//         AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
//         outcome.waitForPage();
//         outcome.selectAssessmentOutcome("assessed in full");
//         outcome.selectVatLiable(true);
//         outcome.clickContinue();

//         ReviewAndAmendPage review = new ReviewAndAmendPage(page);
//         review.waitForPage();
//         Assertions.assertEquals("Review and amend", review.getHeadingText());

//         // -------- Claim costs --------

//         review.clickChangeProfitCosts();
//         AssessProfitCostsPage profit = new AssessProfitCostsPage(page);
//         profit.waitForPage();
//         profit.setAssessedValue("999.99");
//         profit.saveChanges();

//         review.waitForPage();
//         review.clickChangeDisbursements();
//         AssessDisbursementsPage disb = new AssessDisbursementsPage(page);
//         disb.waitForPage();
//         disb.setAssessedValue("111.11");
//         disb.saveChanges();

//         review.waitForPage();
//         review.clickChangeDisbursementsVat();
//         AssessDisbursementsVatPage disbVat = new AssessDisbursementsVatPage(page);
//         disbVat.waitForPage();
//         disbVat.setAssessedValue("22.22");
//         disbVat.saveChanges();

//         review.waitForPage();
//         review.clickChangeTravelCosts();
//         AssessTravelCostsPage travel = new AssessTravelCostsPage(page);
//         travel.waitForPage();
//         travel.setAssessedValue("10.00");
//         travel.saveChanges();

//         review.waitForPage();
//         review.clickChangeWaitingCosts();
//         AssessWaitingCostsPage waiting = new AssessWaitingCostsPage(page);
//         waiting.waitForPage();
//         waiting.setAssessedValue("12.34");
//         waiting.saveChanges();

//         // -------- Total claim value (assessed totals) --------

//         review.waitForPage();
//         review.clickAddAssessedTotalVat();
//         AssessTotalClaimValuePage assessedTotals = new AssessTotalClaimValuePage(page);
//         assessedTotals.waitForPage();
//         assessedTotals.setAssessedTotalVat("5.00");
//         assessedTotals.setAssessedTotalInclVat("1000.00");
//         assessedTotals.saveChanges();

//         // -------- Total allowed value --------

//         review.waitForPage();
//         review.clickAddAllowedTotalVat();
//         AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
//         allowedTotals.waitForPage();
//         allowedTotals.setAllowedTotalVat("6.00");
//         allowedTotals.setAllowedTotalInclVat("1100.00");
//         allowedTotals.saveChanges();

//         // -------- Submit --------

//         review.waitForPage();
//         review.submitAdjustments();

//         AssessmentCompletePage complete = new AssessmentCompletePage(page);
//         complete.waitForPage();

//         Assertions.assertEquals("Assessment complete", complete.getHeadingText());
//         Assertions.assertTrue(complete.getBodyText().contains("Your changes have been submitted"));
//         Assertions.assertTrue(complete.goToSearchExists());
//         Assertions.assertTrue(complete.viewAssessedClaimExists());
//     }
// }