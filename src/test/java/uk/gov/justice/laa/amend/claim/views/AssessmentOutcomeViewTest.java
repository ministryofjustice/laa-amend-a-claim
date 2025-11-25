package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
        assertPageHasPrimaryButton(doc, "Continue");
        assertPageHasSecondaryButton(doc, "Cancel");
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);

    }

    @Test
    void testPageErrors() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("assessmentOutcome", "");
        params.add("liabilityForVat", "");

        Document doc = renderDocumentWithErrors(params);

        assertPageHasTitle(doc, "Assessment Outcome");

        assertPageHasHeading(doc, "Assessment Outcome");
        assertPageHasPrimaryButton(doc, "Continue");
        assertPageHasSecondaryButton(doc, "Cancel");
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);

        assertPageHasErrorSummary(doc,
                "assessment-outcome",
                "liability-for-vat"
        );
    }

}
