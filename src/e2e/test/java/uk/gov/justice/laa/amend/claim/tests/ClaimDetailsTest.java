package uk.gov.justice.laa.amend.claim.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.models.ClaimDetailsFixture;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;

import java.util.stream.Stream;

import static uk.gov.justice.laa.amend.claim.base.E2ETestHelper.loadFixture;

@Epic("Claim Details")
@Feature("Assessment Outcome Button State")
public class ClaimDetailsTest extends BaseTest {

    private static final String NON_ESCAPE_UFN = "021123/005";

    private static final String CRIME_PROVIDER_ACCOUNT = "2R223X";

    static Stream<ClaimDetailsFixture> detailsCases() {
        return Stream.of(
            // crime
            loadFixture(
                "fixtures/claim-details/crime-111018-001.json"
            ),
            // civil
            loadFixture(
                "fixtures/claim-details/civil-121019-001.json"
            )
        );
    }

    @Test
    @Story("Non-escape claim")
    @DisplayName("Claim Details: Add assessment outcome is disabled for non-escape claims")
    @Severity(SeverityLevel.CRITICAL)
    void addAssessmentOutcomeIsDisabledForNonEscapeClaim() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
                CRIME_PROVIDER_ACCOUNT,
            "11",
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

    /**
     * Verifying Totals has been removed from test data, until we fix test data
     *
     * @param claimDetailsFixture
     */
    @ParameterizedTest(name = "[{index}] {0} Claim: Values match fixture")
    @MethodSource("detailsCases")
    @DisplayName("Claim Details: Values match fixture")
    @Severity(SeverityLevel.CRITICAL)
    void crimeClaimValuesMatchFixture(ClaimDetailsFixture claimDetailsFixture) {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

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

        Assertions.assertFalse(
            details.isAddAssessmentOutcomeDisabled(),
            "Expected Add assessment outcome button to be enabled for escape claim"
        );

        Assertions.assertEquals(
            claimDetailsFixture.isAddAssessmentOutcomeDisabled(),
            details.isAddAssessmentOutcomeDisabled(),
            "Add assessment outcome enabled/disabled state mismatch"
        );

        details.assertAllValues(claimDetailsFixture.getValues());
    }
}