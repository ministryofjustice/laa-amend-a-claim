package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.exceptions.ErrorPageController;
import uk.gov.justice.laa.amend.claim.factories.ReferenceNumberFactory;

@ActiveProfiles("local")
@WebMvcTest(ErrorPageController.class)
@Import(LocalSecurityConfig.class)
public class NotFoundViewTest extends ViewTestBase {

    @MockitoBean
    private ReferenceNumberFactory referenceNumberFactory;

    NotFoundViewTest() {
        super("/error");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderErrorPage(404);

        assertPageHasTitle(doc, "Page not found");

        assertPageHasHeading(doc, "Page not found");

        assertPageHasContent(doc, "If you entered a web address please check it was correct.");

        assertPageHasContent(
                doc,
                "Alternatively return to the Amend a claim for contracted work and extension homepage and try again.");

        assertPageHasLink(doc, "homepage", "Amend a claim for contracted work and extension homepage", "/");
    }
}
