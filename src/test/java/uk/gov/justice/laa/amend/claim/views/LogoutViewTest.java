package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.controllers.LogoutController;

@WebMvcTest(LogoutController.class)
public class LogoutViewTest extends ViewTestBase {
    protected LogoutViewTest() {
        this.mapping = "/logout-success";
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
