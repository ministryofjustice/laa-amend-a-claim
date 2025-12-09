package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;

@Epic("Claim Details")
@Feature("Assessment Outcome Button State")
public class ClaimDetailsTest extends BaseTest {

    private static final String NON_ESCAPE_UFN = "101117/712";
    private static final String ESCAPE_UFN = "290419/711";

    @Test
    @Story("Non-escape claim")
    @DisplayName("Claim Details: Add assessment outcome is disabled for non-escape claims")
    @Severity(SeverityLevel.CRITICAL)
    void addAssessmentOutcomeIsDisabledForNonEscapeClaim() {

        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
                "123456",
                "10",
                "2025",
                NON_ESCAPE_UFN,
                ""
        );

        search.clickViewForUfn(NON_ESCAPE_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();

        Assertions.assertTrue(
                details.isAddAssessmentOutcomeDisabled(),
                "Expected Add assessment outcome button to be disabled for non-escape claim"
        );
    }

    @Test
    @Story("Escape claim")
    @DisplayName("Claim Details: Add assessment outcome is enabled for escape claims")
    @Severity(SeverityLevel.CRITICAL)
    void addAssessmentOutcomeIsEnabledForEscapeClaim() {

        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
                "123456",
                "10",
                "2025",
                ESCAPE_UFN,
                ""
        );

        search.clickViewForUfn(ESCAPE_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();

        Assertions.assertFalse(
                details.isAddAssessmentOutcomeDisabled(),
                "Expected Add assessment outcome button to be enabled for escape claim"
        );
    }
}