package uk.gov.justice.laa.amend.claim.views;

import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.AssessmentOutcomeController;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

@ActiveProfiles("local")
@WebMvcTest(AssessmentOutcomeController.class)
@Import(LocalSecurityConfig.class)
class AssessmentOutcomeViewTest extends ViewTestBase {

    @MockitoBean
    private AssessmentService assessmentService;

    AssessmentOutcomeViewTest() {
        this.mapping = String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Assessment outcome");

        assertPageHasHeading(doc, "Assessment outcome");
        assertPageHasPrimaryButton(doc, "Save changes");
        assertPageHasSecondaryLink(doc, "Cancel");
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);
        assertPageDoesNotHaveBackLink(doc);
    }

    @Test
    void testPageErrors() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("assessmentOutcome", "");
        params.add("liabilityForVat", "");

        Document doc = renderDocumentWithErrors(params);

        assertPageHasTitle(doc, "Assessment outcome");

        assertPageHasHeading(doc, "Assessment outcome");
        assertPageHasPrimaryButton(doc, "Save changes");
        assertPageHasSecondaryLink(doc, "Cancel");
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);

        assertPageHasErrorSummary(doc, "assessment-outcome", "liability-for-vat");
    }

    @Test
    void whenClaimHasAssessment_CancelRoutesToReviewAndAmend() throws Exception {
        Document doc = renderDocumentWithAssessmentOutcome(true, OutcomeType.REDUCED_TO_FIXED_FEE);

        assertPageHasTitle(doc, "Assessment outcome");

        assertPageHasHeading(doc, "Assessment outcome");
        assertPageHasPrimaryButton(doc, "Save changes");
        assertPageHasSecondaryLink(doc, "Cancel");
        assertPageCancelLinkValue(
                doc, "Cancel", String.format("/submissions/%s/claims/%s/review", submissionId, claimId));
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);
        assertPageDoesNotHaveBackLink(doc);
    }

    @Test
    void whenClaimHasNoSavedAssessment_OutcomeSelected_CancelRoutesToReview() throws Exception {
        Document doc = renderDocumentWithAssessmentOutcome(false, OutcomeType.REDUCED_TO_FIXED_FEE);

        assertPageHasTitle(doc, "Assessment outcome");

        assertPageHasHeading(doc, "Assessment outcome");
        assertPageHasPrimaryButton(doc, "Save changes");
        assertPageHasSecondaryLink(doc, "Cancel");
        assertPageCancelLinkValue(
                doc, "Cancel", String.format("/submissions/%s/claims/%s/review", submissionId, claimId));
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);
        assertPageDoesNotHaveBackLink(doc);
    }

    @Test
    void whenClaimHasNoSavedAssessment_NoOutcomeSelected_CancelRoutesToClaimDetails() throws Exception {
        Document doc = renderDocumentWithAssessmentOutcome(false, null);

        assertPageHasTitle(doc, "Assessment outcome");

        assertPageHasHeading(doc, "Assessment outcome");
        assertPageHasPrimaryButton(doc, "Save changes");
        assertPageHasSecondaryLink(doc, "Cancel");
        assertPageCancelLinkValue(doc, "Cancel", String.format("/submissions/%s/claims/%s", submissionId, claimId));
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);
        assertPageDoesNotHaveBackLink(doc);
    }

    private Document renderDocumentWithAssessmentOutcome(Boolean hasAssessment, OutcomeType outcome) throws Exception {
        claim.setHasAssessment(hasAssessment);
        claim.setAssessmentOutcome(outcome);
        return renderDocument(Map.of());
    }
}
