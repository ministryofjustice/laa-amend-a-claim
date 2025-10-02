package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest
class NotAuthorisedViewTest extends ViewTestBase {

    NotAuthorisedViewTest() {
        super("not-authorised");
    }

    @Test
    void testPage() {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "You are not authorised");

        assertPageHasHeading(doc, "You are not authorised");

        assertPageHasNoActiveServiceNavigationItems(doc);
    }
}
