package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ConfirmationController;

@ActiveProfiles("local")
@WebMvcTest(ConfirmationController.class)
@Import(LocalSecurityConfig.class)
class ConfirmationViewTest extends ViewTestBase {

    ConfirmationViewTest() {
        super("/submissions/submissionId/claims/claimId/confirmation");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Adjustments submitted");

        assertPageHasHeading(doc, "Adjustments submitted");

        assertPageHasPanel(doc);

        assertPageHasContent(doc, "Your changes may take a few minutes to take effect");

        assertPageHasLink(doc, "go-to-search", "Go to search");
    }
}
