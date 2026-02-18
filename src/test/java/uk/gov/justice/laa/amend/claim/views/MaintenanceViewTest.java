package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.exceptions.ErrorPageController;
import uk.gov.justice.laa.amend.claim.factories.ReferenceNumberFactory;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafLiteralString;

@ActiveProfiles("local")
@WebMvcTest(ErrorPageController.class)
@Import(LocalSecurityConfig.class)
class MaintenanceViewTest extends ViewTestBase {

    @Autowired
    private MaintenanceService maintenanceService;

    @MockitoBean
    private ReferenceNumberFactory referenceNumberFactory;

    MaintenanceViewTest() {
        super("/error");
    }

    @Test
    void testPage() throws Exception {
        when(maintenanceService.maintenanceEnabled()).thenReturn(true);
        when(maintenanceService.getTitle()).thenReturn(new ThymeleafLiteralString("Service maintenance"));
        when(maintenanceService.getMessage()).thenReturn(new ThymeleafLiteralString("Expected return: 9:00"));

        Document doc = renderErrorPage(503, 503);

        assertPageHasTitle(doc, "Service maintenance");
        assertPageHasHeading(doc, "Service maintenance");
        assertPageBodyText(doc, "Expected return: 9:00");

        assertPageHasNoActiveServiceNavigationItems(doc);
    }
}
