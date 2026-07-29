package uk.gov.justice.laa.amend.claim.views.amendments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.CheckController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@WebMvcTest(CheckController.class)
class CheckViewTest extends AmendmentsBaseTest {

  private static final String FORENAME = "forename";
  private static final String SURNAME = "surname";
  private static final String GENDER = "gender";
  private static final String ETHNICITY = "ethnicity";
  private static final String DISABILITY = "disability";

  CheckViewTest() {
    this.mapping = checkUrl;
  }

  @Test
  void testShowsOnlyAmendedClientFields() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setClientGender(GENDER);
    claim.setClientEthnicity(ETHNICITY);
    claim.setClientDisability(DISABILITY);

    var forms = createClientForms(claim);
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
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setClientGender(GENDER);
    claim.setClientEthnicity(ETHNICITY);
    claim.setClientDisability(DISABILITY);

    var forms = createClientForms(claim);
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

  private AmendmentForms createClientForms(ClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    return new AmendmentForms(
        new AmendmentForm(view.client1Rows()), new AmendmentForm(), new AmendmentForm());
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Check your amendments");
    assertPageHasHeading(doc, "Check your amendments");
    assertPageHasBackLink(doc);

    assertPageHasLink(doc, "change-client", "Change", amendClientUrl);
    assertPageHasLink(doc, "cancel", "Cancel amendments", overviewUrl);
  }
}
