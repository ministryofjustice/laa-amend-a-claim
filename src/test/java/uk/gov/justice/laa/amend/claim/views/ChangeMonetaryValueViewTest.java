package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ChangeMonetaryValueController;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;

@ActiveProfiles("local")
@WebMvcTest(ChangeMonetaryValueController.class)
@Import(LocalSecurityConfig.class)
class ChangeMonetaryValueViewTest extends ViewTestBase {

    private String claimId = "claimId";

    ChangeMonetaryValueViewTest() {
        super("/submissions/submissionId/claims/claimId/profit-costs");
    }

    @Test
    void testPage() throws Exception {
        Claim claim = new CivilClaimDetails();
        session.setAttribute(claimId, claim);
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Amend profit costs");

        assertPageHasHeading(doc, "Amend profit costs");

        assertPageHasHint(doc, "value-hint", "Enter the amended value for the providers' profit costs, excluding VAT.");

        assertPageHasTextInput(doc, "value", "Amend profit costs");
    }

    @Test
    void testPageWithErrors() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("value", "-1");

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorSummary(doc, "value");
    }
}
