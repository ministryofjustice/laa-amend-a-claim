package uk.gov.justice.laa.amend.claim.viewmodels;

import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.AllowedClaimField;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.BoltOnClaimField;
import uk.gov.justice.laa.amend.claim.models.CalculatedTotalClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.CostClaimField;
import uk.gov.justice.laa.amend.claim.models.FixedFeeClaimField;
import uk.gov.justice.laa.amend.claim.models.VatLiabilityClaimField;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

public class ClaimFieldRowTest {

  @Test
  void whenProfitCostClaimField() {
    CostClaimField field = MockClaimsFunctions.createNetProfitCostField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.PROFIT_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenDisbursementsClaimField() {
    CostClaimField field = MockClaimsFunctions.createDisbursementCostField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.DISBURSEMENTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenDisbursementsVatClaimField() {
    CostClaimField field = MockClaimsFunctions.createDisbursementVatCostField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.DISBURSEMENTS_VAT.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenTravelCostClaimFieldWhenValuesAreNull() {
    CostClaimField field = MockClaimsFunctions.createTravelCostField();
    field.setSubmitted(null);
    field.setCalculated(null);
    field.setAssessed(null);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(BigDecimal.ZERO, result.submitted());
    Assertions.assertEquals(BigDecimal.ZERO, result.calculated());
    Assertions.assertEquals(BigDecimal.ZERO, result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.TRAVEL_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenTravelCostClaimFieldWhenValuesAreNotNull() {
    CostClaimField field = MockClaimsFunctions.createTravelCostField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.TRAVEL_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenWaitingCostClaimFieldWhenValuesAreNull() {
    CostClaimField field = MockClaimsFunctions.createWaitingCostField();
    field.setSubmitted(null);
    field.setCalculated(null);
    field.setAssessed(null);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(BigDecimal.ZERO, result.submitted());
    Assertions.assertEquals(BigDecimal.ZERO, result.calculated());
    Assertions.assertEquals(BigDecimal.ZERO, result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.WAITING_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenWaitingCostClaimFieldWhenValuesAreNotNull() {
    CostClaimField field = MockClaimsFunctions.createWaitingCostField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.WAITING_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenDetentionCostClaimFieldWhenValuesAreNull() {
    CostClaimField field = MockClaimsFunctions.createDetentionCostField();
    field.setSubmitted(null);
    field.setCalculated(null);
    field.setAssessed(null);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(BigDecimal.ZERO, result.submitted());
    Assertions.assertEquals(BigDecimal.ZERO, result.calculated());
    Assertions.assertEquals(BigDecimal.ZERO, result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(
        Cost.DETENTION_TRAVEL_AND_WAITING_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenDetentionCostClaimFieldWhenValuesAreNotNull() {
    CostClaimField field = MockClaimsFunctions.createDetentionCostField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(
        Cost.DETENTION_TRAVEL_AND_WAITING_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenJrFormFillingCostClaimFieldWhenValuesAreNull() {
    CostClaimField field = MockClaimsFunctions.createJrFormFillingCostField();
    field.setSubmitted(null);
    field.setCalculated(null);
    field.setAssessed(null);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(BigDecimal.ZERO, result.submitted());
    Assertions.assertEquals(BigDecimal.ZERO, result.calculated());
    Assertions.assertEquals(BigDecimal.ZERO, result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.JR_FORM_FILLING_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenJrFormFillingCostClaimFieldWhenValuesAreNotNull() {
    CostClaimField field = MockClaimsFunctions.createJrFormFillingCostField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.JR_FORM_FILLING_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenCounselCostClaimFieldWhenValuesAreNull() {
    CostClaimField field = MockClaimsFunctions.createCounselCostField();
    field.setSubmitted(null);
    field.setCalculated(null);
    field.setAssessed(null);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(BigDecimal.ZERO, result.submitted());
    Assertions.assertEquals(BigDecimal.ZERO, result.calculated());
    Assertions.assertEquals(BigDecimal.ZERO, result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.COUNSEL_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenCounselCostClaimFieldWhenValuesAreNotNull() {
    CostClaimField field = MockClaimsFunctions.createCounselCostField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals(Cost.COUNSEL_COSTS.getChangeUrl(), result.changeUrl());
  }

  @Test
  void whenAllowedClaimFieldWhenValuesAreNull() {
    AllowedClaimField field = MockClaimsFunctions.createAllowedTotalVatField();
    field.setSubmitted(null);
    field.setCalculated(null);
    field.setAssessed(null);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertNull(result.submitted());
    Assertions.assertEquals(BigDecimal.ZERO, result.calculated());
    Assertions.assertNull(result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals("/submissions/%s/claims/%s/allowed-totals", result.changeUrl());
  }

  @Test
  void whenAllowedClaimFieldWhenValuesAreNotNull() {
    AllowedClaimField field = MockClaimsFunctions.createAllowedTotalVatField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals("/submissions/%s/claims/%s/allowed-totals", result.changeUrl());
  }

  @Test
  void whenAssessedClaimField() {
    AssessedClaimField field = MockClaimsFunctions.createAssessedTotalVatField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertTrue(result.assessable());
    Assertions.assertEquals("/submissions/%s/claims/%s/assessed-totals", result.changeUrl());
  }

  @Test
  void whenVatLiabilityClaimField() {
    VatLiabilityClaimField field = MockClaimsFunctions.createVatClaimedField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertFalse(result.assessable());
    Assertions.assertEquals("/submissions/%s/claims/%s/assessment-outcome", result.changeUrl());
  }

  @Test
  void whenBoltOnClaimField() {
    BoltOnClaimField field = MockClaimsFunctions.createAdjournedHearingField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertFalse(result.assessable());
    Assertions.assertNull(result.changeUrl());
  }

  @Test
  void whenBoltOnClaimFieldWithNullSubmittedValue() {
    BoltOnClaimField field = MockClaimsFunctions.createAdjournedHearingField();
    field.setSubmitted(null);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertNull(result);
  }

  @Test
  void whenBoltOnClaimFieldWithZeroSubmittedValue() {
    BoltOnClaimField field = MockClaimsFunctions.createAdjournedHearingField();
    field.setSubmitted(BigDecimal.ZERO);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertNull(result);
  }

  @Test
  void whenBoltOnClaimFieldWithNonZeroSubmittedValue() {
    BoltOnClaimField field = MockClaimsFunctions.createAdjournedHearingField();
    field.setSubmitted(BigDecimal.ONE);
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertNotNull(result);
  }

  @Test
  void whenTotalClaimField() {
    CalculatedTotalClaimField field = MockClaimsFunctions.createTotalAmountField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertFalse(result.assessable());
    Assertions.assertNull(result.changeUrl());
  }

  @Test
  void whenFixedFeeClaimField() {
    FixedFeeClaimField field = MockClaimsFunctions.createFixedFeeField();
    ClaimFieldRow result = ClaimFieldRow.from(field);
    Assertions.assertEquals(field.getKey(), result.key());
    Assertions.assertEquals(field.getSubmitted(), result.submitted());
    Assertions.assertEquals(field.getCalculated(), result.calculated());
    Assertions.assertEquals(field.getAssessed(), result.assessed());
    Assertions.assertFalse(result.assessable());
    Assertions.assertNull(result.changeUrl());
  }
}
