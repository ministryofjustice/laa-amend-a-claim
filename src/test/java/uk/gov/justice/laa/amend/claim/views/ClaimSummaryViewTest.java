package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ClaimSummaryPageController;
import uk.gov.justice.laa.amend.claim.controllers.NotAuthorisedController;

@ActiveProfiles("local")
@WebMvcTest(ClaimSummaryPageController.class)
@Import(LocalSecurityConfig.class)
class ClaimSummaryViewTest extends ViewTestBase {

    ClaimSummaryViewTest() {
        super("/submissions/submissionId/claims/claimId");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Claim summary");

        assertPageHasHeading(doc, "Claim summary");

        assertPageHasNoActiveServiceNavigationItems(doc);
    }
}
