package uk.gov.justice.laa.amend.claim.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

@ExtendWith(MockitoExtension.class)
class ClaimStatusHandlerTest {

    private ClaimStatusHandler claimStatusHandler;

    @BeforeEach
    void setUp() {
        claimStatusHandler = new ClaimStatusHandler();
    }

    @Nested
    class NilledStatusTests {
        @Test
        void shouldSetAssessedTotalFieldsToNotModifiable() {
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            ClaimDetails claimDetails = new CrimeClaimDetails();
            claimDetails.setAssessedTotalVat(assessedTotalVatField);
            claimDetails.setAssessedTotalInclVat(assessedTotalInclVatField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);

            assertThat(assessedTotalVatField.isAssessable()).isFalse();
            assertThat(assessedTotalInclVatField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetAssessableCrimeFieldsToNotModifiable() {
            ClaimField travelCostsField = MockClaimsFunctions.createTravelCostField();
            ClaimField waitingCostsField = MockClaimsFunctions.createWaitingCostField();
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setTravelCosts(travelCostsField);
            crimeClaim.setWaitingCosts(waitingCostsField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.NILLED);

            assertThat(travelCostsField.isAssessable()).isFalse();
            assertThat(waitingCostsField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetAssessableCivilFieldsToNotModifiable() {
            ClaimField detentionTravelWaitingCostsField = MockClaimsFunctions.createDetentionCostField();
            ClaimField jrFormFillingCostField = MockClaimsFunctions.createJrFormFillingCostField();
            ClaimField counselsCostField = MockClaimsFunctions.createCounselCostField();
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            crimeClaim.setDetentionTravelWaitingCosts(detentionTravelWaitingCostsField);
            crimeClaim.setJrFormFillingCost(jrFormFillingCostField);
            crimeClaim.setCounselsCost(counselsCostField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.NILLED);

            assertThat(detentionTravelWaitingCostsField.isAssessable()).isFalse();
            assertThat(jrFormFillingCostField.isAssessable()).isFalse();
            assertThat(counselsCostField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetBoltOnFieldsToNotModifiable() {
            ClaimField adjournedHearingField = MockClaimsFunctions.createAdjournedHearingField();
            ClaimField cmrhTelephoneField = MockClaimsFunctions.createCmrhTelephoneField();
            ClaimField cmrhOralField = MockClaimsFunctions.createCmrhOralField();
            ClaimField hoInterviewField = MockClaimsFunctions.createHoInterviewField();
            ClaimField substantiveHearingField = MockClaimsFunctions.createSubstantiveHearingField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(adjournedHearingField);
            claimDetails.setCmrhTelephone(cmrhTelephoneField);
            claimDetails.setCmrhOral(cmrhOralField);
            claimDetails.setHoInterview(hoInterviewField);
            claimDetails.setSubstantiveHearing(substantiveHearingField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);

            assertThat(adjournedHearingField.isAssessable()).isFalse();
            assertThat(cmrhTelephoneField.isAssessable()).isFalse();
            assertThat(cmrhOralField.isAssessable()).isFalse();
            assertThat(hoInterviewField.isAssessable()).isFalse();
            assertThat(substantiveHearingField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetFixedFeeFieldToNotModifiable() {
            ClaimField fixedFeeField = MockClaimsFunctions.createFixedFeeField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setFixedFee(fixedFeeField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);

            assertThat(fixedFeeField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetTotalAmountFieldToNotModifiable() {
            ClaimField totalAmountField = MockClaimsFunctions.createTotalAmountField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setTotalAmount(totalAmountField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);

            assertThat(totalAmountField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetVatClaimedFieldToModifiable() {
            ClaimField vatClaimedField = MockClaimsFunctions.createVatClaimedField();
            ClaimDetails civilClaimDetails = new CivilClaimDetails();
            civilClaimDetails.setVatClaimed(vatClaimedField);

            claimStatusHandler.updateFieldStatuses(civilClaimDetails, OutcomeType.NILLED);

            assertThat(vatClaimedField.isAssessable()).isTrue();
        }
    }

    @Nested
    class ReducedStatusTests {
        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCrimeClaimWithNoFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode(null);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.isAssessable()).isFalse();
            assertThat(assessedTotalInclVatField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCrimeClaimWithInvalidFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("ABCD");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.isAssessable()).isFalse();
            assertThat(assessedTotalInclVatField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetAssessedTotalFieldsToModifiableForCrimeClaimWithValidFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("INVC");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.isAssessable()).isTrue();
            assertThat(assessedTotalInclVatField.isAssessable()).isTrue();
        }

        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCivilClaim() {
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.isAssessable()).isFalse();
            assertThat(assessedTotalInclVatField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetNetProfitCostFieldToModifiable() {
            ClaimField netProfitCostField = MockClaimsFunctions.createNetProfitCostField();
            ClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setNetProfitCost(netProfitCostField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(netProfitCostField.isAssessable()).isTrue();
        }

        @Test
        void shouldSetBoltOnFieldsToNotModifiable() {
            ClaimField adjournedHearingField = MockClaimsFunctions.createAdjournedHearingField();
            ClaimField cmrhTelephoneField = MockClaimsFunctions.createCmrhTelephoneField();
            ClaimField cmrhOralField = MockClaimsFunctions.createCmrhOralField();
            ClaimField hoInterviewField = MockClaimsFunctions.createHoInterviewField();
            ClaimField substantiveHearingField = MockClaimsFunctions.createSubstantiveHearingField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(adjournedHearingField);
            claimDetails.setCmrhTelephone(cmrhTelephoneField);
            claimDetails.setCmrhOral(cmrhOralField);
            claimDetails.setHoInterview(hoInterviewField);
            claimDetails.setSubstantiveHearing(substantiveHearingField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(adjournedHearingField.isAssessable()).isFalse();
            assertThat(cmrhTelephoneField.isAssessable()).isFalse();
            assertThat(cmrhOralField.isAssessable()).isFalse();
            assertThat(hoInterviewField.isAssessable()).isFalse();
            assertThat(substantiveHearingField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetFixedFeeFieldToNotModifiable() {
            ClaimField fixedFeeField = MockClaimsFunctions.createFixedFeeField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setFixedFee(fixedFeeField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(fixedFeeField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetTotalAmountFieldToNotModifiable() {
            ClaimField totalAmountField = MockClaimsFunctions.createTotalAmountField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setTotalAmount(totalAmountField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(totalAmountField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetAssessableCrimeFieldsToModifiable() {
            ClaimField travelCostsField = MockClaimsFunctions.createTravelCostField();
            ClaimField waitingCostsField = MockClaimsFunctions.createWaitingCostField();
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setTravelCosts(travelCostsField);
            crimeClaim.setWaitingCosts(waitingCostsField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(travelCostsField.isAssessable()).isTrue();
            assertThat(waitingCostsField.isAssessable()).isTrue();
        }

        @Test
        void shouldSetAssessableCivilFieldsToModifiable() {
            ClaimField detentionTravelWaitingCostsField = MockClaimsFunctions.createDetentionCostField();
            ClaimField jrFormFillingCostField = MockClaimsFunctions.createJrFormFillingCostField();
            ClaimField counselsCostField = MockClaimsFunctions.createCounselCostField();
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            crimeClaim.setDetentionTravelWaitingCosts(detentionTravelWaitingCostsField);
            crimeClaim.setJrFormFillingCost(jrFormFillingCostField);
            crimeClaim.setCounselsCost(counselsCostField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(detentionTravelWaitingCostsField.isAssessable()).isTrue();
            assertThat(jrFormFillingCostField.isAssessable()).isTrue();
            assertThat(counselsCostField.isAssessable()).isTrue();
        }

        @Test
        void shouldResetWhetherFieldIsAssessableOrNotAfterOutcomeChange() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("INVC");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.NILLED);

            assertThat(assessedTotalVatField.isAssessable()).isFalse();
            assertThat(assessedTotalInclVatField.isAssessable()).isFalse();

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.isAssessable()).isTrue();
            assertThat(assessedTotalInclVatField.isAssessable()).isTrue();
        }
    }

    @Nested
    class PaidInFullStatusTests {
        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCrimeClaimWithNoFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode(null);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);
            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(assessedTotalVatField.isAssessable()).isFalse();
            assertThat(assessedTotalInclVatField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCrimeClaimWithInvalidFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("ABCD");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(assessedTotalVatField.isAssessable()).isFalse();
            assertThat(assessedTotalInclVatField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetAssessedTotalFieldsToModifiableForCrimeClaimWithValidFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("INVC");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(assessedTotalVatField.isAssessable()).isTrue();
            assertThat(assessedTotalInclVatField.isAssessable()).isTrue();
        }

        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCivilClaim() {
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            ClaimField assessedTotalVatField = MockClaimsFunctions.createAssessedTotalVatField();
            ClaimField assessedTotalInclVatField = MockClaimsFunctions.createAssessedTotalInclVatField();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(assessedTotalVatField.isAssessable()).isFalse();
            assertThat(assessedTotalInclVatField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetFixedFeeFieldToNotModifiable() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField field = MockClaimsFunctions.createFixedFeeField();
            crimeClaim.setFixedFee(field);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(field.isAssessable()).isFalse();
        }

        @Test
        void shouldSetTotalAmountFieldToNotModifiable() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField field = MockClaimsFunctions.createTotalAmountField();
            crimeClaim.setTotalAmount(field);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(field.isAssessable()).isFalse();
        }

        @Test
        void shouldSetBoltOnFieldsToNotModifiable() {
            ClaimField adjournedHearingField = MockClaimsFunctions.createAdjournedHearingField();
            ClaimField cmrhTelephoneField = MockClaimsFunctions.createCmrhTelephoneField();
            ClaimField cmrhOralField = MockClaimsFunctions.createCmrhOralField();
            ClaimField hoInterviewField = MockClaimsFunctions.createHoInterviewField();
            ClaimField substantiveHearingField = MockClaimsFunctions.createSubstantiveHearingField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(adjournedHearingField);
            claimDetails.setCmrhTelephone(cmrhTelephoneField);
            claimDetails.setCmrhOral(cmrhOralField);
            claimDetails.setHoInterview(hoInterviewField);
            claimDetails.setSubstantiveHearing(substantiveHearingField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.PAID_IN_FULL);

            assertThat(adjournedHearingField.isAssessable()).isFalse();
            assertThat(cmrhTelephoneField.isAssessable()).isFalse();
            assertThat(cmrhOralField.isAssessable()).isFalse();
            assertThat(hoInterviewField.isAssessable()).isFalse();
            assertThat(substantiveHearingField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetAssessableCrimeFieldsToModifiable() {
            ClaimField travelCostsField = MockClaimsFunctions.createTravelCostField();
            ClaimField waitingCostsField = MockClaimsFunctions.createWaitingCostField();
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setTravelCosts(travelCostsField);
            crimeClaim.setWaitingCosts(waitingCostsField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(travelCostsField.isAssessable()).isTrue();
            assertThat(waitingCostsField.isAssessable()).isTrue();
        }

        @Test
        void shouldSetAssessableCivilFieldsToModifiable() {
            ClaimField detentionTravelWaitingCostsField = MockClaimsFunctions.createDetentionCostField();
            ClaimField jrFormFillingCostField = MockClaimsFunctions.createJrFormFillingCostField();
            ClaimField counselsCostField = MockClaimsFunctions.createCounselCostField();
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            crimeClaim.setDetentionTravelWaitingCosts(detentionTravelWaitingCostsField);
            crimeClaim.setJrFormFillingCost(jrFormFillingCostField);
            crimeClaim.setCounselsCost(counselsCostField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(detentionTravelWaitingCostsField.isAssessable()).isTrue();
            assertThat(jrFormFillingCostField.isAssessable()).isTrue();
            assertThat(counselsCostField.isAssessable()).isTrue();
        }
    }

    @Nested
    class ReducedToFixedFeeStatusTests {
        @Test
        void shouldSetAssessableCrimeFieldsToModifiable() {
            ClaimField travelCostsField = MockClaimsFunctions.createTravelCostField();
            ClaimField waitingCostsField = MockClaimsFunctions.createWaitingCostField();
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setTravelCosts(travelCostsField);
            crimeClaim.setWaitingCosts(waitingCostsField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(travelCostsField.isAssessable()).isTrue();
            assertThat(waitingCostsField.isAssessable()).isTrue();
        }

        @Test
        void shouldSetAssessableCivilFieldsToModifiable() {
            ClaimField detentionTravelWaitingCostsField = MockClaimsFunctions.createDetentionCostField();
            ClaimField jrFormFillingCostField = MockClaimsFunctions.createJrFormFillingCostField();
            ClaimField counselsCostField = MockClaimsFunctions.createCounselCostField();
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            crimeClaim.setDetentionTravelWaitingCosts(detentionTravelWaitingCostsField);
            crimeClaim.setJrFormFillingCost(jrFormFillingCostField);
            crimeClaim.setCounselsCost(counselsCostField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(detentionTravelWaitingCostsField.isAssessable()).isTrue();
            assertThat(jrFormFillingCostField.isAssessable()).isTrue();
            assertThat(counselsCostField.isAssessable()).isTrue();
        }

        @Test
        void shouldSetBoltOnFieldsToNotModifiable() {
            ClaimField adjournedHearingField = MockClaimsFunctions.createAdjournedHearingField();
            ClaimField cmrhTelephoneField = MockClaimsFunctions.createCmrhTelephoneField();
            ClaimField cmrhOralField = MockClaimsFunctions.createCmrhOralField();
            ClaimField hoInterviewField = MockClaimsFunctions.createHoInterviewField();
            ClaimField substantiveHearingField = MockClaimsFunctions.createSubstantiveHearingField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(adjournedHearingField);
            claimDetails.setCmrhTelephone(cmrhTelephoneField);
            claimDetails.setCmrhOral(cmrhOralField);
            claimDetails.setHoInterview(hoInterviewField);
            claimDetails.setSubstantiveHearing(substantiveHearingField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(adjournedHearingField.isAssessable()).isFalse();
            assertThat(cmrhTelephoneField.isAssessable()).isFalse();
            assertThat(cmrhOralField.isAssessable()).isFalse();
            assertThat(hoInterviewField.isAssessable()).isFalse();
            assertThat(substantiveHearingField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetFixedFeeFieldToNotModifiable() {
            ClaimField fixedFeeField = MockClaimsFunctions.createFixedFeeField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setFixedFee(fixedFeeField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(fixedFeeField.isAssessable()).isFalse();
        }

        @Test
        void shouldSetTotalAmountFieldToNotModifiable() {
            ClaimField totalAmountField = MockClaimsFunctions.createTotalAmountField();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setTotalAmount(totalAmountField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(totalAmountField.isAssessable()).isFalse();
        }
    }
}
