package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class AssessmentInfo implements Serializable {
    private OffsetDateTime lastAssessmentDate;
    private String lastAssessedBy;
    private OutcomeType lastAssessmentOutcome;
    private UUID id;
    private UUID claimId;
    private BigDecimal fixedFeeAmount;
    private BigDecimal netProfitCostsAmount;
    private BigDecimal disbursementAmount;
    private BigDecimal disbursementVatAmount;
    private BigDecimal netCostOfCounselAmount;
    private BigDecimal netTravelCostsAmount;
    private BigDecimal netWaitingCostsAmount;
    private BigDecimal detentionTravelAndWaitingCostsAmount;
    private BigDecimal jrFormFillingAmount;
    private BigDecimal boltOnAdjournedHearingFee;
    private BigDecimal boltOnCmrhTelephoneFee;
    private BigDecimal boltOnCmrhOralFee;
    private BigDecimal boltOnHomeOfficeInterviewFee;
    private BigDecimal boltOnSubstantiveHearingFee;
    private Boolean isVatApplicable;
    private BigDecimal assessedTotalVat;
    private BigDecimal assessedTotalInclVat;
    private BigDecimal allowedTotalVat;
    private BigDecimal allowedTotalInclVat;
}
