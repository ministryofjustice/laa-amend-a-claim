package uk.gov.justice.laa.amend.claim.views.claimdetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.controllers.claimdetails.ClaimInquestController;
import uk.gov.justice.laa.amend.claim.exceptions.FeatureNotEnabledException;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.InquestDataService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestData;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.InquestDepartmentReference;

@WebMvcTest(ClaimInquestController.class)
class ClaimInquestViewTest extends ClaimDetailsBaseTest {

  private static final String DEPARTMENT_CODE = "DEPT_A";
  private static final String DEPARTMENT_LABEL = "Department A";
  private static final String OTHER_DEPARTMENT_CODE = "DEPT_B";
  private static final String OTHER_DEPARTMENT_LABEL = "Department B";

  @MockitoBean private AssessmentService assessmentService;
  @MockitoBean private UserRetrievalService userRetrievalService;
  @MockitoBean private InquestDataService inquestDataService;

  @BeforeEach
  public void setup() {
    super.setup();
    mapping = inquestUrl;

    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    ((CivilClaimDetails) claim).setMatterType1("INQUEST");

    when(featureFlagsConfig.getIsInquestTabEnabled()).thenReturn(true);
    when(inquestConfig.getMatterTypeCodes()).thenReturn(Set.of("INQUEST"));
    when(inquestDataService.getInquestDepartments())
        .thenReturn(
            List.of(
                new InquestDepartmentReference(DEPARTMENT_CODE, DEPARTMENT_LABEL, 1, true),
                new InquestDepartmentReference(
                    OTHER_DEPARTMENT_CODE, OTHER_DEPARTMENT_LABEL, 2, true)));
  }

  @Test
  void testShowsEmptyFormWhenNoInquestDataExists() {
    when(inquestDataService.get(claimId)).thenReturn(Optional.empty());

    var doc = renderDocument();

    assertPageHasActiveSubNavigationItem(doc, "Inquest", inquestUrl);
    assertPageHasHeading(doc, "Claim details");
    assertH2Exists(doc, "Inquest");

    assertInputValue(doc, "deceasedForename", "");
    assertInputValue(doc, "deceasedSurname", "");
    assertInputValue(doc, "coronersInquestReference", "");

    var checkbox = selectFirst(doc, "input[name=interestedDepartmentCodes]");
    assertEquals("", checkbox.attr("checked"));
  }

  @Test
  void testShowsPrefilledFormWhenInquestDataExists() {
    var data =
        ClaimInquestData.builder()
            .deceasedForename("John")
            .deceasedSurname("Smith")
            .deceasedDateOfBirth(LocalDate.of(1950, 1, 1))
            .deceasedDateOfDeath(LocalDate.of(2026, 1, 1))
            .coronersInquestReference("REF-123")
            .interestedDepartmentCodes(Set.of(DEPARTMENT_CODE))
            .interestedPublicAuthorities(List.of("Authority One", "Authority Two"))
            .actorUserId("caseworker-1")
            .isComplete(true)
            .build();
    when(inquestDataService.get(claimId)).thenReturn(Optional.of(data));

    var doc = renderDocument();

    assertInputValue(doc, "deceasedForename", "John");
    assertInputValue(doc, "deceasedSurname", "Smith");
    assertInputValue(doc, "coronersInquestReference", "REF-123");

    var selectedDepartment = selectFirst(doc, "input#department-" + DEPARTMENT_CODE);
    assertEquals("checked", selectedDepartment.attr("checked"));

    var unselectedDepartment = selectFirst(doc, "input#department-" + OTHER_DEPARTMENT_CODE);
    assertEquals("", unselectedDepartment.attr("checked"));

    assertInputValue(doc, "interestedPublicAuthorities0", "Authority One");
    assertInputValue(doc, "interestedPublicAuthorities1", "Authority Two");
  }

  @Test
  void testFailsSafelyWhenClaimIsNotInquestEligible() throws Exception {
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    session.setAttribute(claimId.toString(), claim);

    mockMvc.perform(get(mapping).session(session)).andExpect(status().isNotFound());
  }

  @Test
  void testFailsSafelyWhenFeatureFlagDisabled() throws Exception {
    doThrow(new FeatureNotEnabledException("isInquestTabEnabled is false"))
        .when(featureFlagsConfig)
        .checkEnabled(Feature.INQUEST_TAB);
    session.setAttribute(claimId.toString(), claim);

    mockMvc.perform(get(mapping).session(session)).andExpect(status().isNotFound());
  }

  private void assertInputValue(Document doc, String id, String expectedValue) {
    var input = selectFirst(doc, "#" + id);
    assertEquals(expectedValue, input.attr("value"));
  }
}
