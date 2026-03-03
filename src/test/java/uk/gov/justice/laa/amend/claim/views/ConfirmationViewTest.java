package uk.gov.justice.laa.amend.claim.views;

import java.util.UUID;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ConfirmationController;

@ActiveProfiles("local")
@WebMvcTest(ConfirmationController.class)
@Import(LocalSecurityConfig.class)
class ConfirmationViewTest extends ViewTestBase {

    private final UUID assessmentId;

    ConfirmationViewTest() {
        assessmentId = UUID.randomUUID();
        this.mapping = String.format("/submissions/%s/claims/%s/assessments/%s", submissionId, claimId, assessmentId);
    }

    @Test
    void testPage() throws Exception {
        session.setAttribute("assessmentId", assessmentId);
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Assessment complete");

        assertPageHasHeading(doc, "Assessment complete");

        assertPageHasPanel(doc);

        assertPageHasContent(doc, "Your changes have been submitted");

        assertPageHasLink(doc, "go-to-search", "Go to search", "/");

        assertPageHasLink(
                doc,
                "view-assessed-claim",
                "View assessed claim",
                String.format("/submissions/%s/claims/%s", submissionId, claimId));
    }
}
