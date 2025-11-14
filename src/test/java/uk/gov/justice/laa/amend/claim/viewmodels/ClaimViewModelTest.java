package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;

import java.time.LocalDate;
import java.time.YearMonth;

public class ClaimViewModelTest {

    @Nested
    class GetAccountNumberTests {
        @Test
        void getAccountNumberPicksOutFirstPartOfScheduleReference() {
            CivilClaim claim = new CivilClaim();
            claim.setScheduleReference("0U733A/2018/02");
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("0U733A", viewModel.getAccountNumber());
        }

        @Test
        void getAccountNumberReturnsScheduleReferenceIfUnexpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setScheduleReference("0U733A201802");
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("0U733A201802", viewModel.getAccountNumber());
        }
    }

    @Nested
    class GetCaseStartDateForDisplayTests {
        @Test
        void getCaseStartDateForDisplayHandlesNull() {
            CivilClaim claim = new CivilClaim();
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(viewModel.getCaseStartDateForDisplay());
        }

        @Test
        void getCaseStartDateForDisplayFormatsDate() {
            CivilClaim claim = new CivilClaim();
            claim.setCaseStartDate(LocalDate.of(2020, 1, 1));
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("01 Jan 2020", viewModel.getCaseStartDateForDisplay());
        }
    }

    @Nested
    class GetCaseEndDateForDisplayTests {
        @Test
        void getCaseEndDateForDisplayHandlesNull() {
            CivilClaim claim = new CivilClaim();
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(viewModel.getCaseEndDateForDisplay());
        }

        @Test
        void getCaseEndDateForDisplayFormatsDate() {
            CivilClaim claim = new CivilClaim();
            claim.setCaseEndDate(LocalDate.of(2020, 1, 1));
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("01 Jan 2020", viewModel.getCaseEndDateForDisplay());
        }
    }

    @Nested
    class GetSubmissionPeriodForDisplayTests {
        @Test
        void getSubmissionPeriodForDisplayHandlesNull() {
            CivilClaim claim = new CivilClaim();
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(viewModel.getSubmissionPeriodForDisplay());
        }

        @Test
        void getSubmissionPeriodForDisplayFormatsDate() {
            CivilClaim claim = new CivilClaim();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("Jan 2020", viewModel.getSubmissionPeriodForDisplay());
        }
    }

    @Nested
    class GetSubmissionPeriodForSortingTests {
        @Test
        void getSubmissionPeriodForSortingHandlesNull() {
            CivilClaim claim = new CivilClaim();
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals(0, viewModel.getSubmissionPeriodForSorting());
        }

        @Test
        void getSubmissionPeriodForSortingGetsEpochValueOfDate() {
            CivilClaim claim = new CivilClaim();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals(18262, viewModel.getSubmissionPeriodForSorting());
        }
    }

    @Nested
    class GetEscapedForSortingTests {
        @Test
        void getEscapedForSortingHandlesNull() {
            CivilClaim claim = new CivilClaim();
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("service.no", viewModel.getEscapedForDisplay());
        }

        @Test
        void getEscapedForDisplayHandlesTrue() {
            CivilClaim claim = new CivilClaim();
            claim.setEscaped(true);
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("service.yes", viewModel.getEscapedForDisplay());
        }

        @Test
        void getEscapedForDisplayHandlesFalse() {
            CivilClaim claim = new CivilClaim();
            claim.setEscaped(false);
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("service.no", viewModel.getEscapedForDisplay());
        }
    }

    @Nested
    class getClientNameTests {
        @Test
        void getClientNameHandlesNullForenameAndSurname() {
            CivilClaim claim = new CivilClaim();
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesNullSurname() {
            CivilClaim claim = new CivilClaim();
            claim.setClientForename("John");
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("John", viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesNullForename() {
            CivilClaim claim = new CivilClaim();
            claim.setClientSurname("Doe");
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("Doe", viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesFullName() {
            CivilClaim claim = new CivilClaim();
            claim.setClientForename("John");
            claim.setClientSurname("Doe");
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("John Doe", viewModel.getClientName());
        }
    }
}
