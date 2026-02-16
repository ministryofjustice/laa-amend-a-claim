package uk.gov.justice.laa.amend.claim.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentGet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    private static final BigDecimal HIGH_VALUE_ASSESSMENT_LIMIT = new BigDecimal("25000");

    @Mock
    private ClaimsApiClient claimsApiClient;

    @Mock
    private ClaimStatusHandler claimStatusHandler;

    @Mock
    private AssessmentMapper assessmentMapper;

    private SimpleMeterRegistry meterRegistry;
    private AssessmentService assessmentService;

    private UUID claimId;
    private int page;
    private int size;
    private String sort;

    @BeforeEach
    void setUp() {
        claimId = UUID.randomUUID();
        page = 0;
        size = 1;
        sort = "createdOn,desc";
        meterRegistry = new SimpleMeterRegistry();
        assessmentService = new AssessmentService(
                claimsApiClient, assessmentMapper, claimStatusHandler, meterRegistry, HIGH_VALUE_ASSESSMENT_LIMIT);
    }

    @Nested
    class ApplyAssessmentOutcome {

        @Test
        void testNilledOutcome() {
            ClaimDetails claim = mock(ClaimDetails.class);

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.NILLED);

            verify(claim).applyOutcome(OutcomeType.NILLED);
        }

        @Test
        void testReducedToFixedFeeOutcome() {
            ClaimDetails claim = mock(ClaimDetails.class);

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED_TO_FIXED_FEE);

            verify(claim).applyOutcome(OutcomeType.REDUCED_TO_FIXED_FEE);
        }

        @Test
        void testReducedOutcome() {
            ClaimDetails claim = mock(ClaimDetails.class);

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.REDUCED);

            verify(claim).applyOutcome(OutcomeType.REDUCED);
        }

        @Test
        void testPaidInFullOutcome() {
            ClaimDetails claim = mock(ClaimDetails.class);

            assessmentService.applyAssessmentOutcome(claim, OutcomeType.PAID_IN_FULL);

            verify(claim).applyOutcome(OutcomeType.PAID_IN_FULL);
        }
    }

    @Nested
    class SubmitAssessmentTests {

        @Test
        void testCivilClaimAssessmentSubmittedToApiAndIncrementsSuccessCounter() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            UUID userId = UUID.randomUUID();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId)).thenReturn(assessment);

            ResponseEntity<CreateAssessment201Response> response = ResponseEntity.ok(new CreateAssessment201Response());

            when(claimsApiClient.submitAssessment(claimId, assessment)).thenReturn(Mono.just(response));

            CreateAssessment201Response result = assessmentService.submitAssessment(claim, userId);

            Assertions.assertNotNull(result);
            assertEquals(response.getBody(), result);

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);

            assertThat(meterRegistry.counter("assessment.submissions").count()).isEqualTo(1.0);
            assertThat(meterRegistry.counter("assessment.submissions.failed").count())
                    .isEqualTo(0.0);
        }

        @Test
        void testCrimeClaimAssessmentSubmittedToApiAndIncrementsSuccessCounter() {
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
            claim.setClaimId(claimId.toString());
            UUID userId = UUID.randomUUID();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCrimeClaimToAssessment(claim, userId)).thenReturn(assessment);

            ResponseEntity<CreateAssessment201Response> response = ResponseEntity.ok(new CreateAssessment201Response());

            when(claimsApiClient.submitAssessment(claimId.toString(), assessment))
                    .thenReturn(Mono.just(response));

            CreateAssessment201Response result = assessmentService.submitAssessment(claim, userId);

            Assertions.assertNotNull(result);
            assertEquals(response.getBody(), result);

            verify(assessmentMapper).mapCrimeClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId.toString(), assessment);

            assertThat(meterRegistry.counter("assessment.submissions").count()).isEqualTo(1.0);
            assertThat(meterRegistry.counter("assessment.submissions.failed").count())
                    .isEqualTo(0.0);
        }

        @Test
        void testWhenApiReturnsEmptyAndIncrementsFailureCounter() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            UUID userId = UUID.randomUUID();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId)).thenReturn(assessment);

            when(claimsApiClient.submitAssessment(claimId, assessment)).thenReturn(Mono.empty());

            assertThrows(RuntimeException.class, () -> assessmentService.submitAssessment(claim, userId));

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);

            assertThat(meterRegistry.counter("assessment.submissions").count()).isEqualTo(0.0);
            assertThat(meterRegistry.counter("assessment.submissions.failed").count())
                    .isEqualTo(1.0);
        }

        @Test
        void testWhenApiReturns5xxStatusAndIncrementsFailureCounter() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            UUID userId = UUID.randomUUID();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId)).thenReturn(assessment);

            ResponseEntity<CreateAssessment201Response> response =
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CreateAssessment201Response());

            when(claimsApiClient.submitAssessment(claimId, assessment)).thenReturn(Mono.just(response));

            assertThrows(RuntimeException.class, () -> assessmentService.submitAssessment(claim, userId));

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);

            assertThat(meterRegistry.counter("assessment.submissions").count()).isEqualTo(0.0);
            assertThat(meterRegistry.counter("assessment.submissions.failed").count())
                    .isEqualTo(1.0);
        }

        @Test
        void testWhenWebClientThrowsExceptionAndIncrementsFailureCounterOnce() {
            String claimId = UUID.randomUUID().toString();
            CivilClaimDetails claim = MockClaimsFunctions.createMockCivilClaim();
            claim.setClaimId(claimId);
            UUID userId = UUID.randomUUID();
            AssessmentPost assessment = new AssessmentPost();

            when(assessmentMapper.mapCivilClaimToAssessment(claim, userId)).thenReturn(assessment);

            when(claimsApiClient.submitAssessment(claimId, assessment))
                    .thenReturn(Mono.error(new RuntimeException("Network error")));

            assertThrows(RuntimeException.class, () -> assessmentService.submitAssessment(claim, userId));

            verify(assessmentMapper).mapCivilClaimToAssessment(claim, userId);
            verify(claimsApiClient).submitAssessment(claimId, assessment);

            assertThat(meterRegistry.counter("assessment.submissions").count()).isEqualTo(0.0);
            assertThat(meterRegistry.counter("assessment.submissions.failed").count())
                    .isEqualTo(1.0);
        }

        @ExtendWith(OutputCaptureExtension.class)
        @Test
        void testHighValueAssessmentSubmittedWillLogWarning(CapturedOutput output) {
            setupHighValueAssessmentLimitTest(HIGH_VALUE_ASSESSMENT_LIMIT);

            assertThat(output.getOut()).contains("HIGH_VALUE_ASSESSMENT");
        }

        @ExtendWith(OutputCaptureExtension.class)
        @Test
        void testLowValueAssessmentSubmittedWillNotLogWarning(CapturedOutput output) {
            setupHighValueAssessmentLimitTest(HIGH_VALUE_ASSESSMENT_LIMIT.subtract(BigDecimal.ONE));

            assertThat(output.getOut()).doesNotContain("HIGH_VALUE_ASSESSMENT");
        }

        private void setupHighValueAssessmentLimitTest(BigDecimal assessedTotalInclVat) {
            CrimeClaimDetails claim = MockClaimsFunctions.createMockCrimeClaim();
            claim.setClaimId(claimId.toString());
            String userId = UUID.randomUUID().toString();
            AssessmentPost assessment = new AssessmentPost();
            assessment.setAssessedTotalInclVat(assessedTotalInclVat);

            when(assessmentMapper.mapCrimeClaimToAssessment(claim, userId)).thenReturn(assessment);

            ResponseEntity<CreateAssessment201Response> response = ResponseEntity.ok(new CreateAssessment201Response());

            when(claimsApiClient.submitAssessment(claimId.toString(), assessment))
                    .thenReturn(Mono.just(response));

            CreateAssessment201Response result = assessmentService.submitAssessment(claim, userId);

            Assertions.assertNotNull(result);
            assertEquals(response.getBody(), result);

            verify(claimsApiClient).submitAssessment(claimId.toString(), assessment);
        }
    }

    @Nested
    class GetAssessmentsTest {
        @Test
        void shouldReturnMappedClaimDetailsWhenAssessmentExists() {

            var claimDetails = new CivilClaimDetails();
            claimDetails.setClaimId(claimId.toString());

            // Arrange
            AssessmentGet assessment = new AssessmentGet(); // dummy assessment
            AssessmentResultSet resultSet = new AssessmentResultSet();
            resultSet.setAssessments(List.of(assessment));
            when(claimsApiClient.getAssessments(claimId, page, size, sort)).thenReturn(Mono.just(resultSet));

            ClaimDetails mappedDetails = new CivilClaimDetails();
            AssessmentInfo assessmentInfo = new AssessmentInfo();
            mappedDetails.setLastAssessment(assessmentInfo);
            when(assessmentMapper.updateClaim(assessment, claimDetails)).thenReturn(mappedDetails);
            when(assessmentMapper.mapAssessmentToClaimDetails(mappedDetails)).thenReturn(mappedDetails);
            // Act
            ClaimDetails result = assessmentService.getLatestAssessmentByClaim(claimDetails);
            // Assert
            assertThat(result).isEqualTo(mappedDetails);
            verify(claimsApiClient).getAssessments(claimId, page, size, sort);
            verify(assessmentMapper).mapAssessmentToClaimDetails(mappedDetails);
        }

        @Test
        void shouldThrowExceptionWhenAssessmentsAreEmpty() {
            var claimDetails = new CivilClaimDetails();
            claimDetails.setClaimId(claimId.toString());
            // Arrange
            AssessmentResultSet emptyResultSet = new AssessmentResultSet();
            emptyResultSet.setAssessments(List.of());
            when(claimsApiClient.getAssessments(claimId, page, size, sort)).thenReturn(Mono.just(emptyResultSet));

            // Act & Assert
            RuntimeException ex = assertThrows(
                    RuntimeException.class, () -> assessmentService.getLatestAssessmentByClaim(claimDetails));

            assertThat(ex.getMessage()).contains("Failed to get assessments");
        }

        @Test
        void shouldThrowExceptionWhenResultIsNull() {
            var claimDetails = new CivilClaimDetails();
            claimDetails.setClaimId(claimId.toString());
            // Arrange
            when(claimsApiClient.getAssessments(claimId, page, size, sort)).thenReturn(Mono.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(
                    RuntimeException.class, () -> assessmentService.getLatestAssessmentByClaim(claimDetails));

            assertThat(ex.getMessage()).contains("Failed to get assessments");
        }
    }
}
