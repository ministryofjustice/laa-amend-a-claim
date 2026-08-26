package uk.gov.justice.laa.payments.amend.views.amendments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.payments.amend.controllers.amendments.AmendCaseTypeController;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.service.AvailableFeeCodesService;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcase.ClaimCaseViewFactory;

@WebMvcTest(AmendCaseTypeController.class)
class AmendStageReachedViewTest extends AmendmentsBaseTest {

  private static final String FEE_CODE = "feecode";
  private static final String STAGE_REACHED = "INVC";
  private static final String STAGE_REACHED_LABEL = "INVC - Police station: attendance";

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
    assertSummaryListRowContainsValues(
        summaryList.getFirst(), "Current stage reached", STAGE_REACHED_LABEL);
    assertAutocompleteDropDownList(doc, "Amended stage reached", STAGE_REACHED_LABEL);
    assertPageHasLabel(doc, "stage-reached-input", "Amended stage reached");
  }

  @Test
  void showsAssessedBanner() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    markAssessed(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), createCaseTypeForm(claim));

    assertShowsAssessedBanner(renderDocument());
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
