package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import uk.gov.justice.laa.amend.claim.pages.*;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;

@Epic("Assessment Discard Flow")
@Feature("Discard Assessment Confirmation & Behaviour")
public class DiscardAssessmentTest extends BaseTest {

    private static final String ESCAPE_UFN = "290419/711";

    @Test
    @Story("AC 1 - Screen display")
    @DisplayName("Discard Assessment: Screen displays correct heading, button, and return link")
    @Severity(SeverityLevel.CRITICAL)
    void discardAssessmentScreenDisplaysCorrectly() {

        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim("123456", "10", "2025", ESCAPE_UFN, "");
        search.clickViewForUfn(ESCAPE_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.completeAssessment("assessed in full", true);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.discardChanges();

        DiscardAssessmentPage discard = new DiscardAssessmentPage(page);
        discard.waitForPage();

        Assertions.assertEquals("Confirm you want to discard this assessment",
                discard.getHeadingText());

        Assertions.assertTrue(discard.isDiscardAssessmentButtonVisible(),
                "Discard Assessment button must be visible");

        Assertions.assertTrue(discard.isReturnToClaimLinkVisible(),
                "Return to Claim link must be visible");
    }

    @Test
    @Story("AC 2 - Discard assessment button")
    @DisplayName("Discard Assessment: Clicking discard redirects to search page with success banner")
    @Severity(SeverityLevel.CRITICAL)
    void discardAssessmentRedirectsToSearchWithBanner() {

        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim("123456", "10", "2025", ESCAPE_UFN, "");
        search.clickViewForUfn(ESCAPE_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.completeAssessment("assessed in full", true);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.discardChanges();

        DiscardAssessmentPage discard = new DiscardAssessmentPage(page);
        discard.waitForPage();

        discard.clickDiscardAssessment();

        SearchPage searchAfterDiscard = new SearchPage(page);
        searchAfterDiscard.waitForPage();

        Assertions.assertTrue(searchAfterDiscard.isSuccessBannerVisible(),
                "Success notification banner should appear after discarding");

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

        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim("123456", "10", "2025", ESCAPE_UFN, "");
        search.clickViewForUfn(ESCAPE_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.completeAssessment("assessed in full", true);

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
        review.waitForPage();
        review.discardChanges();

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

        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);
        search.searchForClaim("123456", "10", "2025", ESCAPE_UFN, "");
        search.clickViewForUfn(ESCAPE_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.completeAssessment("assessed in full", true);

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
}