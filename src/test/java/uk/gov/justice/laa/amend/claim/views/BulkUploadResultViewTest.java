package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;

import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadError;
import uk.gov.justice.laa.amend.claim.controllers.BulkUploadController;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;

@WebMvcTest(BulkUploadController.class)
public class BulkUploadResultViewTest extends ViewTestBase {

  @MockitoBean private BulkUploadService bulkUploadService;

  BulkUploadResultViewTest() {
    this.mapping = "/bulk-upload/result";
  }

  @Test
  void testSuccessResultIsRendered() {
    when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

    var successReason = "success reason";
    var result = new BulkUploadResult(SUCCESS, List.of(new BulkUploadError(null, successReason)));

    Document doc = renderDocument(Map.of("result", result));

    assertPageHasTitle(doc, "Bulk upload of civil escape claim assessments");

    assertPageHasHeading(doc, "Your file was uploaded successfully");

    assertPageHasActiveServiceNavigationItem(doc, "Bulk upload");

    assertPageHasPanel(doc);

    assertPageHasContent(doc, "success reason");

    assertPageHasLink(doc, "upload-another-file", "Upload another file", "/bulk-upload");
    assertPageHasLink(doc, "back-to-search", "Back to search", "/");
  }

  @Test
  void testErrorResultIsRendered() {
    when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

    var errorReason1 = "error reason 1";
    var errorReason2 = "error reason 2";
    var result =
        new BulkUploadResult(
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
}
