package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.HomePageController;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

import java.util.List;
import java.util.Map;

@ActiveProfiles("local")
@WebMvcTest(HomePageController.class)
@Import(LocalSecurityConfig.class)
class IndexViewTest extends ViewTestBase {

    IndexViewTest() {
        super("/");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Search for a claim");

        assertPageHasHeading(doc, "Search for a claim");

        assertPageHasHint(doc, "search-hint", "Enter a provider account number and at least one other field to search.");

        assertPageHasTextInput(doc, "provider-account-number", "Provider account number");

        assertPageHasDateInput(doc, "Submission date");

        assertPageHasHint(doc, "submission-date-hint", "For example, 3 2007");

        assertPageHasTextInput(doc, "submission-date-month", "Month");

        assertPageHasTextInput(doc, "submission-date-year", "Year");

        assertPageHasTextInput(doc, "reference-number", "UFN or CRN");

        assertPageHasActiveServiceNavigationItem(doc, "Search");
    }

    @Test
    void testPageWithPagination() throws Exception {
        ClaimResponse claim1 = new ClaimResponse();
        claim1.setUniqueFileNumber("290419/711");
        claim1.setCaseReferenceNumber("EF/4560/2018/4364683");
        claim1.setClientSurname("Doe");
        claim1.setCaseStartDate("2019-04-29");
        claim1.setScheduleReference("0X766A/2018/02");

        ClaimResponse claim2 = new ClaimResponse();
        claim2.setUniqueFileNumber("101117/712");
        claim2.setCaseReferenceNumber("EF/4439/2017/3078011");
        claim2.setClientSurname("White");
        claim2.setCaseStartDate("2017-11-10");
        claim2.setScheduleReference("0X766A/2018/02");

        ClaimResponse claim3 = new ClaimResponse();
        claim3.setUniqueFileNumber("120419/714");
        claim3.setCaseReferenceNumber("DM/4604/2019/4334501");
        claim3.setClientSurname("Stevens");
        claim3.setCaseStartDate("2019-04-12");
        claim3.setScheduleReference("0X766A/2018/02");

        ClaimResultSet result = new ClaimResultSet();
        result.setContent(List.of(claim1, claim2, claim3));
        SearchResultViewModel viewModel = new SearchResultViewModel(result);

        Map<String, Object> variables = Map.of(
            "viewModel", viewModel
        );

        Document doc = renderDocument(variables);

        assertPageHasH2(doc, "3 search results");

        assertPageHasTable(doc);

        assertPageHasPagination(doc);
    }
}
