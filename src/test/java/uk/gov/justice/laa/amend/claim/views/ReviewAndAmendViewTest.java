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
import uk.gov.justice.laa.amend.claim.controllers.ChangeMonetaryValueController;
import uk.gov.justice.laa.amend.claim.controllers.ClaimReviewController;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("local")
@WebMvcTest(ClaimReviewController.class)
@Import(LocalSecurityConfig.class)
class ReviewAndAmendViewTest extends ViewTestBase {

    @MockitoBean
    private AssessmentService assessmentService;

    private final String claimId = "claimId";

    ReviewAndAmendViewTest() {
        super("/submissions/submissionId/claims/claimId/review");
    }

    @Test
    void testPage() throws Exception {
        Claim claim = new CivilClaimDetails();
        session.setAttribute(claimId, claim);
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");
    }

    @Test
    void testPageWithErrors() throws Exception {
        Claim claim = new CivilClaimDetails();
        session.setAttribute(claimId, claim);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        when(assessmentService.submitAssessment(any(), any()))
            .thenThrow(WebClientResponseException.InternalServerError.class);

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorAlert(doc);
    }
}
