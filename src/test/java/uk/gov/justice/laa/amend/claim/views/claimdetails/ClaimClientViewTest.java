package uk.gov.justice.laa.amend.claim.views.claimdetails;

import static org.mockito.Mockito.when;

import org.apache.commons.compress.archivers.sevenz.CLI;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.claimdetails.ClaimClientController;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

import java.time.LocalDate;

@WebMvcTest(ClaimClientController.class)
class ClaimClientViewTest extends ClaimDetailsBaseTest {

  private static final String FORENAME = "forename";
  private static final String SURNAME = "surname";
  private static final String GENDER = "gender";
  private static final String ETHNICITY = "ethnicity";
  private static final String DISABILITY = "disability";

    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1970, 1, 1);
    private static final String DATE_OF_BIRTH_RENDERED = "01 January 1970";
    private static final String UCN = "ucn";
    private static final String POSTCODE = "postcode";
    private static final String HO_UCN = "hoUcn";
    private static final String CLIENT_TYPE = "clientType";


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
    claim.setEligibleClient(true);
    claim.setClientType(CLIENT_TYPE);
    claim.setHoUcn(HO_UCN);

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
    assertSummaryListRowContainsValues(clientDetails.get(10), "Home Office unique client number (HO UCN)", HO_UCN);
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
