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
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;
import uk.gov.justice.laa.amend.claim.pages.AssessmentOutcomePage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.DiscardAssessmentPage;
import uk.gov.justice.laa.amend.claim.pages.ReviewAndAmendPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import java.util.List;
import java.util.UUID;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

@Epic("Assessment Discard Flow")
@Feature("Discard Assessment Confirmation & Behaviour")
public class DiscardAssessmentTest extends BaseTest {

    private final String PROVIDER_ACCOUNT = "123456";
    private final String UFN = generateUfn();
    private final String MONTH = "04";
    private final String YEAR = "2025";
    private final String SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CLAIM_ID = UUID.randomUUID().toString();
    private final String CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

    @Override
    protected List<Insert> inserts() {
        return List.of(
            BulkSubmissionInsert
                .builder()
                .id(BULK_SUBMISSION_ID)
                .userId(USER_ID)
                .build(),

            SubmissionInsert
                .builder()
                .id(SUBMISSION_ID)
                .bulkSubmissionId(BULK_SUBMISSION_ID)
                .officeAccountNumber(PROVIDER_ACCOUNT)
                .submissionPeriod("APR-2025")
                .areaOfLaw("CRIME_LOWER")
                .userId(USER_ID)
                .build(),

            ClaimInsert
                .builder()
                .id(CLAIM_ID)
                .submissionId(SUBMISSION_ID)
                .uniqueFileNumber(UFN)
                .userId(USER_ID)
                .build(),

            ClaimSummaryFeeInsert
                .builder()
                .id(CLAIM_SUMMARY_FEE_ID)
                .claimId(CLAIM_ID)
                .userId(USER_ID)
                .build(),

            CalculatedFeeDetailInsert
                .builder()
                .id(CALCULATED_FEE_DETAIL_ID)
                .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID)
                .claimId(CLAIM_ID)
                .escaped(true)
                .userId(USER_ID)
                .build()
        );
    }

    @Test
    @Story("AC 1 - Screen display")
    @DisplayName("Discard Assessment: Screen displays correct heading, button, and return link")
    @Severity(SeverityLevel.CRITICAL)
    void discardAssessmentScreenDisplaysCorrectly() {

        DiscardAssessmentPage discard = goToDiscardAssessmentScreen();

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

        DiscardAssessmentPage discard = goToDiscardAssessmentScreen();
        discard.clickDiscardAssessment();

        SearchPage searchAfterDiscard = new SearchPage(page);

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

        DiscardAssessmentPage discard = goToDiscardAssessmentScreen();
        discard.clickReturnToClaim();

        ReviewAndAmendPage review = new ReviewAndAmendPage(page);
    }

    @Test
    @Story("AC 4 - Discard changes on Review and Amend")
    @DisplayName("Review & Amend: Discard changes navigates to Discard Assessment screen")
    @Severity(SeverityLevel.CRITICAL)
    void reviewAndAmendDiscardNavigatesToDiscardScreen() {

        ReviewAndAmendPage review = goToReviewAndAmendPage();
        review.discardChanges();

        DiscardAssessmentPage discard = new DiscardAssessmentPage(page);
    }

    private DiscardAssessmentPage goToDiscardAssessmentScreen() {
        ReviewAndAmendPage review = goToReviewAndAmendPage();
        review.discardChanges();

        return new DiscardAssessmentPage(page);
    }

    private ReviewAndAmendPage goToReviewAndAmendPage() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
            PROVIDER_ACCOUNT,
            MONTH,
            YEAR,
            UFN,
            ""
        );

        search.clickViewForUfn(UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);

        Assertions.assertFalse(
                details.isAddAssessmentOutcomeDisabled(),
                "Test data issue: expected escape claim (Add assessment outcome enabled) but it was disabled"
        );

        details.clickAddUpdateAssessmentOutcome();

        AssessmentOutcomePage outcome = new AssessmentOutcomePage(page);
        outcome.completeAssessment("assessed in full", true);

        return new ReviewAndAmendPage(page);
    }
}