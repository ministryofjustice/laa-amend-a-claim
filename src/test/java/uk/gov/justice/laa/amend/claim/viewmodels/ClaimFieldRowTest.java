package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.math.BigDecimal;

public class ClaimFieldRowTest {

    @Test
    void whenProfitCostClaimField() {
        ClaimField field = MockClaimsFunctions.createNetProfitCostField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.PROFIT_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenDisbursementsClaimField() {
        ClaimField field = MockClaimsFunctions.createDisbursementCostField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.DISBURSEMENTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenDisbursementsVatClaimField() {
        ClaimField field = MockClaimsFunctions.createDisbursementVatCostField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.DISBURSEMENTS_VAT.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenTravelCostClaimFieldWhenValuesAreNull() {
        ClaimField field = MockClaimsFunctions.createTravelCostField();
        field.setSubmitted(null);
        field.setCalculated(null);
        field.setAssessed(null);
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(BigDecimal.ZERO, result.getSubmitted());
        Assertions.assertEquals(BigDecimal.ZERO, result.getCalculated());
        Assertions.assertEquals(BigDecimal.ZERO, result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.TRAVEL_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenTravelCostClaimFieldWhenValuesAreNotNull() {
        ClaimField field = MockClaimsFunctions.createTravelCostField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.TRAVEL_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenWaitingCostClaimFieldWhenValuesAreNull() {
        ClaimField field = MockClaimsFunctions.createWaitingCostField();
        field.setSubmitted(null);
        field.setCalculated(null);
        field.setAssessed(null);
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(BigDecimal.ZERO, result.getSubmitted());
        Assertions.assertEquals(BigDecimal.ZERO, result.getCalculated());
        Assertions.assertEquals(BigDecimal.ZERO, result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.WAITING_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenWaitingCostClaimFieldWhenValuesAreNotNull() {
        ClaimField field = MockClaimsFunctions.createWaitingCostField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.WAITING_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenDetentionCostClaimFieldWhenValuesAreNull() {
        ClaimField field = MockClaimsFunctions.createDetentionCostField();
        field.setSubmitted(null);
        field.setCalculated(null);
        field.setAssessed(null);
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(BigDecimal.ZERO, result.getSubmitted());
        Assertions.assertEquals(BigDecimal.ZERO, result.getCalculated());
        Assertions.assertEquals(BigDecimal.ZERO, result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.DETENTION_TRAVEL_AND_WAITING_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenDetentionCostClaimFieldWhenValuesAreNotNull() {
        ClaimField field = MockClaimsFunctions.createDetentionCostField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.DETENTION_TRAVEL_AND_WAITING_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenJrFormFillingCostClaimFieldWhenValuesAreNull() {
        ClaimField field = MockClaimsFunctions.createJrFormFillingCostField();
        field.setSubmitted(null);
        field.setCalculated(null);
        field.setAssessed(null);
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(BigDecimal.ZERO, result.getSubmitted());
        Assertions.assertEquals(BigDecimal.ZERO, result.getCalculated());
        Assertions.assertEquals(BigDecimal.ZERO, result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.JR_FORM_FILLING_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenJrFormFillingCostClaimFieldWhenValuesAreNotNull() {
        ClaimField field = MockClaimsFunctions.createJrFormFillingCostField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.JR_FORM_FILLING_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenCounselCostClaimFieldWhenValuesAreNull() {
        ClaimField field = MockClaimsFunctions.createCounselCostField();
        field.setSubmitted(null);
        field.setCalculated(null);
        field.setAssessed(null);
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(BigDecimal.ZERO, result.getSubmitted());
        Assertions.assertEquals(BigDecimal.ZERO, result.getCalculated());
        Assertions.assertEquals(BigDecimal.ZERO, result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.COUNSEL_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenCounselCostClaimFieldWhenValuesAreNotNull() {
        ClaimField field = MockClaimsFunctions.createCounselCostField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals(Cost.COUNSEL_COSTS.getChangeUrl(), result.getChangeUrl());
    }

    @Test
    void whenAllowedClaimFieldWhenValuesAreNull() {
        ClaimField field = MockClaimsFunctions.createAllowedTotalVatField();
        field.setSubmitted(null);
        field.setCalculated(null);
        field.setAssessed(null);
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertNull(result.getSubmitted());
        Assertions.assertEquals(BigDecimal.ZERO, result.getCalculated());
        Assertions.assertNull(result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals("/submissions/%s/claims/%s/allowed-totals", result.getChangeUrl());
    }

    @Test
    void whenAllowedClaimFieldWhenValuesAreNotNull() {
        ClaimField field = MockClaimsFunctions.createAllowedTotalVatField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals("/submissions/%s/claims/%s/allowed-totals", result.getChangeUrl());
    }

    @Test
    void whenAssessedClaimField() {
        ClaimField field = MockClaimsFunctions.createAssessedTotalVatField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals("/submissions/%s/claims/%s/assessed-totals", result.getChangeUrl());
    }

    @Test
    void whenVatLiabilityClaimField() {
        ClaimField field = MockClaimsFunctions.createVatClaimedField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertTrue(result.isAssessable());
        Assertions.assertEquals("/submissions/%s/claims/%s/assessment-outcome", result.getChangeUrl());
    }

    @Test
    void whenBoltOnClaimField() {
        ClaimField field = MockClaimsFunctions.createAdjournedHearingField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertFalse(result.isAssessable());
        Assertions.assertNull(result.getChangeUrl());
    }

    @Test
    void whenTotalClaimField() {
        ClaimField field = MockClaimsFunctions.createTotalAmountField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertFalse(result.isAssessable());
        Assertions.assertNull(result.getChangeUrl());
    }

    @Test
    void whenFixedFeeClaimField() {
        ClaimField field = MockClaimsFunctions.createFixedFeeField();
        ClaimFieldRow result = new ClaimFieldRow(field);
        Assertions.assertEquals(field.getKey(), result.getKey());
        Assertions.assertEquals(field.getSubmitted(), result.getSubmitted());
        Assertions.assertEquals(field.getCalculated(), result.getCalculated());
        Assertions.assertEquals(field.getAssessed(), result.getAssessed());
        Assertions.assertFalse(result.isAssessable());
        Assertions.assertNull(result.getChangeUrl());
    }
}
