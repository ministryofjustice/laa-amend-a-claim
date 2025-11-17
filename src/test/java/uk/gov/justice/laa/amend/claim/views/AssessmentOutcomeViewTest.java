package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.AssessmentOutcomeController;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;


@ActiveProfiles("local")
@WebMvcTest(AssessmentOutcomeController.class)
@Import(LocalSecurityConfig.class)
class AssessmentOutcomeViewTest extends ViewTestBase {

    @MockitoBean
    private AssessmentService assessmentService;

    AssessmentOutcomeViewTest() {
        super("/submissions/submissionId/claims/claimId/assessment-outcome");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Assessment Outcome");

        assertPageHasHeading(doc, "Assessment Outcome");
        assertPageHasSecondaryButton(doc);
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);

    }

    @Test
    void testPageErrors() throws Exception {
        Document doc = renderRedirect("/submissions/123/claims/456/assessment-outcome");

        assertPageHasTitle(doc, "Assessment Outcome");

        assertPageHasHeading(doc, "Assessment Outcome");
        assertPageHasSecondaryButton(doc);
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);

        assertPageHasErrorSummary(doc,
                "assessment-outcome",
                "liability-for-vat"
        );
    }

}
