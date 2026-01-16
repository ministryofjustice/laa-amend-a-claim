package uk.gov.justice.laa.amend.claim.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.pages.AssessAllowedTotalsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessProfitCostsPage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentCompletePage;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.models.ClaimDetailsFixture;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;

import java.util.List;

import static uk.gov.justice.laa.amend.claim.base.E2ETestHelper.getFromMap;
import static uk.gov.justice.laa.amend.claim.base.E2ETestHelper.loadFixture;
import static uk.gov.justice.laa.amend.claim.base.E2ETestHelper.normalizeMoneyForInput;

@Epic("ClaimDetails")
@Feature("Assessment Totals")
public class AssessedClaimDetailsTest extends BaseTest {

    @Override
    protected List<Insert> inserts() {
        return List.of();
    }

    @Disabled("Disabled this until we have test data for INVC fee codes")
    @DisplayName("E2E: Assessed ClaimDetails - Reduced (still escaped) - Show claim Assessed/Allowed totals")
    void showClaimTotals() {
        ClaimDetailsFixture claimDetailsFixture = loadFixture("fixtures/claim-details/crime-reduced-002.json");
        SearchPage search = new SearchPage(page).navigateTo(EnvConfig.baseUrl());

        search.searchForClaim(
            claimDetailsFixture.getProviderAccount(),
            "",
            "",
            claimDetailsFixture.getUfn(),
            ""
        );

        search.clickViewForUfn(claimDetailsFixture.getUfn());

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();


        details.clickAddUpdateAssessmentOutcome();


        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.waitForPage();
        outcome.selectAssessmentOutcome(claimDetailsFixture.getOutcome());
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


        // -------- Total allowed value --------

        int colIndex = 2;

        String allowedVatRaw = getFromMap(claimDetailsFixture.getAllowedTotals(), "Allowed total VAT", colIndex);
        String allowedInclVatRaw = getFromMap(claimDetailsFixture.getAllowedTotals(), "Allowed total incl VAT", colIndex);

        // Normalize for input fields (strip currency, commas, handle "Not applicable")
        String allowedVat = normalizeMoneyForInput(allowedVatRaw);       // e.g., "£239.35" -> "239.35"
        String allowedInclVat = normalizeMoneyForInput(allowedInclVatRaw); // "Not applicable" -> null


        review.waitForPage();
        review.clickAddAllowedTotalVat();
        AssessAllowedTotalsPage allowedTotals = new AssessAllowedTotalsPage(page);
        allowedTotals.waitForPage();
        allowedTotals.setAllowedTotalVat(allowedVat);
        allowedTotals.setAllowedTotalInclVat(allowedInclVat);
        allowedTotals.saveChanges();

        // -------- Submit --------

        review.waitForPage();
        review.submitAdjustments();

        AssessmentCompletePage complete = new AssessmentCompletePage(page);
        complete.waitForPage();

        Assertions.assertEquals("Assessment complete", complete.getHeadingText());
        Assertions.assertTrue(complete.getBodyText().contains("Your changes have been submitted"));
        Assertions.assertTrue(complete.goToSearchExists());
        Assertions.assertTrue(complete.viewAssessedClaimExists());

        complete.clickViewAssessedClaim();

        ClaimDetailsPage claimDetails = new ClaimDetailsPage(page);
        claimDetails.waitForPage();
        claimDetails.assertButtonLabel("Update assessment outcome");
        claimDetails.assertAllowedTotals(claimDetailsFixture.getAllowedTotals());
        claimDetails.assertAssessedTotals(claimDetailsFixture.getAssessedTotals());
    }

}
