package uk.gov.justice.laa.payments.amend.views.amendments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.payments.amend.controllers.amendments.AmendmentRequestedByController;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.payments.amend.forms.validators.RequestedByFormValidator;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.service.SystemReferenceService;

@WebMvcTest(AmendmentRequestedByController.class)
public class AmendmentsRequestedByViewTest extends AmendmentsBaseTest {

  @MockitoBean private SystemReferenceService systemReferenceService;
  @MockitoBean private RequestedByFormValidator requestedByFormValidator;

  AmendmentsRequestedByViewTest() {
    this.mapping =
        "/submissions/%s/claims/%s/amendments/requested-by".formatted(submissionId, claimId);
  }

  @Test
  void requestedByDisplayContent() {
    when(featureFlagsConfig.getIsClaimAmendmentEnabled()).thenReturn(true);
    when(requestedByFormValidator.supports(any())).thenReturn(true);
    when(systemReferenceService.getAmendmentRequestedByOptions())
        .thenReturn(createSortedReferenceMap());

    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    var forms = createRequestedByForms();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    Document doc = renderDocument();

    assertPageHasTitle(doc, "Amend claim details");

    assertPageHasHeading(doc, "Who requested the amendment?");

    assertPageHasRadioButtons(doc, "RequestedBy Label 1", "RequestedBy Label 2");
    assertThat(doc.getElementsByClass("govuk-radios__label").eachText())
        .containsExactly("RequestedBy Label 1", "RequestedBy Label 2");
    assertNoRadioSelected(doc);

    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasSecondaryLink(doc, "Cancel");
  }

  private Map<String, String> createSortedReferenceMap() {
    var sortedMap = new LinkedHashMap<String, String>();
    sortedMap.put("code2", "RequestedBy Label 1");
    sortedMap.put("code1", "RequestedBy Label 2");
    return sortedMap;
  }

  private static AmendmentForms createRequestedByForms() {
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm())
        .requestedBy(new RequestedByForm())
        .build();
  }
}
