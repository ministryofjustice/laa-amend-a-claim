package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.AssessStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;

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
            when(mockClaim.getAssessedTotalVat()).thenReturn(mockField);
            when(mockClaim.getVatClaimed()).thenReturn(null);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.NILLED);

            verify(mockField).setStatus(AssessStatus.DO_NOT_DISPLAY);
        }

        @Test
        void shouldSetAssessableForVatClaimed() {
            ClaimField vatField = new ClaimField();
            when(mockClaim.getVatClaimed()).thenReturn(vatField);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.NILLED);

            assertThat(vatField.getStatus()).isEqualTo(AssessStatus.ASSESSABLE);
        }

        @Test
        void shouldSetNotAssessableForOtherFields() {
            ClaimField otherField = new ClaimField();
            when(mockClaim.getFixedFee()).thenReturn(otherField);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.NILLED);

            assertThat(otherField.getStatus()).isEqualTo(AssessStatus.NOT_ASSESSABLE);
        }
    }

    @Nested
    class ReducedStatusTests {
        @Test
        void shouldSetNeedsAssessingForNetProfitCost() {
            ClaimField profitCostField = new ClaimField();
            when(mockClaim.getNetProfitCost()).thenReturn(profitCostField);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.REDUCED);

            assertThat(profitCostField.getStatus()).isEqualTo(AssessStatus.NEEDS_ASSESSING);
        }

        @Test
        void shouldSetNotAssessableForBoltOnFields() {
            ClaimField boltOnField = new ClaimField();
            boltOnField.setKey(ADJOURNED_FEE);
            when(mockClaim.getFixedFee()).thenReturn(boltOnField);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.REDUCED);

            assertThat(boltOnField.getStatus()).isEqualTo(AssessStatus.NOT_ASSESSABLE);
        }

        @Test
        void shouldSetAssessableForOtherFields() {
            ClaimField otherField = new ClaimField();
            otherField.setKey("OTHER_FIELD");
            when(mockClaim.getFixedFee()).thenReturn(otherField);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.REDUCED);

            assertThat(otherField.getStatus()).isEqualTo(AssessStatus.ASSESSABLE);
        }
    }

    @Nested
    class PaidInFullStatusTests {
        @Test
        void shouldSetDoNotDisplayForCrimeClaim() {
            CrimeClaimDetails crimeClaim = mock(CrimeClaimDetails.class);
            ClaimField totalField = new ClaimField();
            when(crimeClaim.getAssessedTotalVat()).thenReturn(totalField);
            when(crimeClaim.getFeeCode()).thenReturn(null);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.PAID_IN_FULL);

            assertThat(totalField.getStatus()).isEqualTo(AssessStatus.DO_NOT_DISPLAY);
        }

        @Test
        void shouldSetNeedsAssessingForUnassessedFields() {
            ClaimField unassessedField = new ClaimField();
            when(mockClaim.getNetProfitCost()).thenReturn(unassessedField);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.PAID_IN_FULL);

            assertThat(unassessedField.getStatus()).isEqualTo(AssessStatus.NEEDS_ASSESSING);
        }
    }

    @Nested
    class ReducedToFixedFeeStatusTests {
        @Test
        void shouldSetNeedsAssessingForUnassessedFields() {
            ClaimField unassessedField = new ClaimField();
            when(mockClaim.getNetProfitCost()).thenReturn(unassessedField);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(unassessedField.getStatus()).isEqualTo(AssessStatus.NEEDS_ASSESSING);
        }

        @Test
        void shouldSetAssessableForAssessedFields() {
            ClaimField assessedField = new ClaimField();
            assessedField.setAssessed("100");
            when(mockClaim.getFixedFee()).thenReturn(assessedField);

            claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.REDUCED_TO_FIXED_FEE);

            assertThat(assessedField.getStatus()).isEqualTo(AssessStatus.ASSESSABLE);
        }
    }

    @Nested
    class ExtractClaimFieldsTests {
        @Test
        void shouldExtractCivilClaimFields() {
            CivilClaimDetails civilClaim = mock(CivilClaimDetails.class);
            ClaimField hoInterview = new ClaimField();
            when(civilClaim.getHoInterview()).thenReturn(hoInterview);

            claimStatusHandler.updateFieldStatuses(civilClaim, OutcomeType.NILLED);

            verify(civilClaim).getHoInterview();
        }

        @Test
        void shouldExtractCrimeClaimFields() {
            CrimeClaimDetails crimeClaim = mock(CrimeClaimDetails.class);
            ClaimField travelCosts = new ClaimField();
            when(crimeClaim.getTravelCosts()).thenReturn(travelCosts);

            claimStatusHandler.updateFieldStatuses(crimeClaim, OutcomeType.NILLED);

            verify(crimeClaim).getTravelCosts();
        }
    }

    @Test
    void shouldHandleNullFields() {
        when(mockClaim.getVatClaimed()).thenReturn(null);
        when(mockClaim.getFixedFee()).thenReturn(null);

        claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.NILLED);

        verifyNoInteractions(mockField);
    }

    @Test
    void shouldProcessAllFieldsInOrder() {
        ClaimField field1 = new ClaimField();
        ClaimField field2 = new ClaimField();

        when(mockClaim.getVatClaimed()).thenReturn(field1);
        when(mockClaim.getFixedFee()).thenReturn(field2);

        claimStatusHandler.updateFieldStatuses(mockClaim, OutcomeType.NILLED);

        assertThat(field1.getStatus()).isNotNull();
        assertThat(field2.getStatus()).isNotNull();
    }
}