package uk.gov.justice.laa.amend.claim.views.amendments;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.amendments.CaseTypeController;

@WebMvcTest(CaseTypeController.class)
class ViewCaseTypeViewTest extends AmendmentsBaseTest {

  private static final String FEE_CODE = "feecode";
  private static final String MATTER_TYPE_CODE = "matter";
  private static final String MATTER_TYPE_CODE_1 = "matterone";
  private static final String MATTER_TYPE_CODE_2 = "mattertwo";

  ViewCaseTypeViewTest() {
    this.mapping = caseUrl;
  }

  @Test
  void testShowsUnamendedCivilCaseDetails() {
    // TODO: Implement view test
    assertThat(false).isTrue();
  }
}
