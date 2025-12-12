package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.AssessmentStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

import java.time.YearMonth;
import java.util.List;

public class CrimeClaimDetailsViewTest {

    @Nested
    class GetAccountNumberTests {
        @Test
        void getAccountNumberPicksOutFirstPartOfScheduleReference() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setScheduleReference("0U733A/2018/02");
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertEquals("0U733A", viewModel.getAccountNumber());
        }

        @Test
        void getAccountNumberReturnsScheduleReferenceIfUnexpectedFormat() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setScheduleReference("0U733A201802");
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertEquals("0U733A201802", viewModel.getAccountNumber());
        }
    }

    @Nested
    class GetAssessedTotalsTests {
        @Test
        void getAssessedTotalsHandlesNullFields() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);

            List<ClaimField> result = viewModel.getAssessedTotals();

            Assertions.assertEquals(List.of(), result);
        }

        @Test
        void getAssessedTotalsHandlesValidFields() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setAssessedTotalVat(createClaimField("assessedTotalVat", AssessmentStatus.ASSESSABLE));
            claim.setAssessedTotalInclVat(createClaimField("assessedTotalInclVat", AssessmentStatus.ASSESSABLE));
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);

            List<ClaimField> result = viewModel.getAssessedTotals();

            Assertions.assertEquals(claim.getAssessedTotalVat(), result.get(0));
            Assertions.assertEquals(claim.getAssessedTotalInclVat(), result.get(1));
        }

        @Test
        void getAssessedTotalsHandlesValidFieldsWithDoNotDisplayStatus() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setAssessedTotalVat(createClaimField("assessedTotalVat", AssessmentStatus.DO_NOT_DISPLAY));
            claim.setAssessedTotalInclVat(createClaimField("assessedTotalInclVat", AssessmentStatus.DO_NOT_DISPLAY));
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);

            List<ClaimField> result = viewModel.getAssessedTotals();

            Assertions.assertEquals(List.of(), result);
        }
    }

    @Nested
    class GetAllowedTotalsTests {
        @Test
        void getAllowedTotalsHandlesNull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);

            List<ClaimField> result = viewModel.getAllowedTotals();

            Assertions.assertEquals(List.of(), result);
        }

        @Test
        void getAllowedTotalsHandlesValid() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setAllowedTotalVat(createClaimField("allowedTotalVat", AssessmentStatus.ASSESSABLE));
            claim.setAllowedTotalInclVat(createClaimField("allowedTotalInclVat", AssessmentStatus.ASSESSABLE));
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);

            List<ClaimField> result = viewModel.getAllowedTotals();

            Assertions.assertEquals(claim.getAllowedTotalVat(), result.get(0));
            Assertions.assertEquals(claim.getAllowedTotalInclVat(), result.get(1));
        }
    }

    @Nested
    class GetSubmissionPeriodForDisplayTests {
        @Test
        void getSubmissionPeriodForDisplayHandlesNull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertNull(viewModel.getSubmissionPeriodForDisplay());
        }

        @Test
        void getSubmissionPeriodForDisplayFormatsDate() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertEquals("Jan 2020", viewModel.getSubmissionPeriodForDisplay());
        }
    }

    @Nested
    class GetSubmissionPeriodForSortingTests {
        @Test
        void getSubmissionPeriodForSortingHandlesNull() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertEquals(0, viewModel.getSubmissionPeriodForSorting());
        }

        @Test
        void getSubmissionPeriodForSortingGetsEpochValueOfDate() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertEquals(18262, viewModel.getSubmissionPeriodForSorting());
        }
    }

    @Nested
    class getClientNameTests {
        @Test
        void getClientNameHandlesNullForenameAndSurname() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertNull(viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesNullSurname() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setClientForename("John");
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertEquals("John", viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesNullForename() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setClientSurname("Doe");
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertEquals("Doe", viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesFullName() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setClientForename("John");
            claim.setClientSurname("Doe");
            ClaimDetailsView<CrimeClaimDetails> viewModel = new CrimeClaimDetailsView(claim);
            Assertions.assertEquals("John Doe", viewModel.getClientName());
        }
    }

    @Nested
    class GetTableRowsTests {
        @Test
        void rowsRenderedForClaimValues() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField travel = new ClaimField("5", null, null);
            ClaimField waiting = new ClaimField("6", null, null);

            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setTravelCosts(travel);
            claim.setWaitingCosts(waiting);

            CrimeClaimDetailsView viewModel = new CrimeClaimDetailsView(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                travel,
                waiting
            );
            Assertions.assertEquals(expectedRows, viewModel.getTableRows());
        }
    }

    @Nested
    class GetErrorTests {

        @Test
        void convertFieldsThatNeedAmendingIntoErrors() {
            CrimeClaimDetails claim = new CrimeClaimDetails();
            claim.setNetProfitCost(createClaimField("profitCost", AssessmentStatus.ASSESSABLE));
            claim.setTravelCosts(createClaimField("travel", AssessmentStatus.ASSESSABLE));
            claim.setWaitingCosts(createClaimField("waiting", AssessmentStatus.ASSESSABLE));
            claim.setAssessedTotalVat(createClaimField("assessedTotalVat", AssessmentStatus.ASSESSABLE));
            claim.setAssessedTotalInclVat(createClaimField("assessedTotalInclVat", AssessmentStatus.ASSESSABLE));
            claim.setAllowedTotalVat(createClaimField("allowedTotalVat", AssessmentStatus.ASSESSABLE));
            claim.setAllowedTotalInclVat(createClaimField("allowedTotalInclVat", AssessmentStatus.ASSESSABLE));
            CrimeClaimDetailsView viewModel = new CrimeClaimDetailsView(claim);

            List<ReviewAndAmendFormError> expectedErrors = List.of(
                new ReviewAndAmendFormError("profit-cost", "claimSummary.rows.profitCost.error"),
                new ReviewAndAmendFormError("assessed-total-vat", "claimSummary.rows.assessedTotalVat.error"),
                new ReviewAndAmendFormError("assessed-total-incl-vat", "claimSummary.rows.assessedTotalInclVat.error"),
                new ReviewAndAmendFormError("allowed-total-vat", "claimSummary.rows.allowedTotalVat.error"),
                new ReviewAndAmendFormError("allowed-total-incl-vat", "claimSummary.rows.allowedTotalInclVat.error")
            );

            Assertions.assertEquals(expectedErrors, viewModel.getErrors());
        }
    }

    public static ClaimField createClaimField(String key, AssessmentStatus status) {
        ClaimField claimField = new ClaimField();
        claimField.setKey(key);
        claimField.setStatus(status);
        return claimField;
    }
}
