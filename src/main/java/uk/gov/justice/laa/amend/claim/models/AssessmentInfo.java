package uk.gov.justice.laa.amend.claim.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record AssessmentInfo(
        OffsetDateTime lastAssessmentDate,
        String lastAssessedBy,
        OutcomeType lastAssessmentOutcome,
        UUID id,
        UUID claimId,
        BigDecimal fixedFeeAmount,
        BigDecimal netProfitCostsAmount,
        BigDecimal disbursementAmount,
        BigDecimal disbursementVatAmount,
        BigDecimal netCostOfCounselAmount,
        BigDecimal netTravelCostsAmount,
        BigDecimal netWaitingCostsAmount,
        BigDecimal detentionTravelAndWaitingCostsAmount,
        BigDecimal jrFormFillingAmount,
        BigDecimal boltOnAdjournedHearingFee,
        BigDecimal boltOnCmrhTelephoneFee,
        BigDecimal boltOnCmrhOralFee,
        BigDecimal boltOnHomeOfficeInterviewFee,
        BigDecimal boltOnSubstantiveHearingFee,
        Boolean isVatApplicable,
        BigDecimal assessedTotalVat,
        BigDecimal assessedTotalInclVat,
        BigDecimal allowedTotalVat,
        BigDecimal allowedTotalInclVat,
        AssessmentTypeEnum assessmentType) {}
