package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.LogoutController;

@ActiveProfiles("local")
@WebMvcTest(LogoutController.class)
@Import(LocalSecurityConfig.class)
public class LogoutViewTest extends ViewTestBase {
    protected LogoutViewTest() {
        super("/logout-success");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "You are now signed out of your account");
        assertPageHasHeading(doc, "You are now signed out of your account");
        assertPageHasContent(doc, "Sign in to continue using the service.");
        assertPageHasPrimaryButton(doc, "Sign in");
    }
}
