package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;
import uk.gov.justice.laa.amend.claim.utils.SearchConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

@Epic("E2E")
@Feature("Search")
public class SearchTest extends BaseTest {

    private static Stream<Arguments> searchConfigProvider() {
        return loadSearchConfigs().stream().map(Arguments::of);
    }

    private static List<SearchConfig> loadSearchConfigs() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = SearchTest.class.getClassLoader().getResourceAsStream("search-config.json")) {
            if (is == null) {
                throw new RuntimeException("search-config.json not found in test resources");
            }
            return mapper.readValue(is, new TypeReference<List<SearchConfig>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load search config", e);
        }
    }

    @ParameterizedTest
    @MethodSource("searchConfigProvider")
    @Story("Search for claim")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Search: submit form and results available")
    void canSearchForClaim(SearchConfig config) {
        String baseUrl = EnvConfig.baseUrl();
        SearchPage searchPage = new SearchPage(page).navigateTo(baseUrl);

        Assertions.assertEquals("Search for a claim", searchPage.getHeadingText());

        searchPage.searchForClaim(
                config.getProviderAccountNumber(),
                config.getSubmissionMonth(),
                config.getSubmissionYear(),
                config.getUfn(),
                config.getCrn()
        );

        boolean hasResults = searchPage.hasResults();
        Assertions.assertEquals(config.isExpectedResults(), hasResults, "Results presence should match expected.");
    }
}


