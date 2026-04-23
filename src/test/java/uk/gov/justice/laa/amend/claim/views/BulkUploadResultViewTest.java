package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadError;
import uk.gov.justice.laa.amend.claim.controllers.BulkUploadController;
import uk.gov.justice.laa.amend.claim.models.BulkUploadAssessmentSummary;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;

@WebMvcTest(BulkUploadController.class)
public class BulkUploadResultViewTest extends ViewTestBase {

  @MockitoBean private BulkUploadService bulkUploadService;

  BulkUploadResultViewTest() {
    this.mapping = "/bulk-upload/result";
  }

  @Test
  void testSuccessResultIsRendered() throws Exception {
    when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

    var result = BulkUploadResult.success(List.of());

    Document doc = renderDocument(Map.of("result", result));

    assertPageHasTitle(doc, "Bulk upload of civil escape claim assessments");

    assertPageHasHeading(doc, "Your file was uploaded successfully");

    assertPageHasActiveServiceNavigationItem(doc, "Bulk upload");

    assertPageHasPanel(doc);

    assertPageHasContent(doc, "0 assessments uploaded");

    assertPageHasLink(doc, "upload-another-file", "Upload another file", "/bulk-upload");
    assertPageHasLink(doc, "back-to-search", "Back to search", "/");
  }

  @Test
  void testErrorResultIsRendered() throws Exception {
    when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

    var errorReason1 = "error reason 1";
    var errorReason2 = "error reason 2";
    var result =
        BulkUploadResult.failure(
            PARSING_FAILURE,
            List.of(new BulkUploadError(null, errorReason1), new BulkUploadError(3, errorReason2)));
    Document doc = renderDocument(Map.of("result", result));

    Elements rows = doc.select("#error-summary-list tbody tr");
    Assertions.assertEquals(2, rows.size());
    Assertions.assertEquals(errorReason1, rows.get(0).select("td").get(1).text());
    Assertions.assertEquals(errorReason2, rows.get(1).select("td").get(1).text());

    assertPageHasLink(doc, "upload-another-file", "Upload another file", "/bulk-upload");
    assertPageHasLink(doc, "back-to-search", "Back to search", "/");
  }

  @Test
  void testSuccessResultShowsAssessmentsTable() throws Exception {
    when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

    var testSubmissionId = UUID.randomUUID();
    var testClaimId = UUID.randomUUID();
    var summary =
        new BulkUploadAssessmentSummary(
            testSubmissionId,
            testClaimId,
            "010101/001001",
            "0P0001",
            OutcomeType.REDUCED,
            new java.math.BigDecimal("1234.56"));
    var result = BulkUploadResult.success(List.of(summary));

    Document doc = renderDocument(Map.of("result", result));

    assertPageHasTable(doc);
    assertPageHasContent(doc, "1 assessments uploaded");
    assertPageHasContent(doc, "010101/001001");
    assertPageHasContent(doc, "0P0001");
    assertPageHasContent(doc, "Reduced");
    assertPageHasContent(doc, "£1,234.56");

    Element viewLink = doc.selectFirst("table.govuk-table a.govuk-link");
    Assertions.assertNotNull(viewLink);
    Assertions.assertEquals("View claim", viewLink.text());
    Assertions.assertEquals(
        "/submissions/" + testSubmissionId + "/claims/" + testClaimId, viewLink.attr("href"));

    // Assert column order: Office Code, UFN, Assessment Result, Allowed Total, View Claim link
    Elements headers = doc.select("table.govuk-table thead th");
    Assertions.assertEquals("Office code", headers.get(0).text());
    Assertions.assertEquals("UFN", headers.get(1).text());
    Assertions.assertEquals("Assessment result", headers.get(2).text());
    Assertions.assertEquals("Allowed total (incl. VAT)", headers.get(3).text());

    // Assert link is in the last column
    Element lastCell = doc.selectFirst("table.govuk-table tbody tr td:last-child");
    Assertions.assertNotNull(lastCell.selectFirst("a.govuk-link"));
  }

  @Test
  void testSuccessResultWithNoAssessmentsDoesNotShowTable() throws Exception {
    when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

    var result = BulkUploadResult.success(List.of());

    Document doc = renderDocument(Map.of("result", result));

    assertPageHasPanel(doc);
    Assertions.assertTrue(doc.getElementsByClass("govuk-table").isEmpty());
  }
}
