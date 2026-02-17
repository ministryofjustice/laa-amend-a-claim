package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.LogoutController;

@ActiveProfiles("local")
@WebMvcTest(LogoutController.class)
@Import(LocalSecurityConfig.class)
public class SessionExpiredViewTest extends ViewTestBase {
    protected SessionExpiredViewTest() {
        super("/logout-success?message=expired");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "You are now signed out of your account");
        assertPageHasContent(doc, "For your security, we signed you out");
        assertPageHasContent(doc, "This is because you were inactive for 15 minutes.");
        assertPageHasPrimaryButton(doc, "Sign in");
    }
}
