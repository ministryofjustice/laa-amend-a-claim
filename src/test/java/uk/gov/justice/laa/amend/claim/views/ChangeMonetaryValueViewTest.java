package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ChangeMonetaryValueController;

@ActiveProfiles("local")
@WebMvcTest(ChangeMonetaryValueController.class)
@Import(LocalSecurityConfig.class)
class ChangeMonetaryValueViewTest extends ViewTestBase {

    ChangeMonetaryValueViewTest() {
        super("/submissions/submissionId/claims/claimId/profit-costs");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Assess profit costs");

        assertPageHasHeading(doc, "Assess profit costs");

        assertPageHasHint(
                doc, "value-hint", "Enter the assessed value for the providers' profit costs, excluding VAT.");

        assertPageHasTextInput(doc, "value", "Assess profit costs");
    }

    @Test
    void testPageWithErrors() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("value", "-1");

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorSummary(doc, "value");
    }
}
