package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.NotAuthorisedController;

@ActiveProfiles("local")
@WebMvcTest(NotAuthorisedController.class)
@Import(LocalSecurityConfig.class)
class NotAuthorisedViewTest extends ViewTestBase {

    NotAuthorisedViewTest() {
        super("/not-authorised");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "You are not authorised");

        assertPageHasHeading(doc, "You are not authorised");

        assertPageHasNoActiveServiceNavigationItems(doc);
    }
}
