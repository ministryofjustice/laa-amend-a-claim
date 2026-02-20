package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ChangeAssessedTotalsController;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

@ActiveProfiles("local")
@WebMvcTest(ChangeAssessedTotalsController.class)
@Import(LocalSecurityConfig.class)
class ChangeAssessedTotalViewTest extends ViewTestBase {

    ChangeAssessedTotalViewTest() {
        this.mapping = String.format("/submissions/%s/claims/%s/assessed-totals", submissionId, claimId);
    }

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        claim.setAssessmentOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Assess total claim value");

        assertPageHasHeading(doc, "Assess total claim value");

        assertPageHasHint(doc, "assessed-total-vat-hint", "Enter the amount of assessed VAT for the claim");
        assertPageHasHint(doc, "assessed-total-incl-vat-hint", "Enter the total assessed value of the claim");

        assertPageHasTextInput(doc, "assessed-total-vat", "Assessed total VAT");
        assertPageHasTextInput(doc, "assessed-total-incl-vat", "Assessed total including VAT");
    }

    @Test
    void testPageWithErrors() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("value", "-1");

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorSummary(doc, "assessed-total-vat", "assessed-total-incl-vat");
    }
}
