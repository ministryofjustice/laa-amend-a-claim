package uk.gov.justice.laa.amend.claim.views.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.AMENDMENTS_KEY;

import java.time.LocalDate;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.ClientController;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@WebMvcTest(ClientController.class)
class AmendClient2ViewTest extends AmendmentsBaseTest {

  private static final String CLIENT_2_FORENAME = "forename2";
  private static final String CLIENT_2_SURNAME = "surname2";
  private static final LocalDate CLIENT_2_DATE_OF_BIRTH = LocalDate.of(1971, 1, 1);
  private static final String CLIENT_2_DATE_OF_BIRTH_RENDERED = "01 January 1971";
  private static final String CLIENT_2_UCN = "ucn2";
  private static final String CLIENT_2_POSTCODE = "postcode2";
  private static final String CLIENT_2_GENDER_LABEL = "Female";
  private static final String CLIENT_2_GENDER = "F";
  private static final String CLIENT_2_ETHNICITY_LABEL = "01 - White Irish";
  private static final String CLIENT_2_ETHNICITY = "01";
  private static final String CLIENT_2_DISABILITY_LABEL = "MOB - Mobility";
  private static final String CLIENT_2_DISABILITY = "MOB";

  AmendClient2ViewTest() {
    this.mapping = amendClientTwoUrl;
  }

  @Test
  void testShowsMediationClient2Details() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    this.claim = claim;
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);

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

    var forms = createClient2Forms(claim);
    session.setAttribute(AMENDMENTS_KEY.formatted(claimId), forms);

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var client2Details = getSummaryListInCard(doc, "Client 2 details");
    assertSummaryListRowContainsValues(client2Details.getFirst(), "Item", "Current", "Amended");
    assertSummaryListRowContainsValues(
        client2Details.get(1), "First name", CLIENT_2_FORENAME, CLIENT_2_FORENAME);
    assertSummaryListRowContainsValues(
        client2Details.get(2), "Last name", CLIENT_2_SURNAME, CLIENT_2_SURNAME);
    assertDateOfBirthRow(client2Details.get(3));
    assertSummaryListRowContainsValues(
        client2Details.get(4), "Unique client number (UCN)", CLIENT_2_UCN, CLIENT_2_UCN);
    assertSummaryListRowContainsValues(
        client2Details.get(5), "Postcode", CLIENT_2_POSTCODE, CLIENT_2_POSTCODE);
    assertEnumTypeaheadRow(
        client2Details.get(6),
        "Gender",
        CLIENT_2_GENDER_LABEL,
        "CLIENT_2_GENDER",
        CLIENT_2_GENDER);
    assertEnumTypeaheadRow(
        client2Details.get(7),
        "Ethnicity",
        CLIENT_2_ETHNICITY_LABEL,
        "CLIENT_2_ETHNICITY",
        CLIENT_2_ETHNICITY);
    assertEnumTypeaheadRow(
        client2Details.get(8),
        "Disability",
        CLIENT_2_DISABILITY_LABEL,
        "CLIENT_2_DISABILITY",
        CLIENT_2_DISABILITY);
    assertBooleanSelectRow(
        client2Details.get(9), "Legally aided", "No", "IS_CLIENT_2_LEGALLY_AIDED", false);
    assertBooleanSelectRow(
        client2Details.get(10),
        "Postal application accepted",
        "Yes",
        "IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED",
        true);
  }

  private void assertDateOfBirthRow(List<Element> row) {
    assertCellContainsText(row.getFirst(), "Date of birth");
    assertCellContainsText(row.get(1), CLIENT_2_DATE_OF_BIRTH_RENDERED);

    Element amended = row.get(2);
    Element dateInput = selectFirst(amended, ".govuk-date-input");

    Element day = selectFirst(dateInput, "input.govuk-input--width-2");
    Assertions.assertEquals("1", day.attr("value"), "Day input value");
    Assertions.assertEquals("CLIENT_2_DATE_OF_BIRTH-day", day.attr("id"), "Day input id");

    Element year = selectFirst(dateInput, "input.govuk-input--width-4");
    Assertions.assertEquals("1971", year.attr("value"), "Year input value");
    Assertions.assertEquals("CLIENT_2_DATE_OF_BIRTH-year", year.attr("id"), "Year input id");

    Element monthSelect = selectFirst(dateInput, "select");
    Assertions.assertEquals(
        "CLIENT_2_DATE_OF_BIRTH-month", monthSelect.attr("id"), "Month select id");
    Assertions.assertEquals(
        12, monthSelect.select("option[value~=^[0-9]+$]").size(), "Twelve month options");
    Element selectedMonth = selectFirst(monthSelect, "option[selected]");
    Assertions.assertEquals("January", selectedMonth.text(), "Selected month name");
    Assertions.assertEquals("1", selectedMonth.attr("value"), "Selected month value");
  }

  private AmendmentForms createClient2Forms(MediationClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    return new AmendmentForms(
        new AmendmentForm(),
        new AmendmentForm(view.client2Rows()),
        new AmendmentForm(),
        new AmendmentForm());
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
