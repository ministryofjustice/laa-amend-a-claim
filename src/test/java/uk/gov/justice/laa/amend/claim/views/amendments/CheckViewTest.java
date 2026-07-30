package uk.gov.justice.laa.amend.claim.views.amendments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.CheckController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@WebMvcTest(CheckController.class)
class CheckViewTest extends AmendmentsBaseTest {

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

  private static final String MATTER_TYPE_1 = "IMLB";
  private static final String MATTER_TYPE_2 = "AHQS";

  CheckViewTest() {
    this.mapping = checkUrl;
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
  void testShowsCivilClientAndCaseAmendments() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    setupClaim(claim);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setMatterType1(MATTER_TYPE_1);
    claim.setMatterType2(MATTER_TYPE_2);
    claim.setScheduleReference("SCHED001");
    claim.setFeeCode("FEE123");

    var forms = createCrimeForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    forms.getCaseTypeForm().getCurrent().getInputs().put("FEE_CODE", "changedFeeCode");
    forms.getCaseDetailsForm().getCurrent().getInputs().put("SCHEDULE_REFERENCE", "SCHED002");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(2, clientDetails.size());

    var caseType = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseType.getFirst(), "Item", "Current", "Amended");

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Item", "Current", "Amended");
  }

  @Test
  void testShowsCrimeClientAndCaseAmendments() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    setupClaim(claim);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setFeeCode("FEE123");
    claim.setStageReached("Trial");

    var forms = createCrimeForms(claim);
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changedSurname");
    forms.getClient1Form().getCurrent().getInputs().put("INITIAL", "changedForename");
    forms.getCaseTypeForm().getCurrent().getInputs().put("FEE_CODE", "changedFeeCode");

    forms.getCaseDetailsForm().getCurrent().getInputs().put("STAGE_REACHED", "Sentencing");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(3, clientDetails.size());

    var caseTypeDetails = getSummaryListInCard(doc, "Case type");
    assertSummaryListRowContainsValues(caseTypeDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(2, caseTypeDetails.size());

    var caseDetails = getSummaryListInCard(doc, "Case details");
    assertSummaryListRowContainsValues(caseDetails.getFirst(), "Item", "Current", "Amended");
    assertEquals(2, caseDetails.size());
  }

  private void setupClaim(ClaimDetails claim) {
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
  }

  private AmendmentForms createCrimeForms(ClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    return new AmendmentForms(
        new AmendmentForm(view.client1Rows()),
        new AmendmentForm(caseView.caseTypeRows()),
        new AmendmentForm(caseView.caseDetailsRows()));
  }

  private AmendmentForms createMediationForms(MediationClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    return new AmendmentForms(
        new AmendmentForm(view.client1Rows()),
        new AmendmentForm(view.client2Rows()),
        new AmendmentForm(caseView.caseTypeRows()),
        new AmendmentForm(caseView.caseDetailsRows()));
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Check your amendments");
    assertPageHasHeading(doc, "Check your amendments");
    assertPageHasBackLink(doc);

    assertPageHasLink(doc, "change-client1", "Change", amendClientUrl);
    assertPageHasLink(doc, "cancel", "Cancel amendments", overviewUrl);
  }
}
