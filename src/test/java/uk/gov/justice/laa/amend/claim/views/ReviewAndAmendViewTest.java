package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ClaimReviewController;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("local")
@WebMvcTest(ClaimReviewController.class)
@Import(LocalSecurityConfig.class)
class ReviewAndAmendViewTest extends ViewTestBase {

    @MockitoBean
    private AssessmentService assessmentService;

    @MockitoBean
    ClaimStatusHandler statusHandler;

    ReviewAndAmendViewTest() {
        super("/submissions/submissionId/claims/claimId/review");
    }

    @Test
    void testCivilClaimPage() throws Exception {
        claim = MockClaimsFunctions.createMockCivilClaim();
        OutcomeType outcome = OutcomeType.PAID_IN_FULL;
        claim.setAssessmentOutcome(outcome);
        MockClaimsFunctions.updateStatus(claim, outcome);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasPrimaryButton(doc, "Submit adjustments");
        assertPageHasSecondaryButton(doc, "Discard changes");

        List<List<Element>> table1 = getTable(doc, "Claim costs");
        assertTableRowContainsValuesWithNoChangeLink(table1.getFirst(), "Fixed fee", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithChangeLink(table1.get(1), "Profit costs", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/profit-costs");
        assertTableRowContainsValuesWithChangeLink(table1.get(2), "Disbursements", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/disbursements");
        assertTableRowContainsValuesWithChangeLink(table1.get(3), "Disbursement VAT", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/disbursements-vat");
        assertTableRowContainsValuesWithChangeLink(table1.get(4), "Detention travel and waiting costs", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/detention-travel-and-waiting-costs");
        assertTableRowContainsValuesWithChangeLink(table1.get(5), "JR and form filling", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/jr-form-filling-costs");
        assertTableRowContainsValuesWithChangeLink(table1.get(6), "Counsel costs", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/counsel-costs");
        assertTableRowContainsValuesWithNoChangeLink(table1.get(7), "Oral CMRH", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(table1.get(8), "Telephone CMRH", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(table1.get(9), "Home office interview", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(table1.get(10), "Substantive hearing", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(table1.get(11), "Adjourned hearing fee", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithChangeLink(table1.get(12), "VAT", "No", "Yes", "Yes", "/submissions/submissionId/claims/claimId/assessment-outcome");

        List<List<Element>> table2 = getTable(doc, "Total claim value");
        assertTableRowContainsValuesWithNoChangeLink(table2.getFirst(), "Assessed total VAT", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(table2.get(1), "Assessed total incl VAT", "£200.00", "£100.00", "£300.00");

        List<List<Element>> table3 = getTable(doc, "Total allowed value");
        assertTableRowContainsValuesWithChangeLink(table3.getFirst(), "Allowed total VAT", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/allowed-totals");
        assertTableRowContainsValuesWithChangeLink(table3.get(1), "Allowed total incl VAT", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/allowed-totals");
    }

    @Test
    void testCrimeClaimPage() throws Exception {
        claim = MockClaimsFunctions.createMockCrimeClaim();
        OutcomeType outcome = OutcomeType.PAID_IN_FULL;
        claim.setAssessmentOutcome(outcome);
        MockClaimsFunctions.updateStatus(claim, outcome);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasPrimaryButton(doc, "Submit adjustments");
        assertPageHasSecondaryButton(doc, "Discard changes");

        List<List<Element>> table1 = getTable(doc, "Claim costs");
        assertTableRowContainsValuesWithNoChangeLink(table1.getFirst(), "Fixed fee", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithChangeLink(table1.get(1), "Profit costs", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/profit-costs");
        assertTableRowContainsValuesWithChangeLink(table1.get(2), "Disbursements", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/disbursements");
        assertTableRowContainsValuesWithChangeLink(table1.get(3), "Disbursement VAT", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/disbursements-vat");
        assertTableRowContainsValuesWithChangeLink(table1.get(4), "Travel costs", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/travel-costs");
        assertTableRowContainsValuesWithChangeLink(table1.get(5), "Waiting costs", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/waiting-costs");
        assertTableRowContainsValuesWithChangeLink(table1.get(6), "VAT", "No", "Yes", "Yes", "/submissions/submissionId/claims/claimId/assessment-outcome");

        List<List<Element>> table2 = getTable(doc, "Total claim value");
        assertTableRowContainsValuesWithNoChangeLink(table2.getFirst(), "Assessed total VAT", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(table2.get(1), "Assessed total incl VAT", "£200.00", "£100.00", "£300.00");

        List<List<Element>> table3 = getTable(doc, "Total allowed value");
        assertTableRowContainsValuesWithChangeLink(table3.getFirst(), "Allowed total VAT", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/allowed-totals");
        assertTableRowContainsValuesWithChangeLink(table3.get(1), "Allowed total incl VAT", "£200.00", "£100.00", "£300.00", "/submissions/submissionId/claims/claimId/allowed-totals");
    }

    @Test
    void testPageWithSubmissionError() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        WebClientResponseException exception = WebClientResponseException
            .create(500, "Something went wrong", null, null, null);

        when(assessmentService.submitAssessment(any(), any()))
            .thenThrow(exception);

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorAlert(doc);
    }

    @Test
    void testPageWithValidationError() throws Exception {
        ClaimField claimField = new ClaimField();
        claimField.setKey("profitCosts");
        claimField.setStatus(ClaimFieldStatus.MODIFIABLE);
        claim.setNetProfitCost(claimField);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        Document doc = renderDocumentWithErrors(params);

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasErrorSummary(doc, "profit-costs");
    }
}
