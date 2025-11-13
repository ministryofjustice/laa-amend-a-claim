package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ConfirmationController;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.Pagination;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;

import java.util.List;
import java.util.Map;

@ActiveProfiles("local")
@WebMvcTest(ConfirmationController.class)
@Import(LocalSecurityConfig.class)
class ConfirmationViewTest extends ViewTestBase {

    ConfirmationViewTest() {
        super("/confirmation");
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
