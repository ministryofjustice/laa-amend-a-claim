package uk.gov.justice.laa.amend.claim.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SearchData;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("E2E")
@Feature("Search")
public class SearchTest extends BaseTest {

    // ---------------- Submission 1 (Has claims) ----------------
    private final String SUBMISSION_ID_1 = UUID.randomUUID().toString();
    private final String CLAIM_ID_1 = UUID.randomUUID().toString();
    private final String CLAIM_SUMMARY_FEE_ID_1 = UUID.randomUUID().toString();
    private final String CALCULATED_FEE_DETAIL_ID_1 = UUID.randomUUID().toString();

    // ---------------- Submission 2 (Does not have claims) ----------------
    private final String SUBMISSION_ID_2 = UUID.randomUUID().toString();

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
                .id(SUBMISSION_ID_1)
                .bulkSubmissionId(BULK_SUBMISSION_ID)
                .officeAccountNumber("123456")
                .submissionPeriod("APR-2025")
                .areaOfLaw("CRIME_LOWER")
                .userId(USER_ID)
                .build(),

            SubmissionInsert
                .builder()
                .id(SUBMISSION_ID_2)
                .bulkSubmissionId(BULK_SUBMISSION_ID)
                .officeAccountNumber("234567")
                .submissionPeriod("OCT-2025")
                .areaOfLaw("LEGAL_HELP")
                .userId(USER_ID)
                .build(),

            ClaimInsert
                .builder()
                .id(CLAIM_ID_1)
                .submissionId(SUBMISSION_ID_1)
                .uniqueFileNumber("121019/001")
                .userId(USER_ID)
                .build(),

            ClaimSummaryFeeInsert
                .builder()
                .id(CLAIM_SUMMARY_FEE_ID_1)
                .claimId(CLAIM_ID_1)
                .userId(USER_ID)
                .build(),

            CalculatedFeeDetailInsert
                .builder()
                .id(CALCULATED_FEE_DETAIL_ID_1)
                .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID_1)
                .claimId(CLAIM_ID_1)
                .escaped(true)
                .userId(USER_ID)
                .build()
        );
    }

    private static Stream<Arguments> searchConfigProvider() {
        return loadSearchConfigs().stream().map(Arguments::of);
    }

    private static List<SearchData> loadSearchConfigs() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = SearchTest.class.getClassLoader().getResourceAsStream("search-config.json")) {
            if (is == null) {
                throw new RuntimeException("search-config.json not found in test resources");
            }
            return mapper.readValue(is, new TypeReference<List<SearchData>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load search config", e);
        }
    }

    @ParameterizedTest
    @MethodSource("searchConfigProvider")
    @Story("Search for claim")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Search: submit form and results available")
    void canSearchForClaim(SearchData config) {
        String baseUrl = EnvConfig.baseUrl();
        SearchPage searchPage = new SearchPage(page).navigateTo(baseUrl);

        searchPage.searchForClaim(
            config.getProviderAccountNumber(),
            config.getSubmissionMonth(),
            config.getSubmissionYear(),
            config.getUfn(),
            config.getCrn(),
            config.isExpectedResults()
        );

        boolean hasResults = searchPage.hasResults();
        Assertions.assertEquals(config.isExpectedResults(), hasResults, "Results presence should match expected.");
    }

    @Test
    @DisplayName("Search values retained upon 'Back to search' click")
    void backToSearch() {
        String baseUrl = EnvConfig.baseUrl();
        SearchPage search = new SearchPage(page).navigateTo(baseUrl);

        search.searchForClaim(
            "123456",
            "04",
            "2025",
            "",
            ""
        );

        search.clickViewForUfn("121019/001");

        ClaimDetailsPage details = new ClaimDetailsPage(page);
        details.waitForPage();

        details.clickBackToSearchButton();
        assertTrue(page.url().contains("/?providerAccountNumber=123456&submissionDateMonth=04&submissionDateYear=2025&page=1&sort=uniqueFileNumber,asc"));
    }
}