package uk.gov.justice.laa.amend.claim.views;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.controllers.HomePageController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.enums.DerivedClaimStatus;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.viewmodels.BaseClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultView;

@WebMvcTest(HomePageController.class)
class IndexViewTest extends ViewTestBase {

  @MockitoBean private ClaimService claimService;

  @MockitoBean private ClaimResultMapper claimResultMapper;

  @MockitoBean private ClaimMapper claimMapper;

  IndexViewTest() {
    this.mapping = "/";
  }

  @Test
  void testPage() {
    Document doc = renderDocument();

    assertPageHasTitle(doc, "Search for a claim");

    assertPageHasHeading(doc, "Search for a claim");

    assertPageHasHint(doc, "search-hint", "Enter at least an office account number to search.");

    assertPageHasLabel(doc, "office-code", "Office account number");

    assertPageHasHint(doc, "office-code-hint", "For example, 0P322F");

    assertPageHasDateInput(doc, "Submission period");

    assertPageHasHint(doc, "submission-date-hint", "For example, 3 2007");

    assertPageHasLabel(doc, "submission-date-month", "Month");

    assertPageHasLabel(doc, "submission-date-year", "Year");

    assertPageHasLabel(doc, "unique-file-number", "Unique file number (UFN)");

    assertPageHasHint(doc, "unique-file-number-hint", "For example, 120223/001");

    assertPageHasLabel(doc, "case-reference-number", "Case reference number (CRN)");

    assertPageHasLabel(doc, "area-of-law", "Area of law");

    assertPageHasLabel(doc, "escape-case", "Escape case");

    assertPageHasActiveServiceNavigationItem(doc, "Search");
  }

  @Test
  void testPageWithPagination() {
    int currentPage = 2;
    String url = String.format("/?page=%d", currentPage);
    int numberOfResultsPerPage = 10;
    this.mapping = url;

    ClaimView claimViewModel = new ClaimView(MockClaimsFunctions.createMockCivilClaim());
    List<BaseClaimView<Claim>> claims =
        new ArrayList<>(Collections.nCopies(numberOfResultsPerPage, claimViewModel));

    Pagination pagination = new Pagination(20, numberOfResultsPerPage, currentPage, url);
    SearchResultView viewModel = new SearchResultView(claims, pagination);

    Map<String, Object> variables = Map.of("viewModel", viewModel);
    Document doc = renderDocument(variables);

    assertH2Exists(doc, "20 search results");

    assertPageHasTable(doc);

    assertPageHasPagination(doc);

    Elements headers = getTableHeaders(doc);

    assertTableHeaderIsSortable(
        headers.get(0), "none", "Client last name", "/?page=1&sort=client_surname,asc");
    assertTableHeaderIsSortable(
        headers.get(1), "ascending", "UFN", "/?page=1&sort=unique_file_number,desc");
    assertTableHeaderIsSortable(
        headers.get(2), "none", "CRN", "/?page=1&sort=case_reference_number,asc");
    assertTableHeaderIsSortable(
        headers.get(3), "none", "Submission period", "/?page=1&sort=submission_period,asc");
    assertTableHeaderIsSortable(
        headers.get(4), "none", "Category of law", "/?page=1&sort=category_of_law,asc");
    assertTableHeaderIsNotSortable(headers.get(5), "Escape case");
    assertTableHeaderIsSortable(headers.get(6), "none", "Status", "/?page=1&sort=status,asc");
  }

  @Test
  void testPageWithOneResult() {
    ClaimView claimViewModel = new ClaimView(MockClaimsFunctions.createMockCivilClaim());
    List<BaseClaimView<Claim>> claims = List.of(claimViewModel);

    Pagination pagination = new Pagination(1, 10, 1, "/");
    SearchResultView viewModel = new SearchResultView(claims, pagination);

    Map<String, Object> variables = Map.of("viewModel", viewModel);
    Document doc = renderDocument(variables);

    assertH2Exists(doc, "1 search result");
  }

  private static Stream<Arguments> derivedClaimStatusTags() {
    return Stream.of(
        Arguments.of(DerivedClaimStatus.ACCEPTED, "Accepted", "govuk-tag--green"),
        Arguments.of(DerivedClaimStatus.ASSESSED, "Assessed", "govuk-tag--blue"),
        Arguments.of(DerivedClaimStatus.AMENDED, "Amended", "govuk-tag--yellow"),
        Arguments.of(DerivedClaimStatus.VOIDED, "Voided", "govuk-tag--red"));
  }

  @ParameterizedTest
  @MethodSource("derivedClaimStatusTags")
  void testPageShowsStatusTagForDerivedClaimStatus(
      DerivedClaimStatus derivedClaimStatus, String expectedTagText, String expectedTagClass) {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setDerivedClaimStatus(derivedClaimStatus);

    List<BaseClaimView<Claim>> claims = List.of(new ClaimView(claim));
    var pagination = new Pagination(1, 10, 1, "/");
    var viewModel = new SearchResultView(claims, pagination);

    var doc = renderDocument(Map.of("viewModel", viewModel));

    var row = doc.selectFirst("tbody.govuk-table__body tr.govuk-table__row");
    assertThat(row).isNotNull();
    var cells = row.select("td.govuk-table__cell");
    var statusTag = cells.get(6).selectFirst("strong.govuk-tag");
    assertThat(statusTag).isNotNull();
    assertThat(statusTag.text()).isEqualTo(expectedTagText);
    assertThat(statusTag.classNames()).contains(expectedTagClass);
  }

  @Test
  void testPageWithErrors() {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("officeCode", "!");
    params.add("submissionDateMonth", "!");
    params.add("submissionDateYear", "!");
    params.add("uniqueFileNumber", "!");
    params.add("caseReferenceNumber", "!");

    Document doc = renderDocumentWithErrors(params);

    assertPageHasErrorSummary(
        doc,
        "office-code",
        "submission-date-month", // date errors get combined
        "unique-file-number",
        "case-reference-number");
  }

  @Test
  void testPageWithNoResultsFound() {
    Pagination pagination = new Pagination(10, 10, 1, "/");
    SearchResultView viewModel = new SearchResultView(List.of(), pagination);

    Map<String, Object> variables = Map.of("viewModel", viewModel);

    Document doc = renderDocument(variables);

    assertH2Exists(doc, "There are no results that match the search criteria");

    assertPageHasContent(doc, "Check you've entered the correct details");
  }

  @Test
  void testPageAfterDiscard() {
    Map<String, Object> variables = Map.of("discarded", true);

    Document doc = renderDocument(variables);

    assertPageHasSuccessBanner(doc, "You discarded the assessment");
  }

  @Test
  void testPageAfterVoid() {
    Map<String, Object> variables = Map.of("voided", true);

    Document doc = renderDocument(variables);

    assertPageHasSuccessBanner(doc, "You voided the claim");
  }
}
