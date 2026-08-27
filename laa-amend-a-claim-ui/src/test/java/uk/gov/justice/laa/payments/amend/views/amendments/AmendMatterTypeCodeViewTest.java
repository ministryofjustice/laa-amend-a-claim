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
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.resources.MockAmendmentFormsFunctions;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.service.AvailableFeeCodesService;

@WebMvcTest(AmendCaseTypeController.class)
class AmendMatterTypeCodeViewTest extends AmendmentsBaseTest {

  private static final String FEE_CODE = "feecode";
  private static final String MATTER_TYPE_CODE_1 = "matterone";
  private static final String MATTER_TYPE_CODE_2 = "mattertwo";

  @MockitoBean AvailableFeeCodesService availableFeeCodesService;

  AmendMatterTypeCodeViewTest() {
    this.mapping = amendMatterTypeCodeUrl;
  }

  @Test
  void testShowsUnamendedLegalHelpMatterTypeCode() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    this.claim = claim;
    this.claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);

    var forms = MockAmendmentFormsFunctions.justCaseTypeFilled(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(any())).thenReturn(Map.of(FEE_CODE, "ABC"));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var matterTypeSummaryList = getFirstSummaryList(doc);
    assertSummaryListRowContainsValues(
        matterTypeSummaryList.get(0), "Current matter type 1", MATTER_TYPE_CODE_1);
    assertSummaryListRowContainsValues(
        matterTypeSummaryList.get(1), "Current matter type 2", MATTER_TYPE_CODE_2);
    assertPageHasLabel(doc, "matter-type-one-input", "Amended matter type 1");
    assertPageHasLabel(doc, "matter-type-two-input", "Amended matter type 2");
  }

  @Test
  void testShowsUnamendedMediationMatterTypeCode() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    this.claim = claim;
    this.claim.setAreaOfLaw(AreaOfLaw.MEDIATION);
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);

    var forms = MockAmendmentFormsFunctions.justCaseTypeFilled(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    when(availableFeeCodesService.getAvailableFeeCodes(any())).thenReturn(Map.of(FEE_CODE, "ABC"));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var matterTypeSummaryList = getFirstSummaryList(doc);
    assertSummaryListRowContainsValues(
        matterTypeSummaryList.get(0), "Current matter type 1", MATTER_TYPE_CODE_1);
    assertSummaryListRowContainsValues(
        matterTypeSummaryList.get(1), "Current matter type 2", MATTER_TYPE_CODE_2);
    assertPageHasLabel(doc, "matter-type-one-input", "Amended matter type 1");
    assertPageHasLabel(doc, "matter-type-two-input", "Amended matter type 2");
  }

  @Test
  void showsAssessedBanner() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    this.claim = claim;
    this.claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterType1(MATTER_TYPE_CODE_1);
    claim.setMatterType2(MATTER_TYPE_CODE_2);
    markAssessed(claim);
    session.setAttribute(
        AMENDMENTS_KEY.formatted(claimId), MockAmendmentFormsFunctions.justCaseTypeFilled(claim));
    when(availableFeeCodesService.getAvailableFeeCodes(any())).thenReturn(Map.of(FEE_CODE, "ABC"));

    assertShowsAssessedBanner(renderDocument());
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Amend claim details");
    assertPageHasHeading(doc, "Amend matter type");
    assertPageHasBackLink(doc);

    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasLink(doc, "cancel", "Cancel", caseUrl);
  }
}
