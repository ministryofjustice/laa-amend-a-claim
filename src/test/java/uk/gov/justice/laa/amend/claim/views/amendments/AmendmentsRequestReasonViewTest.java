package uk.gov.justice.laa.amend.claim.views.amendments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.amendments.AmendmentRequestReasonController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.forms.validators.RequestReasonFormValidator;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;

@WebMvcTest(AmendmentRequestReasonController.class)
public class AmendmentsRequestReasonViewTest extends AmendmentsBaseTest {

  @MockitoBean private SystemReferenceService systemReferenceService;
  @MockitoBean private RequestReasonFormValidator requestReasonFormValidator;

  AmendmentsRequestReasonViewTest() {
    this.mapping =
        "/submissions/%s/claims/%s/amendments/requested-reason".formatted(submissionId, claimId);
  }

  @Test
  void requestReasonDisplayContent() {
    when(featureFlagsConfig.getIsClaimAmendmentEnabled()).thenReturn(true);
    when(requestReasonFormValidator.supports(any())).thenReturn(true);
    when(systemReferenceService.getAmendmentRequestReason(any()))
        .thenReturn(
            Map.of(
                "code1", "Reason Label 1",
                "code2", "Reason Label 2"));

    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    var forms = createRequestByForms();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    Document doc = renderDocument();

    assertPageHasTitle(doc, "Amend claim details");

    assertPageHasHeading(doc, "Why was the amendment requested?");

    assertPageHasRadioButtons(doc, "Reason Label 1", "Reason Label 2");
    assertNoRadioSelected(doc);

    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasSecondaryLink(doc, "Cancel");
  }

  private static AmendmentForms createRequestByForms() {
    var requestedByForm = new RequestedByForm();
    requestedByForm.setRequestedBy("COURT");
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm())
        .requestedBy(requestedByForm)
        .build();
  }
}
