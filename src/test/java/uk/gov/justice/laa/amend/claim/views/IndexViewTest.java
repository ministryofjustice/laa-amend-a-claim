package uk.gov.justice.laa.amend.claim.views;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.HomePageController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimType;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ActiveProfiles("local")
@WebMvcTest(HomePageController.class)
@Import(LocalSecurityConfig.class)
class IndexViewTest extends ViewTestBase {

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimResultMapper claimResultMapper;

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

        assertPageHasTextInput(doc, "reference-number", "UFN or CRN");

        assertPageHasActiveServiceNavigationItem(doc, "Search");
    }

    @Test
    void testPageWithPagination() throws Exception {
        // Create test claims
        List<ClaimResponse> claims = List.of(createTestClaim("290419/711", "EF/4560/2018/4364683", "Doe", "2019-04-29", "0X766A/2018/01"), createTestClaim("101117/712", "EF/4439/2017/3078011", "White", "2017-11-10", "0X766A/2018/02"), createTestClaim("120419/714", "DM/4604/2019/4334501", "Stevens", "2019-04-12", "0X766A/2018/03"));

        ClaimResultSet result = new ClaimResultSet();
        result.setContent(claims);
        SearchResultViewModel viewModel = new SearchResultViewModel();
        viewModel.setClaims(claims.stream().map(this::convertToClaimDto).collect(Collectors.toList()));
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
        params.add("referenceNumber", "!");

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorSummary(doc,
            "provider-account-number",
            "submission-date-month",
            "submission-date-year",
            "reference-number"
        );
    }

    @Test
    void testPageWithNoResultsFound() throws Exception {
        ClaimResultSet result = new ClaimResultSet();
        result.setContent(List.of());
        SearchResultViewModel viewModel = new SearchResultViewModel();
        viewModel.setClaims(List.of());
        Pagination pagination = new Pagination(10, 10, 1, "/");
        viewModel.setPagination(pagination);

        Map<String, Object> variables = Map.of("viewModel", viewModel);

        Document doc = renderDocument(variables);

        assertPageHasH2(doc, "There are no results that match the search criteria");

        assertPageHasContent(doc, "Check you've entered the correct details");
    }

    private Claim convertToClaimDto(ClaimResponse claimResponse) {
        Claim claim = new Claim();
        claim.setAccount(claimResponse.getScheduleReference());
        claim.setType("Unknown");
        claim.setStatus(ClaimType.FIXED);
        claim.setClientSurname(claimResponse.getClientSurname());
        claim.setCaseReferenceNumber(claimResponse.getCaseReferenceNumber());
        Assertions.assertNotNull(claimResponse.getCaseStartDate());
        var date = LocalDate.parse(claimResponse.getCaseStartDate());
        claim.setDateSubmitted(date);
        claim.setDateSubmittedForDisplay(DateTimeFormatter.ofPattern("dd MMM yyyy").format(date));
        return claim;
    }

    private @NotNull ClaimResponse createTestClaim(String s, String s1, String doe, String date, String s2) {
        ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.setCaseReferenceNumber(s);
        claimResponse.setClientSurname(doe);
        claimResponse.setCaseStartDate(date);
        claimResponse.setScheduleReference(s1);
        claimResponse.setMatterTypeCode("MatterType001");
        claimResponse.setStatus(ClaimStatus.VALID);
        claimResponse.setUniqueFileNumber(s2);
        return claimResponse;
    }
}
