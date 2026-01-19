package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldType;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ASSESSED_TOTAL_INCL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ASSESSED_TOTAL_VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.COUNSELS_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.DETENTION_TRAVEL_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TRAVEL_COSTS;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.VAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.WAITING_COSTS;

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
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimDetails claimDetails = new CrimeClaimDetails();
            claimDetails.setAssessedTotalVat(assessedTotalVatField);
            claimDetails.setAssessedTotalInclVat(assessedTotalInclVatField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetAssessableCrimeFieldsToNotModifiable() {
            ClaimField travelCostsField = ClaimField.builder().key(TRAVEL_COSTS).type(ClaimFieldType.OTHER).build();
            ClaimField waitingCostsField = ClaimField.builder().key(WAITING_COSTS).type(ClaimFieldType.OTHER).build();
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setTravelCosts(travelCostsField);
            crimeClaim.setWaitingCosts(waitingCostsField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.NILLED);

            assertThat(travelCostsField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(waitingCostsField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetAssessableCivilFieldsToNotModifiable() {
            ClaimField detentionTravelWaitingCostsField = ClaimField.builder().key(DETENTION_TRAVEL_COST).type(ClaimFieldType.OTHER).build();
            ClaimField jrFormFillingCostField = ClaimField.builder().key(JR_FORM_FILLING).type(ClaimFieldType.OTHER).build();
            ClaimField counselsCostField = ClaimField.builder().key(COUNSELS_COST).type(ClaimFieldType.OTHER).build();
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            crimeClaim.setDetentionTravelWaitingCosts(detentionTravelWaitingCostsField);
            crimeClaim.setJrFormFillingCost(jrFormFillingCostField);
            crimeClaim.setCounselsCost(counselsCostField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.NILLED);

            assertThat(detentionTravelWaitingCostsField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(jrFormFillingCostField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(counselsCostField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetBoltOnFieldsToNotModifiable() {
            ClaimField adjournedHearingField = ClaimField.builder().key(ADJOURNED_FEE).type(ClaimFieldType.BOLT_ON).build();
            ClaimField cmrhTelephoneField = ClaimField.builder().key(CMRH_TELEPHONE).type(ClaimFieldType.BOLT_ON).build();
            ClaimField cmrhOralField = ClaimField.builder().key(CMRH_ORAL).type(ClaimFieldType.BOLT_ON).build();
            ClaimField hoInterviewField = ClaimField.builder().key(HO_INTERVIEW).type(ClaimFieldType.BOLT_ON).build();
            ClaimField substantiveHearingField = ClaimField.builder().key(SUBSTANTIVE_HEARING).type(ClaimFieldType.BOLT_ON).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(adjournedHearingField);
            claimDetails.setCmrhTelephone(cmrhTelephoneField);
            claimDetails.setCmrhOral(cmrhOralField);
            claimDetails.setHoInterview(hoInterviewField);
            claimDetails.setSubstantiveHearing(substantiveHearingField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);

            assertThat(adjournedHearingField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(cmrhTelephoneField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(cmrhOralField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(hoInterviewField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(substantiveHearingField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetFixedFeeFieldToNotModifiable() {
            ClaimField fixedFeeField = ClaimField.builder().key(FIXED_FEE).type(ClaimFieldType.FIXED_FEE).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setFixedFee(fixedFeeField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);

            assertThat(fixedFeeField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetTotalAmountFieldToNotModifiable() {
            ClaimField totalAmountField = ClaimField.builder().key(TOTAL).type(ClaimFieldType.CALCULATED_TOTAL).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setTotalAmount(totalAmountField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);

            assertThat(totalAmountField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetVatClaimedFieldToModifiable() {
            ClaimField vatClaimedField = ClaimField.builder().key(VAT).type(ClaimFieldType.OTHER).build();
            ClaimDetails civilClaimDetails = new CivilClaimDetails();
            civilClaimDetails.setVatClaimed(vatClaimedField);

            claimStatusHandler.updateFieldStatuses(civilClaimDetails, OutcomeType.NILLED);

            assertThat(vatClaimedField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }
    }

    @Nested
    class ReducedStatusTests {
        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCrimeClaimWithNoFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode(null);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCrimeClaimWithInvalidFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("ABCD");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetAssessedTotalFieldsToModifiableForCrimeClaimWithValidFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("INVC");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }

        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCivilClaim() {
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetNetProfitCostFieldToModifiable() {
            ClaimField netProfitCostField = ClaimField.builder().key(NET_PROFIT_COST).type(ClaimFieldType.OTHER).build();
            ClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setNetProfitCost(netProfitCostField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(netProfitCostField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }

        @Test
        void shouldSetBoltOnFieldsToNotModifiable() {
            ClaimField adjournedHearingField = ClaimField.builder().key(ADJOURNED_FEE).type(ClaimFieldType.BOLT_ON).build();
            ClaimField cmrhTelephoneField = ClaimField.builder().key(CMRH_TELEPHONE).type(ClaimFieldType.BOLT_ON).build();
            ClaimField cmrhOralField = ClaimField.builder().key(CMRH_ORAL).type(ClaimFieldType.BOLT_ON).build();
            ClaimField hoInterviewField = ClaimField.builder().key(HO_INTERVIEW).type(ClaimFieldType.BOLT_ON).build();
            ClaimField substantiveHearingField = ClaimField.builder().key(SUBSTANTIVE_HEARING).type(ClaimFieldType.BOLT_ON).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(adjournedHearingField);
            claimDetails.setCmrhTelephone(cmrhTelephoneField);
            claimDetails.setCmrhOral(cmrhOralField);
            claimDetails.setHoInterview(hoInterviewField);
            claimDetails.setSubstantiveHearing(substantiveHearingField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(adjournedHearingField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(cmrhTelephoneField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(cmrhOralField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(hoInterviewField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(substantiveHearingField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetFixedFeeFieldToNotModifiable() {
            ClaimField fixedFeeField = ClaimField.builder().key(FIXED_FEE).type(ClaimFieldType.FIXED_FEE).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setFixedFee(fixedFeeField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(fixedFeeField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetTotalAmountFieldToNotModifiable() {
            ClaimField totalAmountField = ClaimField.builder().key(TOTAL).type(ClaimFieldType.CALCULATED_TOTAL).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setTotalAmount(totalAmountField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(totalAmountField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetAssessableCrimeFieldsToModifiable() {
            ClaimField travelCostsField = ClaimField.builder().key(TRAVEL_COSTS).type(ClaimFieldType.OTHER).build();
            ClaimField waitingCostsField = ClaimField.builder().key(WAITING_COSTS).type(ClaimFieldType.OTHER).build();
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setTravelCosts(travelCostsField);
            crimeClaim.setWaitingCosts(waitingCostsField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(travelCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(waitingCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }

        @Test
        void shouldSetAssessableCivilFieldsToModifiable() {
            ClaimField detentionTravelWaitingCostsField = ClaimField.builder().key(DETENTION_TRAVEL_COST).type(ClaimFieldType.OTHER).build();
            ClaimField jrFormFillingCostField = ClaimField.builder().key(JR_FORM_FILLING).type(ClaimFieldType.OTHER).build();
            ClaimField counselsCostField = ClaimField.builder().key(COUNSELS_COST).type(ClaimFieldType.OTHER).build();
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            crimeClaim.setDetentionTravelWaitingCosts(detentionTravelWaitingCostsField);
            crimeClaim.setJrFormFillingCost(jrFormFillingCostField);
            crimeClaim.setCounselsCost(counselsCostField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED);

            assertThat(detentionTravelWaitingCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(jrFormFillingCostField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(counselsCostField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }
    }

    @Nested
    class PaidInFullStatusTests {
        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCrimeClaimWithNoFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode(null);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);
            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCrimeClaimWithInvalidFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("ABCD");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetAssessedTotalFieldsToModifiableForCrimeClaimWithValidFeeCode() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);
            crimeClaim.setFeeCode("INVC");

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }

        @Test
        void shouldSetAssessedTotalFieldsToNotModifiableForCivilClaim() {
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            ClaimField assessedTotalVatField = ClaimField.builder().key(ASSESSED_TOTAL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            ClaimField assessedTotalInclVatField = ClaimField.builder().key(ASSESSED_TOTAL_INCL_VAT).type(ClaimFieldType.ASSESSED_TOTAL).build();
            crimeClaim.setAssessedTotalVat(assessedTotalVatField);
            crimeClaim.setAssessedTotalInclVat(assessedTotalInclVatField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(assessedTotalVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(assessedTotalInclVatField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetFixedFeeFieldToNotModifiable() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField field = ClaimField.builder().key(FIXED_FEE).type(ClaimFieldType.FIXED_FEE).build();
            crimeClaim.setFixedFee(field);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(field.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetTotalAmountFieldToNotModifiable() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField field = ClaimField.builder().key(TOTAL).type(ClaimFieldType.CALCULATED_TOTAL).build();
            crimeClaim.setTotalAmount(field);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(field.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetBoltOnFieldsToNotModifiable() {
            ClaimField adjournedHearingField = ClaimField.builder().key(ADJOURNED_FEE).type(ClaimFieldType.BOLT_ON).build();
            ClaimField cmrhTelephoneField = ClaimField.builder().key(CMRH_TELEPHONE).type(ClaimFieldType.BOLT_ON).build();
            ClaimField cmrhOralField = ClaimField.builder().key(CMRH_ORAL).type(ClaimFieldType.BOLT_ON).build();
            ClaimField hoInterviewField = ClaimField.builder().key(HO_INTERVIEW).type(ClaimFieldType.BOLT_ON).build();
            ClaimField substantiveHearingField = ClaimField.builder().key(SUBSTANTIVE_HEARING).type(ClaimFieldType.BOLT_ON).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(adjournedHearingField);
            claimDetails.setCmrhTelephone(cmrhTelephoneField);
            claimDetails.setCmrhOral(cmrhOralField);
            claimDetails.setHoInterview(hoInterviewField);
            claimDetails.setSubstantiveHearing(substantiveHearingField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.PAID_IN_FULL);

            assertThat(adjournedHearingField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(cmrhTelephoneField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(cmrhOralField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(hoInterviewField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(substantiveHearingField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetAssessableCrimeFieldsToModifiable() {
            ClaimField travelCostsField = ClaimField.builder().key(TRAVEL_COSTS).type(ClaimFieldType.OTHER).build();
            ClaimField waitingCostsField = ClaimField.builder().key(WAITING_COSTS).type(ClaimFieldType.OTHER).build();
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setTravelCosts(travelCostsField);
            crimeClaim.setWaitingCosts(waitingCostsField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(travelCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(waitingCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }

        @Test
        void shouldSetAssessableCivilFieldsToModifiable() {
            ClaimField detentionTravelWaitingCostsField = ClaimField.builder().key(DETENTION_TRAVEL_COST).type(ClaimFieldType.OTHER).build();
            ClaimField jrFormFillingCostField = ClaimField.builder().key(JR_FORM_FILLING).type(ClaimFieldType.OTHER).build();
            ClaimField counselsCostField = ClaimField.builder().key(COUNSELS_COST).type(ClaimFieldType.OTHER).build();
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            crimeClaim.setDetentionTravelWaitingCosts(detentionTravelWaitingCostsField);
            crimeClaim.setJrFormFillingCost(jrFormFillingCostField);
            crimeClaim.setCounselsCost(counselsCostField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(detentionTravelWaitingCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(jrFormFillingCostField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(counselsCostField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }
    }

    @Nested
    class ReducedToFixedFeeStatusTests {
        @Test
        void shouldSetAssessableCrimeFieldsToModifiable() {
            ClaimField travelCostsField = ClaimField.builder().key(TRAVEL_COSTS).type(ClaimFieldType.OTHER).build();
            ClaimField waitingCostsField = ClaimField.builder().key(WAITING_COSTS).type(ClaimFieldType.OTHER).build();
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setTravelCosts(travelCostsField);
            crimeClaim.setWaitingCosts(waitingCostsField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(travelCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(waitingCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }

        @Test
        void shouldSetAssessableCivilFieldsToModifiable() {
            ClaimField detentionTravelWaitingCostsField = ClaimField.builder().key(DETENTION_TRAVEL_COST).type(ClaimFieldType.OTHER).build();
            ClaimField jrFormFillingCostField = ClaimField.builder().key(JR_FORM_FILLING).type(ClaimFieldType.OTHER).build();
            ClaimField counselsCostField = ClaimField.builder().key(COUNSELS_COST).type(ClaimFieldType.OTHER).build();
            CivilClaimDetails crimeClaim = new CivilClaimDetails();
            crimeClaim.setDetentionTravelWaitingCosts(detentionTravelWaitingCostsField);
            crimeClaim.setJrFormFillingCost(jrFormFillingCostField);
            crimeClaim.setCounselsCost(counselsCostField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(detentionTravelWaitingCostsField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(jrFormFillingCostField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
            assertThat(counselsCostField.getStatus()).isEqualTo(ClaimFieldStatus.MODIFIABLE);
        }

        @Test
        void shouldSetBoltOnFieldsToNotModifiable() {
            ClaimField adjournedHearingField = ClaimField.builder().key(ADJOURNED_FEE).type(ClaimFieldType.BOLT_ON).build();
            ClaimField cmrhTelephoneField = ClaimField.builder().key(CMRH_TELEPHONE).type(ClaimFieldType.BOLT_ON).build();
            ClaimField cmrhOralField = ClaimField.builder().key(CMRH_ORAL).type(ClaimFieldType.BOLT_ON).build();
            ClaimField hoInterviewField = ClaimField.builder().key(HO_INTERVIEW).type(ClaimFieldType.BOLT_ON).build();
            ClaimField substantiveHearingField = ClaimField.builder().key(SUBSTANTIVE_HEARING).type(ClaimFieldType.BOLT_ON).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(adjournedHearingField);
            claimDetails.setCmrhTelephone(cmrhTelephoneField);
            claimDetails.setCmrhOral(cmrhOralField);
            claimDetails.setHoInterview(hoInterviewField);
            claimDetails.setSubstantiveHearing(substantiveHearingField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(adjournedHearingField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(cmrhTelephoneField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(cmrhOralField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(hoInterviewField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
            assertThat(substantiveHearingField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetFixedFeeFieldToNotModifiable() {
            ClaimField fixedFeeField = ClaimField.builder().key(FIXED_FEE).type(ClaimFieldType.FIXED_FEE).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setFixedFee(fixedFeeField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(fixedFeeField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }

        @Test
        void shouldSetTotalAmountFieldToNotModifiable() {
            ClaimField totalAmountField = ClaimField.builder().key(TOTAL).type(ClaimFieldType.CALCULATED_TOTAL).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setTotalAmount(totalAmountField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(totalAmountField.getStatus()).isEqualTo(ClaimFieldStatus.NOT_MODIFIABLE);
        }
    }
}