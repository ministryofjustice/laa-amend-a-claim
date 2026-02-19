package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.Mockito.when;

import org.jsoup.nodes.Document;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.exceptions.ErrorPageController;
import uk.gov.justice.laa.amend.claim.factories.ReferenceNumberFactory;

@ActiveProfiles("local")
@WebMvcTest(ErrorPageController.class)
@Import(LocalSecurityConfig.class)
public class ErrorViewTest extends ViewTestBase {

    @MockitoBean
    private ReferenceNumberFactory referenceNumberFactory;

    ErrorViewTest() {
        super("/error");
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 500, 503})
    void testPage(int requestStatus) throws Exception {
        when(referenceNumberFactory.create()).thenReturn("123456");

        Document doc = renderErrorPage(requestStatus, 500);

        assertPageHasTitle(doc, "Sorry, there's a problem with this service");

        assertPageHasHeading(doc, "Sorry, there's a problem with this service");

        assertPageHasContent(doc, "Try again later.");

        assertPageHasContent(doc, "This error has been logged and forwarded for investigation.");

        assertPageHasContent(
                doc, "Contact the Amend a Bulk Claim digital team quoting reference 123456 if you have any questions.");

        assertPageHasLink(
                doc,
                "email",
                "Amend a Bulk Claim digital team",
                "mailto:someone@example.com?subject=AaBC issue reference: 123456");
    }
}
