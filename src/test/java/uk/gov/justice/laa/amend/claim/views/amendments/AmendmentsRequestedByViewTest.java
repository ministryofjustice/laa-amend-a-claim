package uk.gov.justice.laa.amend.claim.views.amendments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.amendments.AmendmentRequestedByController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.forms.validators.RequestedByFormValidator;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReference;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;

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
    when(systemReferenceService.getAmendmentRequestedByReferenceList())
        .thenReturn(createReferenceList(createUnsortedReferenceMap()));

    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    var forms = createRequestedByForms();
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    Document doc = renderDocument();

    assertPageHasTitle(doc, "Amend claim details");

    assertPageHasHeading(doc, "Who requested the amendment?");

    assertPageHasRadioButtons(doc, "RequestBy Label 1", "RequestBy Label 2");
    assertThat(doc.getElementsByClass("govuk-radios__label").eachText())
        .containsExactly("RequestBy Label 1", "RequestBy Label 2");
    assertNoRadioSelected(doc);

    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasSecondaryLink(doc, "Cancel");
  }

  private AmendmentRequestedByReferenceList createReferenceList(Map<String, String> codes) {
    var referenceList = new AmendmentRequestedByReferenceList();
    codes.forEach(
        (code, displayLabel) -> {
          var reference = new AmendmentRequestedByReference();
          reference.setCode(code);
          reference.setDisplayLabel(displayLabel);
          referenceList.getRequestedBy().add(reference);
        });
    return referenceList;
  }

  private Map<String, String> createUnsortedReferenceMap() {
    var unsortedMap = new LinkedHashMap<String, String>();
    unsortedMap.put("code1", "RequestBy Label 2");
    unsortedMap.put("code2", "RequestBy Label 1");
    return unsortedMap;
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
