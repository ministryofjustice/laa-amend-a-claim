package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.MaintenancePageController;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafLiteralString;

@WebMvcTest(MaintenancePageController.class)
class MaintenanceViewTest extends ViewTestBase {

  @Autowired private MaintenanceService maintenanceService;

  MaintenanceViewTest() {
    this.mapping = "/maintenance";
  }

  @Test
  void testPage() throws Exception {
    when(maintenanceService.maintenanceApplies(any())).thenReturn(true);
    when(maintenanceService.getTitle())
        .thenReturn(new ThymeleafLiteralString("Service maintenance"));
    when(maintenanceService.getMessage())
        .thenReturn(new ThymeleafLiteralString("Expected return: 9:00"));

    Document doc = renderDocument();

    assertPageHasTitle(doc, "Service maintenance");
    assertPageHasHeading(doc, "Service maintenance");
    assertPageBodyText(doc, "Expected return: 9:00");

    assertPageHasNoActiveServiceNavigationItems(doc);
  }
}
