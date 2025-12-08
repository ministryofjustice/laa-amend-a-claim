package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CivilClaimDetailsViewTest {

    @Nested
    class GetAccountNumberTests {
        @Test
        void getAccountNumberPicksOutFirstPartOfScheduleReference() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setScheduleReference("0U733A/2018/02");
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("0U733A", viewModel.getAccountNumber());
        }

        @Test
        void getAccountNumberReturnsScheduleReferenceIfUnexpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setScheduleReference("0U733A201802");
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("0U733A201802", viewModel.getAccountNumber());
        }
    }

    @Nested
    class GetSubmissionPeriodForDisplayTests {
        @Test
        void getSubmissionPeriodForDisplayHandlesNull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertNull(viewModel.getSubmissionPeriodForDisplay());
        }

        @Test
        void getSubmissionPeriodForDisplayFormatsDate() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("Jan 2020", viewModel.getSubmissionPeriodForDisplay());
        }
    }


    @Nested
    class GetAllowedTotalsTests {
        @Test
        void getAllowedTotalsHandlesNull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);

            List<ClaimField> result = viewModel.getAllowedTotals();

            Assertions.assertEquals(List.of(), result);
        }

        @Test
        void getAllowedTotalsHandlesValid() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setAllowedTotalVat(createClaimField("allowedTotalVat", AmendStatus.NEEDS_AMENDING));
            claim.setAllowedTotalInclVat(createClaimField("allowedTotalInclVat", AmendStatus.NEEDS_AMENDING));
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);

            List<ClaimField> result = viewModel.getAllowedTotals();

            Assertions.assertEquals(claim.getAllowedTotalVat(), result.get(0));
            Assertions.assertEquals(claim.getAllowedTotalInclVat(), result.get(1));

        }
    }

    @Nested
    class GetSubmissionPeriodForSortingTests {
        @Test
        void getSubmissionPeriodForSortingHandlesNull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals(0, viewModel.getSubmissionPeriodForSorting());
        }

        @Test
        void getSubmissionPeriodForSortingGetsEpochValueOfDate() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setSubmissionPeriod(YearMonth.of(2020, 1));
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals(18262, viewModel.getSubmissionPeriodForSorting());
        }
    }

    @Nested
    class getClientNameTests {
        @Test
        void getClientNameHandlesNullForenameAndSurname() {
            CivilClaimDetails claim = new CivilClaimDetails();
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertNull(viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesNullSurname() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setClientForename("John");
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("John", viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesNullForename() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setClientSurname("Doe");
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("Doe", viewModel.getClientName());
        }

        @Test
        void getClientNameHandlesFullName() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setClientForename("John");
            claim.setClientSurname("Doe");
            ClaimDetailsView<CivilClaimDetails> viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("John Doe", viewModel.getClientName());
        }
    }

    @Nested
    class GetMatterTypeCodeOneTests {
        @Test
        void getMatterTypeCodeOneWhenInExpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("IMLB+AHQS");
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("IMLB", viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenInUnexpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("IMLB");
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("IMLB", viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenNull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode(null);
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenEmpty() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("");
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenBlank() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode(" ");
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }
    }

    @Nested
    class GetMatterTypeCodeTwoTests {
        @Test
        void getMatterTypeCodeTwoWhenInExpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("IMLB+AHQS");
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertEquals("AHQS", viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenInUnexpectedFormat() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("IMLB");
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertNull(viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenNull() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode(null);
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenEmpty() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode("");
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenBlank() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setMatterTypeCode(" ");
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
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
            ClaimField adjournedHearing = new ClaimField("8", 100, 100);
            ClaimField cmrhTelephone = new ClaimField("9", 100, 100);
            ClaimField cmrhOral = new ClaimField("10", 100, 100);
            ClaimField hoInterview = new ClaimField("11", 100, 100);
            ClaimField substantiveHearing = new ClaimField("12", 100, 100);
            ClaimField vatClaimed = new ClaimField("13", 100, 100);
            ClaimField totalAmount = new ClaimField("14", 100, 100);

            CivilClaimDetails claim = new CivilClaimDetails();
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

            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            List<ClaimField> expectedRows = List.of(
                    fixedFee,
                    netProfitCost,
                    netDisbursementAmount,
                    disbursementVatAmount,
                    detention,
                    jrFormFilling,
                    counselCost,
                    cmrhOral,
                    cmrhTelephone,
                    hoInterview,
                    substantiveHearing,
                    adjournedHearing,
                    vatClaimed,
                    totalAmount
            );
            Assertions.assertEquals(expectedRows, viewModel.getTableRows());
        }

        @Test
        void onlyRowsWithValuesRendered() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);
            ClaimField adjournedHearing = new ClaimField("8", 100, 100);
            ClaimField cmrhTelephone = new ClaimField("9", null, null);
            ClaimField cmrhOral = new ClaimField("10", null, null);
            ClaimField hoInterview = new ClaimField("11", null, null);
            ClaimField substantiveHearing = new ClaimField("12", null, null);
            ClaimField vatClaimed = new ClaimField("13", null, 100);
            ClaimField totalAmount = new ClaimField("14", 100, null);

            CivilClaimDetails claim = new CivilClaimDetails();
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

            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            List<ClaimField> expectedRows = List.of(
                    fixedFee,
                    netProfitCost,
                    netDisbursementAmount,
                    disbursementVatAmount,
                    detention,
                    jrFormFilling,
                    counselCost,
                    adjournedHearing,
                    vatClaimed,
                    totalAmount
            );
            Assertions.assertEquals(expectedRows, viewModel.getTableRows());
        }

        @Test
        void rowsRenderedForNullBoltOnClaimValues() {
            ClaimField fixedFee = new ClaimField("1", null, null);
            ClaimField netProfitCost = new ClaimField("2", null, null);
            ClaimField netDisbursementAmount = new ClaimField("3", null, null);
            ClaimField disbursementVatAmount = new ClaimField("4", null, null);
            ClaimField counselCost = new ClaimField("5", null, null);
            ClaimField detention = new ClaimField("6", null, null);
            ClaimField jrFormFilling = new ClaimField("7", null, null);

            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setFixedFee(fixedFee);
            claim.setNetProfitCost(netProfitCost);
            claim.setNetDisbursementAmount(netDisbursementAmount);
            claim.setDisbursementVatAmount(disbursementVatAmount);
            claim.setCounselsCost(counselCost);
            claim.setDetentionTravelWaitingCosts(detention);
            claim.setJrFormFillingCost(jrFormFilling);

            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            List<ClaimField> expectedRows = List.of(
                    fixedFee,
                    netProfitCost,
                    netDisbursementAmount,
                    disbursementVatAmount,
                    detention,
                    jrFormFilling,
                    counselCost
            );
            Assertions.assertEquals(expectedRows, viewModel.getTableRows());
        }
    }

    @Nested
    class GetErrorTests {

        @Test
        void convertFieldsThatNeedAmendingIntoErrors() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setNetProfitCost(createClaimField("profitCost", AmendStatus.NEEDS_AMENDING));
            claim.setCounselsCost(createClaimField("counselsCost", AmendStatus.NEEDS_AMENDING));
            claim.setJrFormFillingCost(createClaimField("jrFormFilling", AmendStatus.AMENDABLE));
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);

            List<ReviewAndAmendFormError> expectedErrors = List.of(
                new ReviewAndAmendFormError("profit-cost", "claimSummary.rows.profitCost.error"),
                new ReviewAndAmendFormError("counsels-cost", "claimSummary.rows.counselsCost.error")
            );

            Assertions.assertEquals(expectedErrors, viewModel.getErrors());
        }
    }

    public static ClaimField createClaimField(String key, AmendStatus status) {
        ClaimField claimField = new ClaimField();
        claimField.setKey(key);
        claimField.setStatus(status);
        return claimField;
    }
}
