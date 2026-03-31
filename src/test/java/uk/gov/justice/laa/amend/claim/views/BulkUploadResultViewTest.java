package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;

import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.BulkUploadController;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;

@WebMvcTest(BulkUploadController.class)
public class BulkUploadResultViewTest extends ViewTestBase {

    @MockitoBean
    private BulkUploadService bulkUploadService;

    BulkUploadResultViewTest() {
        this.mapping = "/bulk-upload/result";
    }

    @Test
    void testSuccessResultIsRendered() throws Exception {
        when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

        var successReason = "success reason";
        var result = new BulkUploadResult(SUCCESS, List.of(successReason));

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
    void testErrorResultIsRendered() throws Exception {
        when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

        var errorReason1 = "error reason 1";
        var errorReason2 = "error reason 2";
        var result = new BulkUploadResult(PARSING_FAILURE, List.of(errorReason1, errorReason2));

        Document doc = renderDocument(Map.of("result", result));

        List<List<Element>> summaryList = getFirstSummaryList(doc);
        Assertions.assertEquals(2, summaryList.size());
        assertCellContainsText(summaryList.get(0).get(1), errorReason1);
        assertCellContainsText(summaryList.get(1).get(1), errorReason2);

        assertPageHasLink(doc, "upload-another-file", "Upload another file", "/bulk-upload");
        assertPageHasLink(doc, "back-to-search", "Back to search", "/");
    }
}
