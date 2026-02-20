package uk.gov.justice.laa.amend.claim.views;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.controllers.ClaimReviewController;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

@ActiveProfiles("local")
@WebMvcTest(ClaimReviewController.class)
@Import(LocalSecurityConfig.class)
class ReviewAndAmendViewTest extends ViewTestBase {

    @MockitoBean
    private AssessmentService assessmentService;

    @MockitoBean
    ClaimStatusHandler statusHandler;

    ReviewAndAmendViewTest() {
        this.mapping = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
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

        List<List<Element>> assessmentTable = getTable(doc, "Assessment");
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.getFirst(),
                "Assessment outcome",
                "Assessed in full",
                String.format(
                        "/submissions/%s/claims/%s/assessment-outcome#assessment-outcome", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.get(1),
                "Is this claim liable for VAT?",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome#liability-for-vat", submissionId, claimId));

        List<List<Element>> claimCostsTable = getTable(doc, "Claim costs");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.getFirst(), "Fixed fee", "£200.00", "Not applicable", "£300.00");
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(1),
                "Profit costs",
                "Not applicable",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(2),
                "Disbursements",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(3),
                "Disbursement VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements-vat", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(4),
                "Detention travel and waiting costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(5),
                "JR and form filling",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/jr-form-filling-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(6),
                "Counsel costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/counsel-costs", submissionId, claimId));
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(7), "Oral CMRH", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(8), "Telephone CMRH", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(9), "Home office interview", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(10), "Substantive hearing", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(11), "Adjourned hearing fee", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(12),
                "VAT",
                "No",
                "Yes",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId));

        List<List<Element>> totalClaimValueTable = getTable(doc, "Total claim value");
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.getFirst(), "Assessed total VAT", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.get(1), "Assessed total incl VAT", "£200.00", "£100.00", "£300.00");

        List<List<Element>> totalAllowedValueTable = getTable(doc, "Total allowed value");
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.getFirst(),
                "Allowed total VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.get(1),
                "Allowed total incl VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
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

        List<List<Element>> assessmentTable = getTable(doc, "Assessment");
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.getFirst(),
                "Assessment outcome",
                "Assessed in full",
                String.format(
                        "/submissions/%s/claims/%s/assessment-outcome#assessment-outcome", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.get(1),
                "Is this claim liable for VAT?",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome#liability-for-vat", submissionId, claimId));

        List<List<Element>> claimCostsTable = getTable(doc, "Claim costs");
        Assertions.assertEquals(7, claimCostsTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.getFirst(), "Fixed fee", "£200.00", "Not applicable", "£300.00");
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(1),
                "Profit costs",
                "Not applicable",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(2),
                "Disbursements",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(3),
                "Disbursement VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements-vat", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(4),
                "Travel costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/travel-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(5),
                "Waiting costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/waiting-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(6),
                "VAT",
                "No",
                "Yes",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId));

        List<List<Element>> totalClaimValueTable = getTable(doc, "Total claim value");
        Assertions.assertEquals(2, totalClaimValueTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.getFirst(), "Assessed total VAT", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.get(1), "Assessed total incl VAT", "£200.00", "£100.00", "£300.00");

        List<List<Element>> totalAllowedValueTable = getTable(doc, "Total allowed value");
        Assertions.assertEquals(2, totalAllowedValueTable.size());
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.getFirst(),
                "Allowed total VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.get(1),
                "Allowed total incl VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
    }

    @Test
    void testCrimeClaimPageWithAddLinks() throws Exception {
        claim = MockClaimsFunctions.createMockCrimeClaim();

        ClaimField netProfitCostField = MockClaimsFunctions.createNetProfitCostField();
        ClaimField assessedTotalVat = MockClaimsFunctions.createAssessedTotalVatField();
        ClaimField assessedTotalInclVat = MockClaimsFunctions.createAssessedTotalInclVatField();

        netProfitCostField.setAssessed(null);
        assessedTotalVat.setAssessed(null);
        assessedTotalInclVat.setAssessed(null);

        ClaimField allowedTotalVat = MockClaimsFunctions.createAllowedTotalVatField();
        ClaimField allowedTotalInclVat = MockClaimsFunctions.createAllowedTotalInclVatField();
        allowedTotalVat.setAssessed(null);
        allowedTotalInclVat.setAssessed(null);

        OutcomeType outcome = OutcomeType.REDUCED;

        claim.setAssessmentOutcome(outcome);
        claim.setFeeCode("INVC");

        claim.setNetProfitCost(netProfitCostField);
        claim.setAssessedTotalVat(assessedTotalVat);
        claim.setAssessedTotalInclVat(assessedTotalInclVat);
        claim.setAllowedTotalVat(allowedTotalVat);
        claim.setAllowedTotalInclVat(allowedTotalInclVat);

        MockClaimsFunctions.updateStatus(claim, outcome);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasPrimaryButton(doc, "Submit adjustments");
        assertPageHasSecondaryButton(doc, "Discard changes");

        List<List<Element>> assessmentTable = getTable(doc, "Assessment");
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.getFirst(),
                "Assessment outcome",
                "Reduced (still escaped)",
                String.format(
                        "/submissions/%s/claims/%s/assessment-outcome#assessment-outcome", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.get(1),
                "Is this claim liable for VAT?",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome#liability-for-vat", submissionId, claimId));

        List<List<Element>> claimCostsTable = getTable(doc, "Claim costs");
        Assertions.assertEquals(7, claimCostsTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.getFirst(), "Fixed fee", "£200.00", "Not applicable", "£300.00");
        assertTableRowContainsValuesWithAddLink(
                claimCostsTable.get(1),
                "Profit costs",
                "Not applicable",
                "£100.00",
                String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(2),
                "Disbursements",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(3),
                "Disbursement VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements-vat", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(4),
                "Travel costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/travel-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(5),
                "Waiting costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/waiting-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(6),
                "VAT",
                "No",
                "Yes",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId));

        List<List<Element>> totalClaimValueTable = getTable(doc, "Total claim value");
        Assertions.assertEquals(2, totalClaimValueTable.size());
        assertTableRowContainsValuesWithAddLink(
                totalClaimValueTable.getFirst(),
                "Assessed total VAT",
                "£200.00",
                "£100.00",
                String.format("/submissions/%s/claims/%s/assessed-totals", submissionId, claimId));
        assertTableRowContainsValuesWithAddLink(
                totalClaimValueTable.get(1),
                "Assessed total incl VAT",
                "£200.00",
                "£100.00",
                String.format("/submissions/%s/claims/%s/assessed-totals", submissionId, claimId));

        List<List<Element>> totalAllowedValueTable = getTable(doc, "Total allowed value");
        Assertions.assertEquals(2, totalAllowedValueTable.size());
        assertTableRowContainsValuesWithAddLink(
                totalAllowedValueTable.getFirst(),
                "Allowed total VAT",
                "£200.00",
                "£100.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
        assertTableRowContainsValuesWithAddLink(
                totalAllowedValueTable.get(1),
                "Allowed total incl VAT",
                "£200.00",
                "£100.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
    }

    @Test
    void testCivilClaimPageWithMissingAssessedTotals() throws Exception {
        claim = MockClaimsFunctions.createMockCivilClaim();

        ClaimField assessedTotalVat = MockClaimsFunctions.createAssessedTotalVatField();
        ClaimField assessedTotalInclVat = MockClaimsFunctions.createAssessedTotalInclVatField();

        assessedTotalVat.setAssessed(null);
        assessedTotalInclVat.setAssessed(null);

        OutcomeType outcome = OutcomeType.REDUCED;

        claim.setAssessmentOutcome(outcome);

        claim.setAssessedTotalVat(assessedTotalVat);
        claim.setAssessedTotalInclVat(assessedTotalInclVat);

        MockClaimsFunctions.updateStatus(claim, outcome);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasPrimaryButton(doc, "Submit adjustments");
        assertPageHasSecondaryButton(doc, "Discard changes");

        List<List<Element>> assessmentTable = getTable(doc, "Assessment");
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.getFirst(),
                "Assessment outcome",
                "Reduced (still escaped)",
                String.format(
                        "/submissions/%s/claims/%s/assessment-outcome#assessment-outcome", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.get(1),
                "Is this claim liable for VAT?",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome#liability-for-vat", submissionId, claimId));

        List<List<Element>> claimCostsTable = getTable(doc, "Claim costs");
        Assertions.assertEquals(13, claimCostsTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.getFirst(), "Fixed fee", "£200.00", "Not applicable", "£300.00");
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(1),
                "Profit costs",
                "Not applicable",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(2),
                "Disbursements",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(3),
                "Disbursement VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements-vat", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(4),
                "Detention travel and waiting costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(5),
                "JR and form filling",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/jr-form-filling-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(6),
                "Counsel costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/counsel-costs", submissionId, claimId));
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(7), "Oral CMRH", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(8), "Telephone CMRH", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(9), "Home office interview", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(10), "Substantive hearing", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(11), "Adjourned hearing fee", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(12),
                "VAT",
                "No",
                "Yes",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId));

        List<List<Element>> totalClaimValueTable = getTable(doc, "Total claim value");
        Assertions.assertEquals(2, totalClaimValueTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.getFirst(), "Assessed total VAT", "£200.00", "£100.00", "");
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.get(1), "Assessed total incl VAT", "£200.00", "£100.00", "");

        List<List<Element>> totalAllowedValueTable = getTable(doc, "Total allowed value");
        Assertions.assertEquals(2, totalAllowedValueTable.size());
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.getFirst(),
                "Allowed total VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.get(1),
                "Allowed total incl VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
    }

    @Test
    void testPageWithSubmissionError() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        WebClientResponseException exception =
                WebClientResponseException.create(500, "Something went wrong", null, null, null);

        when(assessmentService.submitAssessment(any(), any())).thenThrow(exception);

        Document doc = renderDocumentWithErrors(params);

        assertPageHasErrorAlert(doc);
    }

    @Test
    void testPageWithValidationError() throws Exception {
        ClaimField claimField = MockClaimsFunctions.createNetProfitCostField();
        claimField.setAssessed(null);
        claim.setNetProfitCost(claimField);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        Document doc = renderDocumentWithErrors(params);

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasErrorSummary(doc, "profit-cost");
    }

    @Test
    void testCivilClaimPageWithBoltOnsWithNullCalculatedValue() throws Exception {
        ClaimField cmrhOralField = MockClaimsFunctions.createCmrhOralField();
        ClaimField cmrhTelephoneField = MockClaimsFunctions.createCmrhTelephoneField();
        ClaimField hoInterviewField = MockClaimsFunctions.createHoInterviewField();

        cmrhOralField.setCalculated(null);
        cmrhTelephoneField.setCalculated(null);
        hoInterviewField.setCalculated(null);

        ClaimField substantiveHearingField = MockClaimsFunctions.createSubstantiveHearingField();
        ClaimField adjournedHearingField = MockClaimsFunctions.createAdjournedHearingField();
        substantiveHearingField.setCalculated(null);
        adjournedHearingField.setCalculated(null);

        cmrhOralField.setAssessed(null);
        cmrhTelephoneField.setAssessed(null);
        hoInterviewField.setAssessed(null);
        substantiveHearingField.setAssessed(null);
        adjournedHearingField.setAssessed(null);

        claim = MockClaimsFunctions.createMockCivilClaim();
        CivilClaimDetails civilClaim = (CivilClaimDetails) claim;
        civilClaim.setCmrhOral(cmrhOralField);
        civilClaim.setCmrhTelephone(cmrhTelephoneField);
        civilClaim.setHoInterview(hoInterviewField);
        civilClaim.setSubstantiveHearing(substantiveHearingField);
        civilClaim.setAdjournedHearing(adjournedHearingField);

        claim = civilClaim;

        OutcomeType outcome = OutcomeType.PAID_IN_FULL;
        claim.setAssessmentOutcome(outcome);
        MockClaimsFunctions.updateStatus(claim, outcome);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasPrimaryButton(doc, "Submit adjustments");
        assertPageHasSecondaryButton(doc, "Discard changes");

        List<List<Element>> assessmentTable = getTable(doc, "Assessment");
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.getFirst(),
                "Assessment outcome",
                "Assessed in full",
                String.format(
                        "/submissions/%s/claims/%s/assessment-outcome#assessment-outcome", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.get(1),
                "Is this claim liable for VAT?",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome#liability-for-vat", submissionId, claimId));

        List<List<Element>> claimCostsTable = getTable(doc, "Claim costs");
        Assertions.assertEquals(13, claimCostsTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.getFirst(), "Fixed fee", "£200.00", "Not applicable", "£300.00");
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(1),
                "Profit costs",
                "Not applicable",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(2),
                "Disbursements",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(3),
                "Disbursement VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements-vat", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(4),
                "Detention travel and waiting costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(5),
                "JR and form filling",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/jr-form-filling-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(6),
                "Counsel costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/counsel-costs", submissionId, claimId));
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(7), "Oral CMRH", "Not applicable", "£100.00", "Not applicable");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(8), "Telephone CMRH", "Not applicable", "£100.00", "Not applicable");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(9), "Home office interview", "Not applicable", "£100.00", "Not applicable");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(10), "Substantive hearing", "Not applicable", "£100.00", "Not applicable");
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.get(11), "Adjourned hearing fee", "Not applicable", "£100.00", "Not applicable");
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(12),
                "VAT",
                "No",
                "Yes",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId));

        List<List<Element>> totalClaimValueTable = getTable(doc, "Total claim value");
        Assertions.assertEquals(2, totalClaimValueTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.getFirst(), "Assessed total VAT", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.get(1), "Assessed total incl VAT", "£200.00", "£100.00", "£300.00");

        List<List<Element>> totalAllowedValueTable = getTable(doc, "Total allowed value");
        Assertions.assertEquals(2, totalAllowedValueTable.size());
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.getFirst(),
                "Allowed total VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.get(1),
                "Allowed total incl VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
    }

    @Test
    void testCivilClaimPageWithBoltOnsWithNullSubmittedValue() throws Exception {

        ClaimField cmrhOralField = MockClaimsFunctions.createCmrhOralField();
        ClaimField cmrhTelephoneField = MockClaimsFunctions.createCmrhTelephoneField();
        ClaimField hoInterviewField = MockClaimsFunctions.createHoInterviewField();

        cmrhOralField.setSubmitted(null);
        cmrhTelephoneField.setSubmitted(null);
        hoInterviewField.setSubmitted(null);

        ClaimField substantiveHearingField = MockClaimsFunctions.createSubstantiveHearingField();
        ClaimField adjournedHearingField = MockClaimsFunctions.createAdjournedHearingField();
        substantiveHearingField.setSubmitted(null);
        adjournedHearingField.setSubmitted(null);

        claim = MockClaimsFunctions.createMockCivilClaim();
        CivilClaimDetails civilClaim = (CivilClaimDetails) claim;
        civilClaim.setCmrhOral(cmrhOralField);
        civilClaim.setCmrhTelephone(cmrhTelephoneField);
        civilClaim.setHoInterview(hoInterviewField);
        civilClaim.setSubstantiveHearing(substantiveHearingField);
        civilClaim.setAdjournedHearing(adjournedHearingField);

        claim = civilClaim;

        OutcomeType outcome = OutcomeType.PAID_IN_FULL;
        claim.setAssessmentOutcome(outcome);
        MockClaimsFunctions.updateStatus(claim, outcome);

        Document doc = renderDocument();

        assertPageHasTitle(doc, "Review and amend");

        assertPageHasHeading(doc, "Review and amend");

        assertPageHasPrimaryButton(doc, "Submit adjustments");
        assertPageHasSecondaryButton(doc, "Discard changes");

        List<List<Element>> assessmentTable = getTable(doc, "Assessment");
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.getFirst(),
                "Assessment outcome",
                "Assessed in full",
                String.format(
                        "/submissions/%s/claims/%s/assessment-outcome#assessment-outcome", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                assessmentTable.get(1),
                "Is this claim liable for VAT?",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome#liability-for-vat", submissionId, claimId));

        List<List<Element>> claimCostsTable = getTable(doc, "Claim costs");
        Assertions.assertEquals(8, claimCostsTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                claimCostsTable.getFirst(), "Fixed fee", "£200.00", "Not applicable", "£300.00");
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(1),
                "Profit costs",
                "Not applicable",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(2),
                "Disbursements",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(3),
                "Disbursement VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/disbursements-vat", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(4),
                "Detention travel and waiting costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/detention-travel-and-waiting-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(5),
                "JR and form filling",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/jr-form-filling-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(6),
                "Counsel costs",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/counsel-costs", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                claimCostsTable.get(7),
                "VAT",
                "No",
                "Yes",
                "Yes",
                String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId));

        List<List<Element>> totalClaimValueTable = getTable(doc, "Total claim value");
        Assertions.assertEquals(2, totalClaimValueTable.size());
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.getFirst(), "Assessed total VAT", "£200.00", "£100.00", "£300.00");
        assertTableRowContainsValuesWithNoChangeLink(
                totalClaimValueTable.get(1), "Assessed total incl VAT", "£200.00", "£100.00", "£300.00");

        List<List<Element>> totalAllowedValueTable = getTable(doc, "Total allowed value");
        Assertions.assertEquals(2, totalAllowedValueTable.size());
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.getFirst(),
                "Allowed total VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
        assertTableRowContainsValuesWithChangeLink(
                totalAllowedValueTable.get(1),
                "Allowed total incl VAT",
                "£200.00",
                "£100.00",
                "£300.00",
                String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId));
    }
}
