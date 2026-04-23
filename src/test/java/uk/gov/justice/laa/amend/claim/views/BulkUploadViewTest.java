package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.controllers.BulkUploadController;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;

@WebMvcTest(BulkUploadController.class)
public class BulkUploadViewTest extends ViewTestBase {

  @MockitoBean private BulkUploadService<BulkUploadCivilClaim> bulkUploadService;

  BulkUploadViewTest() {
    this.mapping = "/bulk-upload";
  }

  @Test
  void testPage() {
    when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

    Document doc = renderDocument();

    assertPageHasTitle(doc, "Bulk upload of civil escape claim assessments");

    assertPageHasHeading(doc, "Bulk upload of civil escape claim assessments");

    assertPageHasActiveServiceNavigationItem(doc, "Bulk upload");

    assertPageHasContent(
        doc, "Upload a CSV file containing the escape claim assessment data for multiple claims");
    assertPageHasContent(doc, "Upload a file");
    assertPageHasContent(doc, "You can upload a CSV file up to 10MB");

    assertPageHasLink(
        doc, "bulk-upload-example-link", "Download example CSV file", "/bulk-upload/example");

    assertPageHasPrimaryButton(doc, "Upload");
    assertPageHasLink(doc, "back-to-search", "Back to search", "/");
  }
}
