package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.utils.ClaimDetailsFixture;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;

import java.io.InputStream;

@Epic("Claim Details")
@Feature("Assessment Outcome Button State")
public class ClaimDetailsTest extends BaseTest {


    private static final String NON_ESCAPE_UFN = "021123/005";
    private static final String ESCAPE_UFN = "031222/002";

    private static final String CRIME_PROVIDER_ACCOUNT = "2R223X";
    private static final String CRIME_UFN = "031222/002";
    private static final String CRIME_FIXTURE_PATH = "fixtures/claim-details/crime-111018-001.json";

    // Civil claim data (your new case)
    private static final String CIVIL_PROVIDER_ACCOUNT = "2N199K";
    private static final String CIVIL_UFN = "121019/001";
    private static final String CIVIL_FIXTURE_PATH = "fixtures/claim-details/civil-121019-001.json";

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

    @Test
    @Story("Escape claim")
    @DisplayName("Claim Details: Add assessment outcome is enabled for escape claims")
    @Severity(SeverityLevel.CRITICAL)
    void addAssessmentOutcomeIsEnabledForEscapeClaim() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
                CRIME_PROVIDER_ACCOUNT,
            "04",           
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

    @Test
    @Story("Crime claim")
    @DisplayName("Claim Details (Crime): Values match fixture")
    @Severity(SeverityLevel.CRITICAL)
    void crimeClaimValuesMatchFixture() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
                CRIME_PROVIDER_ACCOUNT,
                "",
                "",
                CRIME_UFN,
                ""
        );

        search.clickViewForUfn(CRIME_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();

        ClaimDetailsFixture fixture = loadFixture(CRIME_FIXTURE_PATH);

        Assertions.assertEquals(
                fixture.isAddAssessmentOutcomeDisabled(),
                details.isAddAssessmentOutcomeDisabled(),
                "Add assessment outcome enabled/disabled state mismatch"
        );

        details.assertAllValues(fixture.getValues());
    }

    @Test
    @Story("Civil claim")
    @DisplayName("Claim Details (Civil): Values match fixture")
    @Severity(SeverityLevel.CRITICAL)
    void civilClaimValuesMatchFixture() {
        String baseUrl = EnvConfig.baseUrl();

        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        // Civil claim search (provider account + UFN)
        search.searchForClaim(
                CIVIL_PROVIDER_ACCOUNT,
                "",
                "",
                CIVIL_UFN,
                ""
        );

        search.clickViewForUfn(CIVIL_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();

        ClaimDetailsFixture fixture = loadFixture(CIVIL_FIXTURE_PATH);

        Assertions.assertEquals(
                fixture.isAddAssessmentOutcomeDisabled(),
                details.isAddAssessmentOutcomeDisabled(),
                "Add assessment outcome enabled/disabled state mismatch"
        );

        details.assertAllValues(fixture.getValues());
    }

    private static ClaimDetailsFixture loadFixture(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = ClaimDetailsTest.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Fixture not found: " + resourcePath);
            }
            return mapper.readValue(is, ClaimDetailsFixture.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load fixture: " + resourcePath, e);
        }
    }
}