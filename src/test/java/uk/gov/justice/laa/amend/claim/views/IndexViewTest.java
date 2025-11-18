package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.HomePageController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.viewmodels.BaseClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultView;

import java.util.List;
import java.util.Map;

@ActiveProfiles("local")
@WebMvcTest(HomePageController.class)
@Import(LocalSecurityConfig.class)
class IndexViewTest extends ViewTestBase {

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimResultMapper claimResultMapper;

    @MockitoBean
    private ClaimMapper claimMapper;

    IndexViewTest() {
        super("/");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Search for a claim");

        assertPageHasHeading(doc, "Search for a claim");

        assertPageHasHint(doc, "search-hint", "Enter at least a provider account number to search.");

        assertPageHasTextInput(doc, "provider-account-number", "Provider account number");

        assertPageHasDateInput(doc, "Submission date");

        assertPageHasHint(doc, "submission-date-hint", "For example, 3 2007");

        assertPageHasTextInput(doc, "submission-date-month", "Month");

        assertPageHasTextInput(doc, "submission-date-year", "Year");

        assertPageHasTextInput(doc, "unique-file-number", "UFN");

        assertPageHasTextInput(doc, "case-reference-number", "CRN");

        assertPageHasActiveServiceNavigationItem(doc, "Search");
    }

    @Test
    void testPageWithPagination() throws Exception {
        // TODO - tests can be enhanced to test the values being rendered on the page
        CivilClaimDetails claim1 = new CivilClaimDetails();
        CivilClaimDetails claim2 = new CivilClaimDetails();
        CivilClaimDetails claim3 = new CivilClaimDetails();

        List<BaseClaimView<Claim>> claims = List.of(
            new ClaimView(claim1),
            new ClaimView(claim2),
            new ClaimView(claim3)
        );

        SearchResultView viewModel = new SearchResultView();
        viewModel.setClaims(claims);
        Pagination pagination = new Pagination(10, 10, 1, "/");
        viewModel.setPagination(pagination);

        Map<String, Object> variables = Map.of("viewModel", viewModel);
        Document doc = renderDocument(variables);

        assertPageHasH2(doc, "10 search results");

        assertPageHasTable(doc);

        assertPageHasPagination(doc);
    }

    @Test
    void testPageWithErrors() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("providerAccountNumber", "!");
        params.add("submissionDateMonth", "!");
        params.add("submissionDateYear", "!");
        params.add("uniqueFileNumber", "!");
        params.add("caseReferenceNumber", "!");

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorSummary(doc,
            "provider-account-number",
            "submission-date-month", // date errors get combined
            "unique-file-number",
            "case-reference-number"
        );
    }

    @Test
    void testPageWithNoResultsFound() throws Exception {
        SearchResultView viewModel = new SearchResultView();
        viewModel.setClaims(List.of());
        Pagination pagination = new Pagination(10, 10, 1, "/");
        viewModel.setPagination(pagination);

        Map<String, Object> variables = Map.of("viewModel", viewModel);

        Document doc = renderDocument(variables);

        assertPageHasH2(doc, "There are no results that match the search criteria");

        assertPageHasContent(doc, "Check you've entered the correct details");
    }
}
