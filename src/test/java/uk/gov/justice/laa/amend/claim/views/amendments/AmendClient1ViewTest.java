package uk.gov.justice.laa.amend.claim.views.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.time.LocalDate;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.AmendClientController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockAmendmentFormsFunctions;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@WebMvcTest(AmendClientController.class)
class AmendClient1ViewTest extends AmendmentsBaseTest {

  private static final String FORENAME = "forename";
  private static final String SURNAME = "surname";
  private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1970, 1, 1);
  private static final String DATE_OF_BIRTH_RENDERED = "01 January 1970";
  private static final String UCN = "ucn";
  private static final String POSTCODE = "postcode";
  private static final String GENDER_LABEL = "Male";
  private static final String GENDER = "M";
  private static final String ETHNICITY_LABEL = "00 - White British";
  private static final String ETHNICITY = "00";
  private static final String DISABILITY_LABEL = "NCD - No condition declared";
  private static final String DISABILITY = "NCD";
  private static final String HOME_OFFICE_CLIENT_NUMBER = "homeOfficeClientNumber";
  private static final String CLIENT_TYPE_LABEL = "Parent";
  private static final String CLIENT_TYPE = "P";

  private static final String CLIENT_2_FORENAME = "forename2";
  private static final String CLIENT_2_SURNAME = "surname2";
  private static final LocalDate CLIENT_2_DATE_OF_BIRTH = LocalDate.of(1971, 1, 1);
  private static final String CLIENT_2_UCN = "ucn2";
  private static final String CLIENT_2_POSTCODE = "postcode2";
  private static final String CLIENT_2_GENDER = "gender2";
  private static final String CLIENT_2_ETHNICITY = "01";
  private static final String CLIENT_2_DISABILITY = "MOB";

  AmendClient1ViewTest() {
    this.mapping = amendClientUrl;
  }

  @Test
  void testShowsCrimeClientDetails() {
    var claim = MockClaimsFunctions.createMockCrimeClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    claim.setClientForename(FORENAME);
    claim.setClientSurname(SURNAME);
    claim.setClientGender(GENDER);
    claim.setClientEthnicity(ETHNICITY);
    claim.setClientDisability(DISABILITY);

    var forms = MockAmendmentFormsFunctions.justClient1Filled(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(clientDetails.get(1), "Initial", FORENAME, FORENAME);
    assertSummaryListRowContainsValues(clientDetails.get(2), "Last name", SURNAME, SURNAME);
    assertEnumTypeaheadRow(clientDetails.get(3), "Gender", GENDER_LABEL, "GENDER", GENDER);
    assertEnumTypeaheadRow(
        clientDetails.get(4), "Ethnicity", ETHNICITY_LABEL, "ETHNICITY", ETHNICITY);
    assertEnumTypeaheadRow(
        clientDetails.get(5), "Disability", DISABILITY_LABEL, "DISABILITY", DISABILITY);
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

    var forms = MockAmendmentFormsFunctions.justClient1Filled(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var client1Details = getSummaryListInCard(doc, "Client 1 details");
    assertSummaryListRowContainsValues(client1Details.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(client1Details.get(1), "First name", FORENAME, FORENAME);
    assertSummaryListRowContainsValues(client1Details.get(2), "Last name", SURNAME, SURNAME);
    assertDateOfBirthRow(client1Details.get(3));
    assertSummaryListRowContainsValues(
        client1Details.get(4), "Unique client number (UCN)", UCN, UCN);
    assertSummaryListRowContainsValues(client1Details.get(5), "Postcode", POSTCODE, POSTCODE);
    assertEnumTypeaheadRow(client1Details.get(6), "Gender", GENDER_LABEL, "GENDER", GENDER);
    assertEnumTypeaheadRow(
        client1Details.get(7), "Ethnicity", ETHNICITY_LABEL, "ETHNICITY", ETHNICITY);
    assertEnumTypeaheadRow(
        client1Details.get(8), "Disability", DISABILITY_LABEL, "DISABILITY", DISABILITY);
    assertBooleanSelectRow(client1Details.get(9), "Legally aided", "Yes", "IS_LEGALLY_AIDED", true);
    assertBooleanSelectRow(
        client1Details.get(10),
        "Postal application accepted",
        "No",
        "IS_POSTAL_APPLICATION_ACCEPTED",
        false);
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

    var forms = MockAmendmentFormsFunctions.justClient1Filled(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(clientDetails.get(1), "First name", FORENAME, FORENAME);
    assertSummaryListRowContainsValues(clientDetails.get(2), "Last name", SURNAME, SURNAME);
    assertDateOfBirthRow(clientDetails.get(3));
    assertEnumTypeaheadRow(clientDetails.get(4), "Gender", GENDER_LABEL, "GENDER", GENDER);
    assertEnumTypeaheadRow(
        clientDetails.get(5), "Ethnicity", ETHNICITY_LABEL, "ETHNICITY", ETHNICITY);
    assertEnumTypeaheadRow(
        clientDetails.get(6), "Disability", DISABILITY_LABEL, "DISABILITY", DISABILITY);
    assertSummaryListRowContainsValues(clientDetails.get(7), "Postcode", POSTCODE, POSTCODE);
    assertBooleanSelectRow(
        clientDetails.get(8), "Eligible client", "Yes", "IS_ELIGIBLE_CLIENT", true);
    assertEnumTypeaheadRow(
        clientDetails.get(9), "Client type", CLIENT_TYPE_LABEL, "CLIENT_TYPE", CLIENT_TYPE);
    assertSummaryListRowContainsValues(
        clientDetails.get(10), "Unique client number (UCN)", UCN, UCN);
    assertSummaryListRowContainsValues(
        clientDetails.get(11),
        "Home Office unique client number (HO UCN)",
        HOME_OFFICE_CLIENT_NUMBER,
        HOME_OFFICE_CLIENT_NUMBER);
    assertBooleanSelectRow(
        clientDetails.get(12),
        "Postal application accepted",
        "No",
        "IS_POSTAL_APPLICATION_ACCEPTED",
        false);
  }

  private void assertDateOfBirthRow(List<Element> row) {
    assertCellContainsText(row.getFirst(), "Date of birth");
    assertCellContainsText(row.get(1), DATE_OF_BIRTH_RENDERED);

    Element amended = row.get(2);
    Element dateInput = selectFirst(amended, ".govuk-date-input");

    Element day = selectFirst(dateInput, "input.govuk-input--width-2");
    Assertions.assertEquals("1", day.attr("value"), "Day input value");
    Assertions.assertEquals("DATE_OF_BIRTH-day", day.attr("id"), "Day input id");

    Element year = selectFirst(dateInput, "input.govuk-input--width-4");
    Assertions.assertEquals("1970", year.attr("value"), "Year input value");
    Assertions.assertEquals("DATE_OF_BIRTH-year", year.attr("id"), "Year input id");

    Element monthSelect = selectFirst(dateInput, "select");
    Assertions.assertEquals("DATE_OF_BIRTH-month", monthSelect.attr("id"), "Month select id");
    Assertions.assertEquals(
        12, monthSelect.select("option[value~=^[0-9]+$]").size(), "Twelve month options");
    Element selectedMonth = selectFirst(monthSelect, "option[selected]");
    Assertions.assertEquals("January", selectedMonth.text(), "Selected month name");
    Assertions.assertEquals("1", selectedMonth.attr("value"), "Selected month value");
  }


  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Amend claim details");
    assertPageHasHeading(doc, "Amend claim details");
    assertPageHasBackLink(doc);

    assertH2Exists(doc, "Client");

    assertPageHasPrimaryButton(doc, "Continue");
    assertPageHasLink(doc, "cancel", "Cancel", clientUrl);
  }
}
