package uk.gov.justice.laa.amend.claim.views.claimdetails;

import static org.mockito.Mockito.when;

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
  private static final String GENDER = "gender";
  private static final String ETHNICITY = "ethnicity";
  private static final String DISABILITY = "disability";

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
    assertSummaryListRowContainsValues(clientDetails.get(1), "Surname", SURNAME);
    assertSummaryListRowContainsValues(clientDetails.get(2), "Gender", GENDER);
    assertSummaryListRowContainsValues(clientDetails.get(3), "Ethnicity", ETHNICITY);
    assertSummaryListRowContainsValues(clientDetails.get(4), "Disability", DISABILITY);
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Claim details");
    assertPageHasHeading(doc, "Claim details");
    assertPageDoesNotHaveBackLink(doc);

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasInactiveSubNavigationItem(doc, "Overview", overviewUrl);
    assertPageHasActiveSubNavigationItem(doc, "Client", clientUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Claim history", historyUrl);

    assertH2Exists(doc, "Client");
  }
}
