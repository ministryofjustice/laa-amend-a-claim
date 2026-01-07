package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssessmentOutcomeTest extends BaseTest {

    private static final String CRIME_PROVIDER_ACCOUNT = "2R223X";
    private static final String CRIME_UFN = "031222/002";

    private void navigateToAssessmentOutcome() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
                CRIME_PROVIDER_ACCOUNT,
                "04",
                "2025",
                CRIME_UFN,
                ""
        );

        search.clickViewForUfn(CRIME_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();
        details.clickAddUpdateAssessmentOutcome();
    }

    @Test
    @DisplayName("Assessment outcome page loads: shows 4 outcome radios + VAT question + Continue")
    void assessmentOutcomePageLoadsSuccessfully() {
        navigateToAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.assertPageLoaded();

        assertTrue(page.url().contains("/assessment-outcome"));
    }

    @Test
    @DisplayName("Assessment outcome validation: Continue without selecting outcome shows GOV.UK error summary")
    void continueWithoutSelectingOutcomeShowsError() {
        navigateToAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();

        outcome.clickContinue();

        assertTrue(page.url().contains("/assessment-outcome"));
        outcome.assertAssessmentOutcomeRequiredError();
    }
}