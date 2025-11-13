package uk.gov.justice.laa.amend.claim.views;

import jakarta.servlet.http.HttpSession;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.AssessmentOutcomeController;
import uk.gov.justice.laa.amend.claim.controllers.ClaimSummaryController;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("local")
@WebMvcTest(AssessmentOutcomeController.class)
@Import(LocalSecurityConfig.class)
class AssessmentOutcomeViewTest extends ViewTestBase {

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimResultMapper claimResultMapper;

    AssessmentOutcomeViewTest() {
        super("/submissions/submissionId/claims/claimId/assessment-outcome");
    }

    @Test
    void testPage() throws Exception {
        Document doc = renderDocument();

        assertPageHasTitle(doc, "Assessment Outcome");

        assertPageHasHeading(doc, "Assessment Outcome");
        assertPageHasSecondaryButton(doc);
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);

    }

    @Test
    void testPageErrors() throws Exception {
        Document doc = renderRedirect("/submissions/123/claims/456/assessment-outcome");

        assertPageHasTitle(doc, "Assessment Outcome");

        assertPageHasHeading(doc, "Assessment Outcome");
        assertPageHasSecondaryButton(doc);
        assertPageHasNoActiveServiceNavigationItems(doc);
        assertPageHasRadioButtons(doc);
        assertPageHasInlineRadioButtons(doc);

        assertPageHasErrorSummary(doc,
                "assessment-outcome",
                "liability-for-vat"
        );
    }

}
