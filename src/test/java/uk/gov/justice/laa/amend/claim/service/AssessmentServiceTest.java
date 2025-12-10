package uk.gov.justice.laa.amend.claim.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentGet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssessmentServiceTest {

    @Mock
    private ClaimsApiClient claimsApiClient;

    @Mock
    private ClaimStatusHandler claimStatusHandler;

    @Mock
    private AssessmentMapper assessmentMapper;

    @InjectMocks
    private AssessmentService assessmentService;

    public AssessmentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class ApplyAssessmentOutcome{

        @Test
        void testNilledOutcome() {
            ClaimDetails claim = mock(ClaimDetails.class);

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

            verify(claim).setNilledValues();
        }

        @Test
        void testReducedToFixedFeeOutcome() {
            ClaimDetails claim = mock(ClaimDetails.class);

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            verify(claim).setReducedToFixedFeeValues();
        }

        @Test
        void testReducedOutcome() {
            ClaimDetails claim = mock(ClaimDetails.class);

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            verify(claim).setReducedValues();
        }

        @Test
        void testPaidInFullOutcome() {
            ClaimDetails claim = mock(ClaimDetails.class);

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            verify(claim).setPaidInFullValues();
        }
    }

    @Nested
    class SubmitAssessmentTests {

        @Test
        void testCivilClaimAssessmentSubmittedToApi() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId))
                .thenReturn(assessment);

            ResponseEntity<CreateAssessment201Response> response = ResponseEntity.ok(new CreateAssessment201Response());

            when(claimsApiClient.submitAssessment(claimId, assessment))
                .thenReturn(Mono.just(response));

            CreateAssessment201Response result =
                assessmentService.submitAssessment(claim, userId);

            Assertions.assertNotNull(result);
            assertEquals(response.getBody(), result);

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);
        }

        @Test
        void testCrimeClaimAssessmentSubmittedToApi() {
            String claimId = UUID.randomUUID().toString();
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
            claim.setClaimId(claimId);
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCrimeClaimToAssessment(claim, userId))
                .thenReturn(assessment);

            ResponseEntity<CreateAssessment201Response> response = ResponseEntity.ok(new CreateAssessment201Response());

            when(claimsApiClient.submitAssessment(claimId, assessment))
                .thenReturn(Mono.just(response));

            CreateAssessment201Response result =
                assessmentService.submitAssessment(claim, userId);

            Assertions.assertNotNull(result);
            assertEquals(response.getBody(), result);

            verify(assessmentMapper).mapCrimeClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);
        }

        @Test
        void testWhenApiReturnsEmpty() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId))
                .thenReturn(assessment);

            when(claimsApiClient.submitAssessment(claimId, assessment))
                .thenReturn(Mono.empty());

            assertThrows(
                RuntimeException.class,
                () -> assessmentService.submitAssessment(claim, userId)
            );

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);
        }

        @Test
        void testWhenApiReturnsNullBody() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId))
                .thenReturn(assessment);

            when(claimsApiClient.submitAssessment(claimId, assessment))
                .thenReturn(Mono.just(ResponseEntity.ok(null)));

            assertThrows(
                RuntimeException.class,
                () -> assessmentService.submitAssessment(claim, userId)
            );

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);
        }
    }

    @Nested
    class GetAssessmentsTest {
        @Test
        void shouldReturnMappedClaimDetailsWhenAssessmentExists() {

            var claimDetails = new CivilClaimDetails();
            claimDetails.setClaimId(UUID.randomUUID().toString());

            // Arrange
            AssessmentGet assessment = new AssessmentGet(); // dummy assessment
            AssessmentResultSet resultSet = new AssessmentResultSet();
            resultSet.setAssessments(List.of(assessment));
            when(claimsApiClient.getAssessments(UUID.fromString(claimDetails.getClaimId())))
                    .thenReturn(Mono.just(resultSet));

            ClaimDetails mappedDetails = new CivilClaimDetails();
            when(assessmentMapper.mapAssessmentToClaimDetails(assessment, claimDetails))
                    .thenReturn(mappedDetails);
            // Act
            ClaimDetails result = assessmentService.getLatestAssessmentByClaim(claimDetails);
            // Assert
            assertThat(result).isEqualTo(mappedDetails);
            verify(claimsApiClient).getAssessments(UUID.fromString(claimDetails.getClaimId()));
            verify(assessmentMapper).mapAssessmentToClaimDetails(assessment, claimDetails);
        }

        @Test
        void shouldThrowExceptionWhenAssessmentsAreEmpty() {
            var claimDetails = new CivilClaimDetails();
            claimDetails.setClaimId(UUID.randomUUID().toString());
            // Arrange
            AssessmentResultSet emptyResultSet = new AssessmentResultSet();
            emptyResultSet.setAssessments(List.of());
            when(claimsApiClient.getAssessments(UUID.fromString(claimDetails.getClaimId())))
                    .thenReturn(Mono.just(emptyResultSet));

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> assessmentService.getLatestAssessmentByClaim(claimDetails));

            assertThat(ex.getMessage()).contains("Failed to get assessments");
        }


        @Test
        void shouldThrowExceptionWhenResultIsNull() {
            var claimDetails = new CivilClaimDetails();
            claimDetails.setClaimId(UUID.randomUUID().toString());
            // Arrange
            when(claimsApiClient.getAssessments(UUID.fromString(claimDetails.getClaimId())))
                    .thenReturn(Mono.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> assessmentService.getLatestAssessmentByClaim(claimDetails));

            assertThat(ex.getMessage()).contains("Failed to get assessments");
        }
    }
}