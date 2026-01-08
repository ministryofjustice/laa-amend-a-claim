package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.gov.justice.laa.amend.claim.forms.errors.ReviewAndAmendFormError;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;

import java.util.ArrayList;
import java.util.List;

public class CivilClaimDetailsViewTest extends ClaimDetailsViewTest<CivilClaimDetails, CivilClaimDetailsView> {

    @Override
    protected CivilClaimDetails createClaim() {
        return new CivilClaimDetails();
    }

    @Override
    protected CivilClaimDetailsView createView(CivilClaimDetails claim) {
        return new CivilClaimDetailsView(claim);
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
        @ParameterizedTest
        @EnumSource(PageType.class)
        void rowsRenderedForClaimValues(PageType pageType) {
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
            claim.setHasAssessment(true);

            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);
            ArrayList<ClaimField> expectedRows = new ArrayList<>(List.of(
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
                    vatClaimed
            ));
            if (PageType.CLAIM_DETAILS.equals(pageType) && !claim.isHasAssessment()) {
                expectedRows.add(totalAmount);
            }
            Assertions.assertEquals(expectedRows, viewModel.getTableRows(pageType));
        }

        @ParameterizedTest
        @EnumSource(PageType.class)
        void onlyRowsWithValuesRendered(PageType pageType) {
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
            List<ClaimField> expectedRows = new ArrayList<>(List.of(
                fixedFee,
                netProfitCost,
                netDisbursementAmount,
                disbursementVatAmount,
                detention,
                jrFormFilling,
                counselCost,
                adjournedHearing,
                vatClaimed
            ));

            if (PageType.CLAIM_DETAILS.equals(pageType) && !claim.isHasAssessment()) {
                expectedRows.add(totalAmount);
            }
            Assertions.assertEquals(expectedRows, viewModel.getTableRows(pageType));
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
            Assertions.assertEquals(expectedRows, viewModel.getTableRows(PageType.CLAIM_DETAILS));
        }
    }

    @Nested
    class GetErrorTests {

        @Test
        void convertFieldsThatNeedAmendingIntoErrors() {
            CivilClaimDetails claim = new CivilClaimDetails();
            claim.setNetProfitCost(createClaimField("profitCost", ClaimFieldStatus.MODIFIABLE));
            claim.setCounselsCost(createClaimField("counselsCost", ClaimFieldStatus.MODIFIABLE));
            claim.setJrFormFillingCost(createClaimField("jrFormFilling", ClaimFieldStatus.MODIFIABLE));
            claim.setAssessedTotalVat(createClaimField("assessedTotalVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAssessedTotalInclVat(createClaimField("assessedTotalInclVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAllowedTotalVat(createClaimField("allowedTotalVat", ClaimFieldStatus.MODIFIABLE));
            claim.setAllowedTotalInclVat(createClaimField("allowedTotalInclVat", ClaimFieldStatus.MODIFIABLE));
            CivilClaimDetailsView viewModel = new CivilClaimDetailsView(claim);

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
}
