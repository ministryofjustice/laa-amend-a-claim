package uk.gov.justice.laa.amend.claim.views.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.time.LocalDate;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.ClientController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@WebMvcTest(ClientController.class)
class ViewClientViewTest extends AmendmentsBaseTest {

  private static final String FORENAME = "forename";
  private static final String SURNAME = "surname";
  private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1970, 1, 1);
  private static final String DATE_OF_BIRTH_RENDERED = "01 January 1970";
  private static final String UCN = "ucn";
  private static final String POSTCODE = "postcode";
  private static final String GENDER = "gender";
  private static final String ETHNICITY = "ethnicity";
  private static final String DISABILITY = "disability";
  private static final String HOME_OFFICE_CLIENT_NUMBER = "homeOfficeClientNumber";
  private static final String CLIENT_TYPE = "clientType";

  private static final String CLIENT_2_FORENAME = "forename2";
  private static final String CLIENT_2_SURNAME = "surname2";
  private static final LocalDate CLIENT_2_DATE_OF_BIRTH = LocalDate.of(1971, 1, 1);
  private static final String CLIENT_2_DATE_OF_BIRTH_RENDERED = "01 January 1971";
  private static final String CLIENT_2_UCN = "ucn2";
  private static final String CLIENT_2_POSTCODE = "postcode2";
  private static final String CLIENT_2_GENDER = "gender2";
  private static final String CLIENT_2_ETHNICITY = "ethnicity2";
  private static final String CLIENT_2_DISABILITY = "disability2";

  ViewClientViewTest() {
    this.mapping = clientUrl;
  }

  @Test
  void testShowsUnamendedCrimeClientDetails() {
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
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Initial", FORENAME);
    assertSummaryListRowContainsValues(clientDetails.get(1), "Last name", SURNAME);
    assertSummaryListRowContainsValues(clientDetails.get(2), "Gender", GENDER);
    assertSummaryListRowContainsValues(clientDetails.get(3), "Ethnicity", ETHNICITY);
    assertSummaryListRowContainsValues(clientDetails.get(4), "Disability", DISABILITY);

    assertPageHasLink(doc, "back-to-claim-details", "Back to claim details", overviewUrl);
  }

  @Test
  void testShowsAmendedClient1Details() {
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
    forms.getClient1Form().getCurrent().getInputs().put("SURNAME", "changed");
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(clientDetails.get(1), "Initial", FORENAME);
    assertSummaryListRowContainsValues(clientDetails.get(2), "Last name", SURNAME, "changed");
    assertSummaryListRowContainsValues(clientDetails.get(3), "Gender", GENDER);
    assertSummaryListRowContainsValues(clientDetails.get(4), "Ethnicity", ETHNICITY);
    assertSummaryListRowContainsValues(clientDetails.get(5), "Disability", DISABILITY);

    assertPageHasLink(doc, "check", "Continue", checkUrl);
    assertPageHasLink(doc, "cancel", "Cancel", overviewUrl);
  }

  @Test
  void testShowsMediationClientDetails() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setClientDateOfBirth(DATE_OF_BIRTH);
    claim.setUniqueClientNumber(UCN);
    claim.setClientPostcode(POSTCODE);
    claim.setClientGender(GENDER);
    claim.setClientEthnicity(ETHNICITY);
    claim.setClientDisability(DISABILITY);
    claim.setIsClientLegallyAided(true);
    claim.setIsClientPostalApplicationAccepted(false);

    claim.setClient2Forename(CLIENT_2_FORENAME);
    claim.setClient2Surname(CLIENT_2_SURNAME);
    claim.setClient2DateOfBirth(CLIENT_2_DATE_OF_BIRTH);
    claim.setClient2Ucn(CLIENT_2_UCN);
    claim.setClient2Postcode(CLIENT_2_POSTCODE);
    claim.setClient2Gender(CLIENT_2_GENDER);
    claim.setClient2Ethnicity(CLIENT_2_ETHNICITY);
    claim.setClient2Disability(CLIENT_2_DISABILITY);
    claim.setIsClient2LegallyAided(false);
    claim.setIsClient2PostalApplicationAccepted(true);

    var forms = createClientForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var client1Details = getSummaryListInCard(doc, "Client 1 details");
    assertSummaryListRowContainsValues(client1Details.getFirst(), "First name", FORENAME);
    assertSummaryListRowContainsValues(client1Details.get(1), "Last name", SURNAME);
    assertSummaryListRowContainsValues(
        client1Details.get(2), "Date of birth", DATE_OF_BIRTH_RENDERED);
    assertSummaryListRowContainsValues(client1Details.get(3), "Unique client number (UCN)", UCN);
    assertSummaryListRowContainsValues(client1Details.get(4), "Postcode", POSTCODE);
    assertSummaryListRowContainsValues(client1Details.get(5), "Gender", GENDER);
    assertSummaryListRowContainsValues(client1Details.get(6), "Ethnicity", ETHNICITY);
    assertSummaryListRowContainsValues(client1Details.get(7), "Disability", DISABILITY);
    assertSummaryListRowContainsValues(client1Details.get(8), "Legally aided", "Yes");
    assertSummaryListRowContainsValues(client1Details.get(9), "Postal application accepted", "No");

    var client2Details = getSummaryListInCard(doc, "Client 2 details");
    assertSummaryListRowContainsValues(client2Details.getFirst(), "First name", CLIENT_2_FORENAME);
    assertSummaryListRowContainsValues(client2Details.get(1), "Last name", CLIENT_2_SURNAME);
    assertSummaryListRowContainsValues(
        client2Details.get(2), "Date of birth", CLIENT_2_DATE_OF_BIRTH_RENDERED);
    assertSummaryListRowContainsValues(
        client2Details.get(3), "Unique client number (UCN)", CLIENT_2_UCN);
    assertSummaryListRowContainsValues(client2Details.get(4), "Postcode", CLIENT_2_POSTCODE);
    assertSummaryListRowContainsValues(client2Details.get(5), "Gender", CLIENT_2_GENDER);
    assertSummaryListRowContainsValues(client2Details.get(6), "Ethnicity", CLIENT_2_ETHNICITY);
    assertSummaryListRowContainsValues(client2Details.get(7), "Disability", CLIENT_2_DISABILITY);
    assertSummaryListRowContainsValues(client2Details.get(8), "Legally aided", "No");
    assertSummaryListRowContainsValues(client2Details.get(9), "Postal application accepted", "Yes");

    assertPageHasLink(doc, "back-to-claim-details", "Back to claim details", overviewUrl);
  }

  @Test
  void testShowsCivilClientDetails() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setClientDateOfBirth(DATE_OF_BIRTH);
    claim.setUniqueClientNumber(UCN);
    claim.setClientPostcode(POSTCODE);
    claim.setClientGender(GENDER);
    claim.setClientEthnicity(ETHNICITY);
    claim.setClientDisability(DISABILITY);
    claim.setIsEligibleClient(true);
    claim.setClientType(CLIENT_TYPE);
    claim.setHomeOfficeClientNumber(HOME_OFFICE_CLIENT_NUMBER);
    claim.setIsPostalApplication(false);

    var forms = createClientForms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "First name", FORENAME);
    assertSummaryListRowContainsValues(clientDetails.get(1), "Last name", SURNAME);
    assertSummaryListRowContainsValues(
        clientDetails.get(2), "Date of birth", DATE_OF_BIRTH_RENDERED);
    assertSummaryListRowContainsValues(clientDetails.get(3), "Gender", GENDER);
    assertSummaryListRowContainsValues(clientDetails.get(4), "Ethnicity", ETHNICITY);
    assertSummaryListRowContainsValues(clientDetails.get(5), "Disability", DISABILITY);
    assertSummaryListRowContainsValues(clientDetails.get(6), "Postcode", POSTCODE);
    assertSummaryListRowContainsValues(clientDetails.get(7), "Eligible client", "Yes");
    assertSummaryListRowContainsValues(clientDetails.get(8), "Client type", CLIENT_TYPE);
    assertSummaryListRowContainsValues(clientDetails.get(9), "Unique client number (UCN)", UCN);
    assertSummaryListRowContainsValues(
        clientDetails.get(10),
        "Home Office unique client number (HO UCN)",
        HOME_OFFICE_CLIENT_NUMBER);
    assertSummaryListRowContainsValues(clientDetails.get(11), "Postal application accepted", "No");

    assertPageHasLink(doc, "back-to-claim-details", "Back to claim details", overviewUrl);
  }

  private AmendmentForms createClientForms(ClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    return new AmendmentForms(
        new AmendmentForm(view.client1Rows()), new AmendmentForm(), new AmendmentForm());
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Amend claim details");
    assertPageHasHeading(doc, "Amend claim details");
    assertPageHasBackLink(doc);

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasActiveSubNavigationItem(doc, "Client", clientUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Case", caseUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Costs", costsUrl);

    assertH2Exists(doc, "Client");

    assertPageHasLink(doc, "amend-client-1", "Change", amendClientUrl);
  }
}
