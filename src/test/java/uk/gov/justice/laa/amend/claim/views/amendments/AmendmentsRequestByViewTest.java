package uk.gov.justice.laa.amend.claim.views.amendments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.amendments.AmendmentRequestByController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.forms.validators.RequestByFormValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;

@WebMvcTest(AmendmentRequestByController.class)
public class AmendmentsRequestByViewTest extends AmendmentsBaseTest {

  @MockitoBean private SystemReferenceService systemReferenceService;
  @MockitoBean private RequestByFormValidator requestByFormValidator;

  AmendmentsRequestByViewTest() {
    this.mapping =
        "/submissions/%s/claims/%s/amendments/requested-by".formatted(submissionId, claimId);
  }

  @Test
  void testPage() {
    when(featureFlagsConfig.getIsClaimAmendmentEnabled()).thenReturn(true);
    when(requestByFormValidator.supports(any())).thenReturn(true);
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    var forms = createRequestByForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    Document doc = renderDocument();

    assertPageHasTitle(doc, "Amend claim details");

    assertPageHasHeading(doc, "Who requested the amendment?");

    assertPageHasActiveServiceNavigationItem(doc, "Amendments");

    assertPageHasContent(doc, "Select who requested the amendment");

    assertPageHasLink(
        doc, "bulk-upload-example-link", "Download example CSV file", "/bulk-upload/example");

    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasLink(doc, "back-to-search", "Back to search", "/");
  }

  private static AmendmentForms createRequestByForms(ClaimDetails claimDetails) {
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm())
        .requestedBy(new RequestedByForm())
        .build();
  }
}
