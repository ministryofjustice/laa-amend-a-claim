package uk.gov.justice.laa.amend.claim.views.amendments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.util.Map;
import org.jsoup.nodes.Document;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.amendments.CheckAmendmentsController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.CheckAmendmentsService;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcosts.ClaimCostsViewFactory;

@WebMvcTest(CheckAmendmentsController.class)
class CheckAmendmentsViewTest extends AmendmentsBaseTest {

  private static final String FORENAME = "forename";
  private static final String SURNAME = "surname";
  private static final String GENDER = "gender";
  private static final String ETHNICITY = "ethnicity";
  private static final String DISABILITY = "disability";

  private static final String CLIENT_2_FORENAME = "forename2";
  private static final String CLIENT_2_SURNAME = "surname2";
  private static final String CLIENT_2_GENDER = "gender2";
  private static final String CLIENT_2_ETHNICITY = "ethnicity2";
  private static final String CLIENT_2_DISABILITY = "disability2";

  private static final String MATTER_TYPE1 = "matterType1";
  private static final String MATTER_TYPE2 = "matterType2";
  private static final String MATTER_TYPE_CODE = "matterType";
  private static final String SCHEDULE_REFERENCE = "schedulereference";
  private static final String FEE_CODE = "feecode";
  private static final String STAGE_REACHED = "INVA";

  @MockitoBean CheckAmendmentsService checkAmendmentsService;
  @MockitoBean SystemReferenceService systemReferenceService;

  CheckAmendmentsViewTest() {
    this.mapping = checkUrl;
  }

  @BeforeEach
  void setupReferenceData() {
    when(systemReferenceService.getAmendmentRequestedByOptions())
        .thenReturn(Map.of("requestedBy", "Provider"));
    when(systemReferenceService.getAmendmentRequestReason("requestedBy"))
        .thenReturn(Map.of("reason", "Case reopened / rebilled"));
  }

  @Test
  void testShowsOnlyAmendedClientFields() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    setupClaim(claim);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setClientGender(GENDER);
    claim.setClientEthnicity(ETHNICITY);
    claim.setClientDisability(DISABILITY);

    var forms = createCrimeForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(
        clientDetails.get(1), "Last name", SURNAME, "changedSurname");
    assertEquals(2, clientDetails.size());
  }

  @Test
  void testShowsMultipleAmendedClientFields() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    setupClaim(claim);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setClientGender(GENDER);
    claim.setClientEthnicity(ETHNICITY);
    claim.setClientDisability(DISABILITY);

    var forms = createCrimeForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("INITIAL", "changedForename");
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    forms.getClient1Form().getCurrent().getInputs().put("GENDER", "changedGender");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(
        clientDetails.get(1), "Initial", FORENAME, "changedForename");
    assertSummaryListRowContainsValues(
        clientDetails.get(2), "Last name", SURNAME, "changedSurname");
    assertSummaryListRowContainsValues(clientDetails.get(3), "Gender", GENDER, "changedGender");
    assertEquals(4, clientDetails.size());
  }

  @Test
  void testShowsMediationClientDetails() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    setupClaim(claim);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setClientGender(GENDER);
    claim.setClientEthnicity(ETHNICITY);
    claim.setClientDisability(DISABILITY);

    claim.setClient2Forename(CLIENT_2_FORENAME);
    claim.setClient2Surname(CLIENT_2_SURNAME);
    claim.setClient2Gender(CLIENT_2_GENDER);
    claim.setClient2Ethnicity(CLIENT_2_ETHNICITY);
    claim.setClient2Disability(CLIENT_2_DISABILITY);

    var forms = createMediationForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    forms
        .getClient2Form()
        .getCurrent()
        .getInputs()
        .put("CLIENT_2_SURNAME", "changedClient2Surname");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var client1Details = getSummaryListInCard(doc, "Client 1 details");
    assertSummaryListRowContainsValues(client1Details.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(
        client1Details.get(1), "Last name", SURNAME, "changedSurname");
    assertEquals(2, client1Details.size());

    var client2Details = getSummaryListInCard(doc, "Client 2 details");
    assertSummaryListRowContainsValues(client2Details.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(
        client2Details.get(1), "Last name", CLIENT_2_SURNAME, "changedClient2Surname");
    assertEquals(2, client2Details.size());

    assertPageHasLink(doc, "change-client1", "Change", amendClientUrl);
    assertPageHasLink(doc, "change-client2", "Change", amendClientTwoUrl);
  }

  @Test
  void testShowsCivilAmendments() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    setupClaim(claim);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setMatterType1(MATTER_TYPE1);
    claim.setMatterType2(MATTER_TYPE2);
    claim.setScheduleReference(SCHEDULE_REFERENCE);
    claim.setFeeCode(FEE_CODE);

    var forms = createCrimeForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    forms.getCaseTypeForm().getCurrent().getInputs().put("FEE_CODE", "changedFeeCode");
    forms.getCaseTypeForm().getCurrent().getInputs().put("MATTER_TYPE_CODE_1", "changed1");
    forms.getCaseTypeForm().getCurrent().getInputs().put("MATTER_TYPE_CODE_2", "changed2");
    forms
        .getCaseDetailsForm()
        .getCurrent()
        .getInputs()
        .put("SCHEDULE_REFERENCE_CIVIL", "changedRef");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(2, clientDetails.size());
    assertSummaryListRowContainsValues(
        clientDetails.get(1), "Last name", SURNAME, "changedSurname");

    var caseType = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseType.getFirst(), "Item", "Current", "Amended");
    assertEquals(4, caseType.size());
    assertSummaryListRowContainsValues(caseType.get(1), "Fee code", "feecode", "changedFeeCode");
    assertSummaryListRowContainsValues(caseType.get(2), "Matter type 1", "matterType1", "changed1");
    assertSummaryListRowContainsValues(caseType.get(3), "Matter type 2", "matterType2", "changed2");

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(2, caseDetails.size());
    assertSummaryListRowContainsValues(
        caseDetails.get(1), "Schedule reference", "schedulereference", "changedRef");
  }

  @Test
  void testShowsCrimeAmendments() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    setupClaim(claim);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setFeeCode(FEE_CODE);
    claim.setMatterTypeCode(MATTER_TYPE_CODE);
    claim.setStageReached(STAGE_REACHED);

    var forms = createCrimeForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    forms.getClient1Form().getCurrent().getInputs().put("INITIAL", "changedForename");
    forms.getCaseTypeForm().getCurrent().getInputs().put("FEE_CODE", "changedFeeCode");
    forms
        .getCaseDetailsForm()
        .getCurrent()
        .getInputs()
        .put("MATTER_TYPE_CODE", "changedMatterType");
    forms.getCostsForm().getCurrent().getInputs().put("PROFIT_COST", "5000.00");

    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(3, clientDetails.size());
    assertSummaryListRowContainsValues(
        clientDetails.get(1), "Initial", FORENAME, "changedForename");
    assertSummaryListRowContainsValues(
        clientDetails.get(2), "Last name", SURNAME, "changedSurname");

    var caseTypeDetails = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseTypeDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(2, caseTypeDetails.size());
    assertSummaryListRowContainsValues(
        caseTypeDetails.get(1), "Fee code", "feecode", "changedFeeCode");

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(2, caseDetails.size());
    assertSummaryListRowContainsValues(
        caseDetails.get(1), "Matter type", MATTER_TYPE_CODE, "changedMatterType");
    var costsDetails = getSummaryListInCard(doc, "Reported costs");
    assertSummaryListRowContainsValues(costsDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(2, costsDetails.size());
    assertPageHasLink(doc, "change-costs", "Change", amendCostsUrl);
  }

  @Test
  void testShowsAmendmentRequestDetailsCard() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    setupClaim(claim);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);

    var forms = createCrimeForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();

    var requestDetails = getSummaryListInCard(doc, "Amendment request details");
    assertSummaryListRowContainsValues(requestDetails.get(0), "Amendment requested by", "Provider");
    assertSummaryListRowContainsValues(
        requestDetails.get(1), "Reason for amendment", "Case reopened / rebilled");
    assertPageHasLink(doc, "change-request-details", "Change", requestedByUrl);
  }

  @Test
  void showsAssessedBanner() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    setupClaim(claim);
    markAssessed(claim);
    var forms = createCrimeForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    assertShowsAssessedBanner(renderDocument());
  }

  private void setupClaim(ClaimDetails claim) {
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
  }

  private AmendmentForms createCrimeForms(ClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    var costsView = ClaimCostsViewFactory.create(claim);
    var requestedByForm = createRequestedByForm();
    var requestedReasonForm = createRequestedReasonForm();

    return AmendmentForms.builder()
        .client1(new AmendmentForm(view.client1Rows()))
        .caseType(new AmendmentForm(caseView.caseTypeRows()))
        .caseDetails(new AmendmentForm(caseView.caseDetailsRows()))
        .costs(new AmendmentForm(costsView.costRows()))
        .requestedBy(requestedByForm)
        .requestedReason(requestedReasonForm)
        .build();
  }

  private AmendmentForms createMediationForms(MediationClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    var costsView = ClaimCostsViewFactory.create(claim);
    var requestedByForm = createRequestedByForm();
    var requestedReasonForm = createRequestedReasonForm();

    return AmendmentForms.builder()
        .client1(new AmendmentForm(view.client1Rows()))
        .client2(new AmendmentForm(view.client2Rows()))
        .caseType(new AmendmentForm(caseView.caseTypeRows()))
        .caseDetails(new AmendmentForm(caseView.caseDetailsRows()))
        .costs(new AmendmentForm(costsView.costRows()))
        .requestedBy(requestedByForm)
        .requestedReason(requestedReasonForm)
        .build();
  }

  private static @NonNull RequestedByForm createRequestedByForm() {
    var requestedByForm = new RequestedByForm();
    requestedByForm.setRequestedBy("requestedBy");
    return requestedByForm;
  }

  private static @NonNull RequestedReasonForm createRequestedReasonForm() {
    var requestedReasonForm = new RequestedReasonForm();
    requestedReasonForm.setRequestedReason("reason");
    return requestedReasonForm;
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Check your amendments");
    assertPageHasHeading(doc, "Check your amendments");
    assertPageHasBackLink(doc);

    assertPageHasLink(doc, "change-client1", "Change", amendClientUrl);
    assertPageHasLink(doc, "cancel", "Cancel amendments", overviewUrl);
  }
}
