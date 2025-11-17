package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CivilClaimViewModelTest {

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
            Assertions.assertEquals("No", viewModel.getEscapedForDisplay());
        }

        @Test
        void getEscapedForDisplayHandlesTrue() {
            CivilClaim claim = new CivilClaim();
            claim.setEscaped(true);
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("Yes", viewModel.getEscapedForDisplay());
        }

        @Test
        void getEscapedForDisplayHandlesFalse() {
            CivilClaim claim = new CivilClaim();
            claim.setEscaped(false);
            ClaimViewModel<CivilClaim> viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("No", viewModel.getEscapedForDisplay());
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

    @Nested
    class GetMatterTypeCodeOneTests {
        @Test
        void getMatterTypeCodeOneWhenInExpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("IMLB+AHQS");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("IMLB", viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenInUnexpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("IMLB");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("IMLB", viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenNull() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode(null);
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenEmpty() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenBlank() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode(" ");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }
    }

    @Nested
    class GetMatterTypeCodeTwoTests {
        @Test
        void getMatterTypeCodeTwoWhenInExpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("IMLB+AHQS");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("AHQS", viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenInUnexpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("IMLB");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenNull() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode(null);
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenEmpty() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenBlank() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode(" ");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
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
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", null, null);
            ClaimField cmrhTelephone = new ClaimField("9", null, null);
            ClaimField cmrhOral = new ClaimField("10", null, null);
            ClaimField hoInterview = new ClaimField("11", null, null);
            ClaimField substantiveHearing = new ClaimField("12", null, null);
            ClaimField vatClaimed = new ClaimField("13", null, null);
            ClaimField totalAmount = new ClaimField("14", null, null);

            CivilClaim claim = new CivilClaim();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);
            claim.setAdjournedHearing(adjournedHearing);
            claim.setCmrhTelephone(cmrhTelephone);
            claim.setCmrhOral(cmrhOral);
            claim.setHoInterview(hoInterview);
            claim.setSubstantiveHearing(substantiveHearing);
            claim.setVatClaimed(vatClaimed);
            claim.setTotalAmount(totalAmount);

            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            List<ClaimField> expectedRows = List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                counselCost,
                detention,
                jrFormFilling,
                adjournedHearing,
                cmrhTelephone,
                cmrhOral,
                hoInterview,
                substantiveHearing,
                vatClaimed,
                totalAmount
            );
            Assertions.assertEquals(expectedRows, viewModel.getTableRows());
        }
    }
}
