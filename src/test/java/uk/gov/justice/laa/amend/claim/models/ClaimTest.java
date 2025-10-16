package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

public class ClaimTest {

    @Nested
    class GetAccountNumberTests {
        @Test
        void getAccountNumberPicksOutFirstPartOfScheduleReference() {
            Claim claim = new Claim();
            claim.setScheduleReference("0U733A/2018/02");
            Assertions.assertEquals("0U733A", claim.getAccountNumber());
        }

        @Test
        void getAccountNumberReturnsScheduleReferenceIfUnexpectedFormat() {
            Claim claim = new Claim();
            claim.setScheduleReference("0U733A201802");
            Assertions.assertEquals("0U733A201802", claim.getAccountNumber());
        }
    }

    @Nested
    class GetReferenceNumberTests {
        @Test
        void getReferenceNumberReturnsUFN() {
            Claim claim = new Claim();
            claim.setUniqueFileNumber("UFN");
            Assertions.assertEquals("UFN", claim.getReferenceNumber());
        }

        @Test
        void getReferenceNumberReturnsCRNIfURNIsNull() {
            Claim claim = new Claim();
            claim.setCaseReferenceNumber("CRN");
            Assertions.assertEquals("CRN", claim.getReferenceNumber());
        }
    }

    @Nested
    class GetCaseStartDateForDisplayTests {
        @Test
        void getCaseStartDateForDisplayHandlesNull() {
            Claim claim = new Claim();
            Assertions.assertNull(claim.getCaseStartDateForDisplay());
        }

        @Test
        void getCaseStartDateForDisplayFormatsDate() {
            Claim claim = new Claim();
            claim.setCaseStartDate(LocalDate.of(2020, 1, 1));
            Assertions.assertEquals("01 Jan 2020", claim.getCaseStartDateForDisplay());
        }
    }

    @Nested
    class GetCaseEndDateForDisplayTests {
        @Test
        void getCaseEndDateForDisplayHandlesNull() {
            Claim claim = new Claim();
            Assertions.assertNull(claim.getCaseEndDateForDisplay());
        }

        @Test
        void getCaseEndDateForDisplayFormatsDate() {
            Claim claim = new Claim();
            claim.setCaseEndDate(LocalDate.of(2020, 1, 1));
            Assertions.assertEquals("01 Jan 2020", claim.getCaseEndDateForDisplay());
        }
    }

    @Nested
    class GetSubmissionPeriodForDisplayTests {
        @Test
        void getSubmissionPeriodForDisplayHandlesNull() {
            Claim claim = new Claim();
            Assertions.assertNull(claim.getSubmissionPeriodForDisplay());
        }

        @Test
        void getSubmissionPeriodForDisplayFormatsDate() {
            Claim claim = new Claim();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            Assertions.assertEquals("Jan 2020", claim.getSubmissionPeriodForDisplay());
        }
    }

    @Nested
    class GetSubmissionPeriodForSortingTests {
        @Test
        void getSubmissionPeriodForSortingHandlesNull() {
            Claim claim = new Claim();
            Assertions.assertEquals(0, claim.getSubmissionPeriodForSorting());
        }

        @Test
        void getSubmissionPeriodForSortingGetsEpochValueOfDate() {
            Claim claim = new Claim();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            Assertions.assertEquals(18262, claim.getSubmissionPeriodForSorting());
        }
    }

    @Nested
    class GetEscapedForSortingTests {
        @Test
        void getEscapedForSortingHandlesNull() {
            Claim claim = new Claim();
            Assertions.assertEquals("index.results.escaped.no", claim.getEscapedForDisplay());
        }

        @Test
        void getEscapedForDisplayHandlesTrue() {
            Claim claim = new Claim();
            claim.setEscaped(true);
            Assertions.assertEquals("index.results.escaped.yes", claim.getEscapedForDisplay());
        }

        @Test
        void getEscapedForDisplayHandlesFalse() {
            Claim claim = new Claim();
            claim.setEscaped(false);
            Assertions.assertEquals("index.results.escaped.no", claim.getEscapedForDisplay());
        }
    }

    @Nested
    class getClientNameTests {
        @Test
        void getClientNameHandlesNullForenameAndSurname() {
            Claim claim = new Claim();
            Assertions.assertNull(claim.getClientName());
        }

        @Test
        void getClientNameHandlesNullSurname() {
            Claim claim = new Claim();
            claim.setClientForename("John");
            Assertions.assertEquals("John", claim.getClientName());
        }

        @Test
        void getClientNameHandlesNullForename() {
            Claim claim = new Claim();
            claim.setClientSurname("Doe");
            Assertions.assertEquals("Doe", claim.getClientName());
        }

        @Test
        void getClientNameHandlesFullName() {
            Claim claim = new Claim();
            claim.setClientForename("John");
            claim.setClientSurname("Doe");
            Assertions.assertEquals("John Doe", claim.getClientName());
        }
    }
}
