package uk.gov.justice.laa.amend.claim.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.DiscardAssessmentPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;

@Epic("Assessment Discard Flow")
@Feature("Discard Assessment Confirmation & Behaviour")
public class DiscardAssessmentTest extends BaseTest {


    private static final EscapeClaim ESCAPE_CLAIM = new EscapeClaim(
            "2R223X",       
            "04",           
            "2025",         
            "031222/002"    
    );

    @Test
    @Story("AC 1 - Screen display")
    @DisplayName("Discard Assessment: Screen displays correct heading, button, and return link")
    @Severity(SeverityLevel.CRITICAL)
    void discardAssessmentScreenDisplaysCorrectly() {

        goToDiscardAssessmentScreen();

        DiscardAssessmentPage discard = new DiscardAssessmentPage(page);
        discard.waitForPage();

        Assertions.assertEquals(
                "Confirm you want to discard this assessment",
                discard.getHeadingText()
        );

        Assertions.assertTrue(
                discard.isDiscardAssessmentButtonVisible(),
                "Discard Assessment button must be visible"
        );

        Assertions.assertTrue(
                discard.isReturnToClaimLinkVisible(),
                "Return to Claim link must be visible"
        );
    }

    @Test
    @Story("AC 2 - Discard assessment button")
    @DisplayName("Discard Assessment: Clicking discard redirects to search page with success banner")
    @Severity(SeverityLevel.CRITICAL)
    void discardAssessmentRedirectsToSearchWithBanner() {

        goToDiscardAssessmentScreen();

        DiscardAssessmentPage discard = new DiscardAssessmentPage(page);
        discard.waitForPage();
        discard.clickDiscardAssessment();

        SearchPage searchAfterDiscard = new SearchPage(page);
        searchAfterDiscard.waitForPage();

        Assertions.assertTrue(
                searchAfterDiscard.isSuccessBannerVisible(),
                "Success notification banner should appear after discarding"
        );

        Assertions.assertEquals(
                "You discarded the assessment",
                searchAfterDiscard.getSuccessBannerHeading(),
                "Success banner heading must match expected text"
        );
    }

    @Test
    @Story("AC 3 - Return to Claim link")
    @DisplayName("Discard Assessment: Return to Claim navigates back to Review and Amend screen")
    @Severity(SeverityLevel.CRITICAL)
    void returnToClaimNavigatesBackToReviewScreen() {

        goToDiscardAssessmentScreen();

        DiscardAssessmentPage discard = new DiscardAssessmentPage(page);
        discard.waitForPage();
        discard.clickReturnToClaim();

        ReviewAndAmendPage reviewBack = new ReviewAndAmendPage(page);
        reviewBack.waitForPage();

        Assertions.assertEquals(
                "Review and amend",
                reviewBack.getHeadingText(),
                "Should return to Review and amend page"
        );
    }

    @Test
    @Story("AC 4 - Discard changes on Review and Amend")
    @DisplayName("Review & Amend: Discard changes navigates to Discard Assessment screen")
    @Severity(SeverityLevel.CRITICAL)
    void reviewAndAmendDiscardNavigatesToDiscardScreen() {

        goToReviewAndAmendPage();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.discardChanges();

        DiscardAssessmentPage discard = new DiscardAssessmentPage(page);
        discard.waitForPage();

        Assertions.assertEquals(
                "Confirm you want to discard this assessment",
                discard.getHeadingText(),
                "Should land on discard confirmation screen"
        );
    }

    private void goToDiscardAssessmentScreen() {
        goToReviewAndAmendPage();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.discardChanges();

        DiscardAssessmentPage discard = new DiscardAssessmentPage(page);
        discard.waitForPage();
    }

    private void goToReviewAndAmendPage() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
                ESCAPE_CLAIM.providerAccount,
                ESCAPE_CLAIM.month,
                ESCAPE_CLAIM.year,
                ESCAPE_CLAIM.ufn,
                ""
        );

        search.clickViewForUfn(ESCAPE_CLAIM.ufn);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();

        Assertions.assertFalse(
                details.isAddAssessmentOutcomeDisabled(),
                "Test data issue: expected escape claim (Add assessment outcome enabled) but it was disabled"
        );

        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.completeAssessment("assessed in full", true);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
    }

    private static class EscapeClaim {
        final String providerAccount;
        final String month;
        final String year;
        final String ufn;

        EscapeClaim(String providerAccount, String month, String year, String ufn) {
            this.providerAccount = providerAccount;
            this.month = month;
            this.year = year;
            this.ufn = ufn;
        }
    }
}