package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.VoidConfirmationController;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@ActiveProfiles("local")
@WebMvcTest(VoidConfirmationController.class)
@Import(LocalSecurityConfig.class)
class VoidConfirmationViewTest extends ViewTestBase {

    VoidConfirmationViewTest() {
        this.mapping = String.format("/submissions/%s/claims/%s/void", submissionId, claimId);
    }

    @Test
    void testPageForCivilClaim() throws Exception {
        when(featureFlagsConfig.getIsVoidingEnabled()).thenReturn(true);

        var claim = MockClaimsFunctions.createMockCivilClaim();
        this.claim = claim;
        claim.setSubmissionId(submissionId.toString());
        claim.setClaimId(claimId.toString());
        claim.setFeeCodeDescription("FCD");
        claim.setUniqueFileNumber("UFN");
        claim.setUniqueClientNumber("UCN");
        claim.setProviderAccountNumber("0P322F");
        claim.setProviderName("Provider Name");
        claim.setClientForename("John");
        claim.setClientSurname("Doe");
        claim.setSubmittedDate(LocalDateTime.of(2020, 6, 15, 9, 30, 0));
        claim.setCategoryOfLaw("TEST");

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Confirm you want to void this claim");

        assertPageHasHeading(doc, "Confirm you want to void this claim");

        assertPageDoesNotHaveBackLink(doc);

        List<List<Element>> summaryList = getFirstSummaryList(doc);
        Assertions.assertEquals(8, summaryList.size());
        assertSummaryListRowContainsValues(summaryList.getFirst(), "Client name", "John Doe");
        assertSummaryListRowContainsValues(summaryList.get(1), "Unique file number (UFN)", "UFN");
        assertSummaryListRowContainsValues(summaryList.get(2), "Unique client number (UCN)", "UCN");
        assertSummaryListRowContainsValues(summaryList.get(3), "Provider name", "Provider Name");
        assertSummaryListRowContainsValues(summaryList.get(4), "Provider account number", "0P322F");
        assertSummaryListRowContainsValues(summaryList.get(5), "Date submitted", "15 June 2020 at 09:30:00");
        assertSummaryListRowContainsValues(summaryList.get(6), "Category of law", "TEST");
        assertSummaryListRowContainsValues(summaryList.get(7), "Fee code description", "FCD");
    }

    @Test
    void testPageForCrimeClaim() throws Exception {
        when(featureFlagsConfig.getIsVoidingEnabled()).thenReturn(true);

        var claim = MockClaimsFunctions.createMockCrimeClaim();
        this.claim = claim;
        claim.setSubmissionId(submissionId.toString());
        claim.setClaimId(claimId.toString());
        claim.setFeeCodeDescription("FCD");
        claim.setUniqueFileNumber("UFN");
        claim.setProviderAccountNumber("0P322F");
        claim.setProviderName("Provider Name");
        claim.setClientForename("John");
        claim.setClientSurname("Doe");
        claim.setSubmittedDate(LocalDateTime.of(2020, 6, 15, 9, 30, 0));
        claim.setCategoryOfLaw("TEST");

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Confirm you want to void this claim");

        assertPageHasHeading(doc, "Confirm you want to void this claim");

        assertPageDoesNotHaveBackLink(doc);

        List<List<Element>> summaryList = getFirstSummaryList(doc);
        Assertions.assertEquals(7, summaryList.size());
        assertSummaryListRowContainsValues(summaryList.getFirst(), "Client name", "John Doe");
        assertSummaryListRowContainsValues(summaryList.get(1), "Unique file number (UFN)", "UFN");
        assertSummaryListRowContainsValues(summaryList.get(2), "Provider name", "Provider Name");
        assertSummaryListRowContainsValues(summaryList.get(3), "Provider account number", "0P322F");
        assertSummaryListRowContainsValues(summaryList.get(4), "Date submitted", "15 June 2020 at 09:30:00");
        assertSummaryListRowContainsValues(summaryList.get(5), "Category of law", "TEST");
        assertSummaryListRowContainsValues(summaryList.get(6), "Fee code description", "FCD");
    }
}
