package uk.gov.justice.laa.amend.claim.views.amendments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.amendments.AmendCaseTypeController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AvailableFeeCodesService;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;

@WebMvcTest(AmendCaseTypeController.class)
class AmendStageReachedViewTest extends AmendmentsBaseTest {

  private static final String FEE_CODE = "feecode";
  private static final String STAGE_REACHED = "INVC";

  @MockitoBean AvailableFeeCodesService availableFeeCodesService;

  AmendStageReachedViewTest() {
    this.mapping = amendStageReachedUrl;
  }

  @Test
  void testShowsUnamendedCrimeStageReached() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    this.claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode(FEE_CODE);
    claim.setStageReached(STAGE_REACHED);

    var forms = createCaseTypeForm(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(any())).thenReturn(Map.of(FEE_CODE, "ABC"));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var summaryList = getFirstSummaryList(doc);
    assertSummaryListRowContainsValues(summaryList.getFirst(), "Current stage reached", "INVC");
    assertAutocompleteDropDownList(doc, "Amended stage reached", "INVC");
    assertPageHasLabel(doc, "stage-reached-input", "Amended stage reached");
  }

  private AmendmentForms createCaseTypeForm(ClaimDetails claim) {
    var view = ClaimCaseViewFactory.create(claim);
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .caseType(new AmendmentForm(view.caseTypeRows()))
        .caseDetails(new AmendmentForm())
        .build();
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Amend claim details");
    assertPageHasHeading(doc, "Amend stage reached");
    assertPageHasBackLink(doc);

    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasLink(doc, "cancel", "Cancel", caseUrl);
  }
}
