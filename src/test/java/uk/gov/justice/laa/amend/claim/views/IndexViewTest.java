package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.client.config.SearchProperties;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.HomePageController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.viewmodels.BaseClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultView;

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

    @MockitoBean
    private SearchProperties searchProperties;

    IndexViewTest() {
        this.mapping = "/";
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Search for a claim");

        assertPageHasHeading(doc, "Search for a claim");

        assertPageHasHint(doc, "search-hint", "Enter at least a provider account number to search.");

        assertPageHasTextInput(doc, "provider-account-number", "Provider account number");

        assertPageHasHint(doc, "provider-account-number-hint", "For example, 0P322F");

        assertPageHasDateInput(doc, "Submission period");

        assertPageHasHint(doc, "submission-date-hint", "For example, 3 2007");

        assertPageHasTextInput(doc, "submission-date-month", "Month");

        assertPageHasTextInput(doc, "submission-date-year", "Year");

        assertPageHasTextInput(doc, "unique-file-number", "Unique file number (UFN)");

        assertPageHasHint(doc, "unique-file-number-hint", "For example, 120223/001");

        assertPageHasTextInput(doc, "case-reference-number", "Case reference number (CRN)");

        assertPageHasActiveServiceNavigationItem(doc, "Search");
    }

    @Test
    void testPageWithPagination() throws Exception {
        int currentPage = 2;
        String url = String.format("/?page=%d", currentPage);
        int numberOfResultsPerPage = 10;
        this.mapping = url;
        when(searchProperties.isSortEnabled()).thenReturn(true);

        ClaimView claimViewModel = new ClaimView(MockClaimsFunctions.createMockCivilClaim());
        List<BaseClaimView<Claim>> claims =
                new ArrayList<>(Collections.nCopies(numberOfResultsPerPage, claimViewModel));

        SearchResultView viewModel = new SearchResultView();
        viewModel.setClaims(claims);
        Pagination pagination = new Pagination(20, numberOfResultsPerPage, currentPage, url);
        viewModel.setPagination(pagination);

        Map<String, Object> variables = Map.of("viewModel", viewModel);
        Document doc = renderDocument(variables);

        assertPageHasH2(doc, "20 search results");

        assertPageHasTable(doc);

        assertPageHasPagination(doc);

        Elements headers = getTableHeaders(doc);

        assertTableHeaderIsNotSortable(headers.get(0), "Claim");
        assertTableHeaderIsSortable(headers.get(1), "ascending", "UFN", "/?page=1&sort=uniqueFileNumber,desc");
        assertTableHeaderIsSortable(headers.get(2), "none", "CRN", "/?page=1&sort=caseReferenceNumber,asc");
        assertTableHeaderIsSortable(headers.get(3), "none", "Client surname", "/?page=1&sort=client.clientSurname,asc");
        assertTableHeaderIsSortable(
                headers.get(4), "none", "Submission period", "/?page=1&sort=submission.submissionPeriod,asc");
        assertTableHeaderIsSortable(headers.get(5), "none", "Account", "/?page=1&sort=scheduleReference,asc");
        assertTableHeaderIsSortable(
                headers.get(6), "none", "Category of law", "/?page=1&sort=calculatedFeeDetail.categoryOfLaw,asc");
        assertTableHeaderIsNotSortable(headers.get(7), "Escape case");
    }

    @Test
    void testPageWithOneResult() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        ClaimView claimViewModel = new ClaimView(MockClaimsFunctions.createMockCivilClaim());
        List<BaseClaimView<Claim>> claims = List.of(claimViewModel);

        SearchResultView viewModel = new SearchResultView();
        viewModel.setClaims(claims);
        Pagination pagination = new Pagination(1, 10, 1, "/");
        viewModel.setPagination(pagination);

        Map<String, Object> variables = Map.of("viewModel", viewModel);
        Document doc = renderDocument(variables);

        assertPageHasH2(doc, "1 search result");
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

        assertPageHasErrorSummary(
                doc,
                "provider-account-number",
                "submission-date-month", // date errors get combined
                "unique-file-number",
                "case-reference-number");
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

    @Test
    void testPageAfterDiscard() throws Exception {
        Map<String, Object> variables = Map.of("discarded", true);

        Document doc = renderDocument(variables);

        assertPageHasSuccessBanner(doc, "You discarded the assessment");
    }
}
