package uk.gov.justice.laa.amend.claim.views;

import jakarta.servlet.http.HttpSession;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ClaimSummaryController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("local")
@WebMvcTest(ClaimSummaryController.class)
@Import(LocalSecurityConfig.class)
class ClaimSummaryViewTest extends ViewTestBase {

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimResultMapper claimResultMapper;

    @MockitoBean
    private HttpSession session;

    ClaimSummaryViewTest() {
        super("/submissions/submissionId/claims/claimId");
    }

    @Test
    void testPage() throws Exception {
        Claim claim = new Claim();
        claim.setEscaped(true);
        claim.setCategoryOfLaw("AAP");
        claim.setFeeScheme("CCS");
        claim.setMatterTypeCode("IMLB:IOUT");
        claim.setProviderAccountNumber("0P322F");
        claim.setClientForename("John");
        claim.setClientSurname("Doe");
        claim.setCaseStartDate(LocalDate.of(2020, 1, 1));
        claim.setCaseEndDate(LocalDate.of(2020, 12, 31));
        claim.setSubmissionPeriod(YearMonth.of(2021, 1));

        when(claimService.getClaim(anyString(), anyString())).thenReturn(new ClaimResponse());
        when(claimResultMapper.mapToClaim(any())).thenReturn(claim);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim summary");

        assertPageHasHeading(doc, "Claim summary");
        assertPageHasH2(doc, "Totals");

        assertPageHasBackLink(doc);

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasSummaryList(doc);

        assertPageHasSummaryListRow(doc, "Escape case", "Yes");
        assertPageHasSummaryListRow(doc, "Type", "AAP");
        assertPageHasSummaryListRow(doc, "Fee scheme", "CCS");
        assertPageHasSummaryListRow(doc, "Legal matter code", "IMLB:IOUT");
        assertPageHasSummaryListRow(doc, "Provider Account Number", "0P322F");
        assertPageHasSummaryListRow(doc, "Client name", "John Doe");
        assertPageHasSummaryListRow(doc, "Case start date", "01 Jan 2020");
        assertPageHasSummaryListRow(doc, "Case end date", "31 Dec 2020");
        assertPageHasSummaryListRow(doc, "Date submitted", "Jan 2021");
        assertPageHasSummaryListRow(doc, "Initial reported total", "TODO");
        assertPageHasSummaryListRow(doc, "Initial authorised total", "TODO");
        assertPageHasSummaryListRow(doc, "Assessment total", "TODO");
    }

    @Test
    void testPageWhenNullClaim() throws Exception {
        when(claimService.getClaim(anyString(), anyString())).thenReturn(new ClaimResponse());
        when(claimResultMapper.mapToClaim(any())).thenReturn(null);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim summary");

        assertPageHasHeading(doc, "Claim summary");

        assertPageHasNoActiveServiceNavigationItems(doc);

        assertPageHasNoSummaryList(doc);
    }

    @Test
    void testPageWhenEmptyClaim() throws Exception {
        when(claimService.getClaim(anyString(), anyString())).thenReturn(new ClaimResponse());
        when(claimResultMapper.mapToClaim(any())).thenReturn(new Claim());

        Document doc = renderDocument();

        assertPageHasSummaryList(doc);

        assertPageHasSummaryListRow(doc, "Escape case", "No");
    }
}
