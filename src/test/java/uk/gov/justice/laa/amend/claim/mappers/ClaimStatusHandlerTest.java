package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.AssessmentStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.NET_PROFIT_COST;

@ExtendWith(MockitoExtension.class)
class ClaimStatusHandlerTest {

    private ClaimStatusHandler claimStatusHandler;

    @Mock
    private ClaimDetails mockClaim;

    @Mock
    private ClaimField mockField;

    @BeforeEach
    void setUp() {
        claimStatusHandler = new ClaimStatusHandler();
    }

    @Nested
    class NilledStatusTests {
        @Test
        void shouldSetDoNotDisplayForTotalFields() {
            ClaimField assessedTotal = new ClaimField();
            ClaimDetails claimDetails = new CrimeClaimDetails();
            claimDetails.setAssessedTotalVat(assessedTotal);
            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.NILLED);
            assertThat(assessedTotal.getStatus()).isEqualTo(AssessmentStatus.DO_NOT_DISPLAY);
        }

        @Test
        void shouldSetAssessableForVatClaimed() {
            ClaimField vatField = new ClaimField();
            ClaimDetails civilClaimDetails = new CivilClaimDetails();
            civilClaimDetails.setVatClaimed(vatField);

            claimStatusHandler.updateFieldStatuses(civilClaimDetails, OutcomeType.NILLED);

            assertThat(vatField.getStatus()).isEqualTo(AssessmentStatus.ASSESSABLE);
        }

        @Test
        void shouldSetNotAssessableForOtherFields() {
            ClaimField otherField = new ClaimField();
            when(mockClaim.getClaimFields()).thenReturn(List.of(otherField));

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.NILLED);

            assertThat(otherField.getStatus()).isEqualTo(AssessmentStatus.NOT_ASSESSABLE);
        }
    }

    @Nested
    class ReducedStatusTests {
        @Test
        void shouldSetNeedsAssessingForNetProfitCost() {
            ClaimField profitCostField = new ClaimField();
            profitCostField.setAssessed(null);
            profitCostField.setKey(NET_PROFIT_COST);
            ClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setNetProfitCost(profitCostField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(profitCostField.getStatus()).isEqualTo(AssessmentStatus.ASSESSABLE);
        }

        @Test
        void shouldSetNotAssessableForBoltOnFields() {
            ClaimField boltOnField = ClaimField.builder().key(ADJOURNED_FEE).build();
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setAdjournedHearing(boltOnField);

            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(boltOnField.getStatus()).isEqualTo(AssessmentStatus.NOT_ASSESSABLE);
        }

        @Test
        void shouldSetAssessableForOtherFields() {
            ClaimField otherField = new ClaimField();
            otherField.setKey("OTHER_FIELD");
            CivilClaimDetails claimDetails = new CivilClaimDetails();
            claimDetails.setFixedFee(otherField);


            claimStatusHandler.updateFieldStatuses(claimDetails, OutcomeType.REDUCED);

            assertThat(otherField.getStatus()).isEqualTo(AssessmentStatus.ASSESSABLE);
        }
    }

    @Nested
    class PaidInFullStatusTests {
        @Test
        void shouldSetDoNotDisplayForCrimeClaim() {
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            ClaimField totalField = new ClaimField();
            crimeClaim.setAssessedTotalVat(totalField);
            crimeClaim.setFeeCode(null);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(totalField.getStatus()).isEqualTo(AssessmentStatus.DO_NOT_DISPLAY);
        }

        @Test
        void shouldSetNeedsAssessingForUnassessedFields() {
            ClaimField unassessedField = new ClaimField();
            unassessedField.setAssessed(null);
            unassessedField.setKey(NET_PROFIT_COST);
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setNetProfitCost(unassessedField);


            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(unassessedField.getStatus()).isEqualTo(AssessmentStatus.ASSESSABLE);
        }
    }

    @Nested
    class ReducedToFixedFeeStatusTests {
        @Test
        void shouldSetNeedsAssessingForUnassessedFields() {
            ClaimField unassessedField = new ClaimField();
            unassessedField.setAssessed(null);
            unassessedField.setKey(NET_PROFIT_COST);
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setNetProfitCost(unassessedField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(unassessedField.getStatus()).isEqualTo(AssessmentStatus.ASSESSABLE);
        }

        @Test
        void shouldSetAssessableForAssessedFields() {
            ClaimField assessedField = new ClaimField();
            assessedField.setAssessed("100");
            CrimeClaimDetails crimeClaim = new CrimeClaimDetails();
            crimeClaim.setFixedFee(assessedField);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(assessedField.getStatus()).isEqualTo(AssessmentStatus.NOT_ASSESSABLE);
        }
    }

    @Test
    void shouldProcessAllFieldsInOrder() {
        ClaimField field1 = new ClaimField();
        ClaimField field2 = new ClaimField();

        when(mockClaim.getClaimFields()).thenReturn(List.of(field1, field2));

        claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.NILLED);

        assertThat(field1.getStatus()).isNotNull();
        assertThat(field2.getStatus()).isNotNull();
    }
}