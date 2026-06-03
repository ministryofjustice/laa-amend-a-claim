package uk.gov.justice.laa.amend.claim.views.claimdetails;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.claimdetails.ClaimClientController;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

@WebMvcTest(ClaimClientController.class)
class ClaimClientViewTest extends ClaimDetailsBaseTest {

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

  @MockitoBean private AssessmentService assessmentService;
  @MockitoBean private UserRetrievalService userRetrievalService;

  @BeforeEach
  public void setup() {
    super.setup();
    when(featureFlagsConfig.getIsFullClaimDetailsEnabled()).thenReturn(true);
    mapping = clientUrl;
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

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var clientDetails = getSummaryListInCard(doc, "Client details");
    assertSummaryListRowContainsValues(clientDetails.getFirst(), "Initial", FORENAME);
    assertSummaryListRowContainsValues(clientDetails.get(1), "Last name", SURNAME);
    assertSummaryListRowContainsValues(clientDetails.get(2), "Gender", GENDER);
    assertSummaryListRowContainsValues(clientDetails.get(3), "Ethnicity", ETHNICITY);
    assertSummaryListRowContainsValues(clientDetails.get(4), "Disability", DISABILITY);
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
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Claim details");
    assertPageHasHeading(doc, "Claim details");
    assertPageDoesNotHaveBackLink(doc);

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasInactiveSubNavigationItem(doc, "Overview", overviewUrl);
    assertPageHasActiveSubNavigationItem(doc, "Client", clientUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Case", caseUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Claim history", historyUrl);

    assertH2Exists(doc, "Client");
  }
}
