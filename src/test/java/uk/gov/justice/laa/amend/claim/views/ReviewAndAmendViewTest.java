package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ClaimReviewController;
import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("local")
@WebMvcTest(ClaimReviewController.class)
@Import(LocalSecurityConfig.class)
class ReviewAndAmendViewTest extends ViewTestBase {

    @MockitoBean
    private AssessmentService assessmentService;

    ReviewAndAmendViewTest() {
        super("/submissions/submissionId/claims/claimId/review");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasPrimaryButton(doc, "Submit adjustments");
        assertPageHasSecondaryButton(doc, "Discard changes");
    }

    @Test
    void testPageWithSubmissionError() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        WebClientResponseException exception = WebClientResponseException.create(500, "Something went wrong", null, null, null);

        when(assessmentService.submitAssessment(any(), any()))
            .thenThrow(exception);

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorAlert(doc);
    }

    @Test
    void testPageWithValidationError() throws Exception {
        ClaimField claimField = new ClaimField();
        claimField.setKey("profitCosts");
        claimField.setStatus(AmendStatus.NEEDS_AMENDING);
        claim.setNetProfitCost(claimField);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        Document doc = renderDocumentWithErrors(params);

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasErrorSummary(doc, "profit-costs");
    }
}
