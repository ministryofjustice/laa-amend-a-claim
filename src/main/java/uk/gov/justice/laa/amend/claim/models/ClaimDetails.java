package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ClaimDetails extends Claim {
    private String areaOfLaw;
    private String categoryOfLaw;
    private String matterTypeCode;
    private String scheduleReference;
    private String providerName;
    private Boolean escaped;
    private Boolean vatApplicable;
    private String providerAccountNumber;
    private ClaimField vatClaimed;
    private ClaimField fixedFee;
    private ClaimField netProfitCost;
    private ClaimField netDisbursementAmount;
    private ClaimField totalAmount;
    private ClaimField disbursementVatAmount;

    private ClaimField assessedTotalVat;
    private ClaimField assessedTotalInclVat;
    private ClaimField allowedTotalVat;
    private ClaimField allowedTotalInclVat;

    private OutcomeType assessmentOutcome;
    private LocalDateTime submittedDate;
    private String feeCode;
    private String feeCodeDescription;
    private boolean hasAssessment;
    private AssessmentInfo lastAssessment;

    public void setNilledValues() {
        // Costs Table
        applyIfNotNull(netProfitCost, ClaimField::setNilled);
        applyIfNotNull(fixedFee, ClaimField::setNilled);
        applyIfNotNull(netDisbursementAmount, ClaimField::setNilled);
        applyIfNotNull(disbursementVatAmount, ClaimField::setNilled);

        // Assessed Totals Table
        applyIfNotNull(assessedTotalVat, ClaimField::setAssessedToNull);
        applyIfNotNull(assessedTotalInclVat, ClaimField::setAssessedToNull);

        // Allowed Totals Table
        applyIfNotNull(allowedTotalInclVat, ClaimField::setNilled);
        applyIfNotNull(allowedTotalVat, ClaimField::setNilled);
    }

    public void setReducedToFixedFeeValues() {
        // Costs Table
        applyIfNotNull(vatClaimed, ClaimField::setAssessedToCalculated);
        applyIfNotNull(fixedFee, ClaimField::setAssessedToCalculated);
        applyIfNotNull(netProfitCost, ClaimField::setAssessedToNull);
        applyIfNotNull(netDisbursementAmount, ClaimField::setAssessedToCalculated);
        applyIfNotNull(disbursementVatAmount, ClaimField::setAssessedToCalculated);

        // Assessed Totals Table
        applyIfNotNull(assessedTotalVat, ClaimField::setAssessedToNull);
        applyIfNotNull(assessedTotalInclVat, ClaimField::setAssessedToNull);

        // Allowed Totals Table
        applyIfNotNull(allowedTotalInclVat, ClaimField::setAssessedToNull);
        applyIfNotNull(allowedTotalVat, ClaimField::setAssessedToNull);
    }

    public void setReducedValues() {
        // Costs Table
        applyIfNotNull(fixedFee, ClaimField::setAssessedToNull);
        applyIfNotNull(netProfitCost, ClaimField::setAssessedToNull);
        applyIfNotNull(vatClaimed, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(netDisbursementAmount, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(disbursementVatAmount, ClaimField::setAssessedToSubmitted);

        // Assessed Totals Table
        applyIfNotNull(assessedTotalVat, ClaimField::setAssessedToNull);
        applyIfNotNull(assessedTotalInclVat, ClaimField::setAssessedToNull);

        // Allowed Totals Table
        applyIfNotNull(allowedTotalInclVat, ClaimField::setAssessedToNull);
        applyIfNotNull(allowedTotalVat, ClaimField::setAssessedToNull);
    }

    public void setPaidInFullValues() {
        // Costs Table
        applyIfNotNull(fixedFee, ClaimField::setAssessedToNull);
        applyIfNotNull(netProfitCost, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(vatClaimed, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(netDisbursementAmount, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(disbursementVatAmount, ClaimField::setAssessedToSubmitted);

        // Assessed Totals Table
        applyIfNotNull(assessedTotalVat, ClaimField::setAssessedToNull);
        applyIfNotNull(assessedTotalInclVat, ClaimField::setAssessedToNull);

        // Allowed Totals Table
        applyIfNotNull(allowedTotalInclVat, ClaimField::setAssessedToNull);
        applyIfNotNull(allowedTotalVat, ClaimField::setAssessedToNull);
    }

    protected void applyIfNotNull(ClaimField field, Consumer<ClaimField> f) {
        if (field != null) {
            f.accept(field);
        }
    }

    public abstract boolean getIsCrimeClaim();

    public abstract ClaimDetailsView<? extends ClaimDetails> toViewModel();

    public abstract AssessmentPost toAssessment(AssessmentMapper mapper, String userId);

    public List<ClaimField> getClaimFields() {
        return Stream.concat(
                commonClaimFields(),
                specificClaimFields()
            )
            .filter(Objects::nonNull)
            .toList();
    }

    protected Stream<ClaimField> commonClaimFields() {
        return Stream.of(
            getVatClaimed(),
            getFixedFee(),
            getNetProfitCost(),
            getNetDisbursementAmount(),
            getDisbursementVatAmount(),
            getTotalAmount(),
            getAssessedTotalVat(),
            getAssessedTotalInclVat(),
            getAllowedTotalVat(),
            getAllowedTotalInclVat()
        );
    }

    protected abstract Stream<ClaimField> specificClaimFields();
}
