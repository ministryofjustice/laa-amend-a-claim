package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.BulkUploadResultController;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;

@ActiveProfiles("local")
@WebMvcTest(BulkUploadResultController.class)
@Import(LocalSecurityConfig.class)
public class BulkUploadResultViewTest extends ViewTestBase {

    private static final UUID USER_ID = UUID.fromString(DummyUserSecurityService.USER_ID);

    BulkUploadResultViewTest() {
        this.mapping = "/bulk-upload-result";
    }

    @Test
    void testSuccessResultIsRendered() throws Exception {
        when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

        var successReason = "success reason";
        var result = new BulkUploadResult(SUCCESS, List.of(successReason));

        Document doc = renderDocument(Map.of("result", result));

        assertPageHasTitle(doc, "Bulk upload of escape claim assessments");

        assertPageHasHeading(doc, "Your file was uploaded successfully");

        assertPageHasPanel(doc);

        assertPageHasContent(doc, "success reason");

        assertPageHasLink(doc, "back-to-upload", "Back to upload", "/bulk-upload");
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

        assertPageHasLink(doc, "back-to-upload", "Back to upload", "/bulk-upload");
        assertPageHasLink(doc, "back-to-search", "Back to search", "/");
    }
}
