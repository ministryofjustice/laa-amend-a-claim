package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaim;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CrimeClaimViewModelTest {

    @Nested
    class GetAccountNumberTests {
        @Test
        void getAccountNumberPicksOutFirstPartOfScheduleReference() {
            CrimeClaim claim = new CrimeClaim();
            claim.setScheduleReference("0U733A/2018/02");
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("0U733A", viewModel.getAccountNumber());
        }

        @Test
        void getAccountNumberReturnsScheduleReferenceIfUnexpectedFormat() {
            CrimeClaim claim = new CrimeClaim();
            claim.setScheduleReference("0U733A201802");
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("0U733A201802", viewModel.getAccountNumber());
        }
    }

    @Nested
    class GetCaseStartDateForDisplayTests {
        @Test
        void getCaseStartDateForDisplayHandlesNull() {
            CrimeClaim claim = new CrimeClaim();
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertNull(viewModel.getCaseStartDateForDisplay());
        }

        @Test
        void getCaseStartDateForDisplayFormatsDate() {
            CrimeClaim claim = new CrimeClaim();
            claim.setCaseStartDate(LocalDate.of(2020, 1, 1));
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("01 Jan 2020", viewModel.getCaseStartDateForDisplay());
        }
    }

    @Nested
    class GetCaseEndDateForDisplayTests {
        @Test
        void getCaseEndDateForDisplayHandlesNull() {
            CrimeClaim claim = new CrimeClaim();
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertNull(viewModel.getCaseEndDateForDisplay());
        }

        @Test
        void getCaseEndDateForDisplayFormatsDate() {
            CrimeClaim claim = new CrimeClaim();
            claim.setCaseEndDate(LocalDate.of(2020, 1, 1));
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("01 Jan 2020", viewModel.getCaseEndDateForDisplay());
        }
    }

    @Nested
    class GetSubmissionPeriodForDisplayTests {
        @Test
        void getSubmissionPeriodForDisplayHandlesNull() {
            CrimeClaim claim = new CrimeClaim();
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertNull(viewModel.getSubmissionPeriodForDisplay());
        }

        @Test
        void getSubmissionPeriodForDisplayFormatsDate() {
            CrimeClaim claim = new CrimeClaim();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("Jan 2020", viewModel.getSubmissionPeriodForDisplay());
        }
    }

    @Nested
    class GetSubmissionPeriodForSortingTests {
        @Test
        void getSubmissionPeriodForSortingHandlesNull() {
            CrimeClaim claim = new CrimeClaim();
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals(0, viewModel.getSubmissionPeriodForSorting());
        }

        @Test
        void getSubmissionPeriodForSortingGetsEpochValueOfDate() {
            CrimeClaim claim = new CrimeClaim();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals(18262, viewModel.getSubmissionPeriodForSorting());
        }
    }

    @Nested
    class GetEscapedForSortingTests {
        @Test
        void getEscapedForSortingHandlesNull() {
            CrimeClaim claim = new CrimeClaim();
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("service.no", viewModel.getEscapedForDisplay());
        }

        @Test
        void getEscapedForDisplayHandlesTrue() {
            CrimeClaim claim = new CrimeClaim();
            claim.setEscaped(true);
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("service.yes", viewModel.getEscapedForDisplay());
        }

        @Test
        void getEscapedForDisplayHandlesFalse() {
            CrimeClaim claim = new CrimeClaim();
            claim.setEscaped(false);
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("service.no", viewModel.getEscapedForDisplay());
        }
    }

    @Nested
    class getClientNameTests {
        @Test
        void getClientNameHandlesNullForenameAndSurname() {
            CrimeClaim claim = new CrimeClaim();
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertNull(viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesNullSurname() {
            CrimeClaim claim = new CrimeClaim();
            claim.setClientForename("John");
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("John", viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesNullForename() {
            CrimeClaim claim = new CrimeClaim();
            claim.setClientSurname("Doe");
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
            Assertions.assertEquals("Doe", viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesFullName() {
            CrimeClaim claim = new CrimeClaim();
            claim.setClientForename("John");
            claim.setClientSurname("Doe");
            ClaimViewModel<CrimeClaim> viewModel = new CrimeClaimViewModel(claim);
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
            ClaimField vatClaimed = new ClaimField("7", null, null);
            ClaimField totalAmount = new ClaimField("8", null, null);

            CrimeClaim claim = new CrimeClaim();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setTravelCosts(travel);
            claim.setWaitingCosts(waiting);
            claim.setVatClaimed(vatClaimed);
            claim.setTotalAmount(totalAmount);

            CrimeClaimViewModel viewModel = new CrimeClaimViewModel(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                travel,
                waiting,
                vatClaimed,
                totalAmount
            );
            Assertions.assertEquals(expectedRows, viewModel.getTableRows());
        }
    }
}
