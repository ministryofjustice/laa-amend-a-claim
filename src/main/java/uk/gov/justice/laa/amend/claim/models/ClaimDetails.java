package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.time.LocalDateTime;
import java.util.function.Consumer;

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

        // Allowed Totals Table
        applyIfNotNull(allowedTotalInclVat, ClaimField::setNilled);
        applyIfNotNull(allowedTotalVat, ClaimField::setNilled);
    }

    public void setReducedToFixedFeeValues() {
        // Costs Table
        applyIfNotNull(vatClaimed, ClaimField::setAmendedToCalculated);
        applyIfNotNull(fixedFee, ClaimField::setAmendedToCalculated);
        applyIfNotNull(netProfitCost, ClaimField::setToNeedsAmending);
        applyIfNotNull(netDisbursementAmount, ClaimField::setAmendedToCalculated);
        applyIfNotNull(disbursementVatAmount, ClaimField::setAmendedToCalculated);

        // Allowed Totals Table
        applyIfNotNull(allowedTotalInclVat, ClaimField::setToNeedsAmending);
        applyIfNotNull(allowedTotalVat, ClaimField::setToNeedsAmending);
    }

    public void setReducedValues() {
        // Costs Table
        applyIfNotNull(fixedFee, ClaimField::setToNotApplicable);
        applyIfNotNull(netProfitCost, ClaimField::setToNeedsAmending);
        applyIfNotNull(vatClaimed, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(netDisbursementAmount, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(disbursementVatAmount, ClaimField::setAmendedToSubmitted);

        // Allowed Totals Table
        applyIfNotNull(allowedTotalInclVat, ClaimField::setToNeedsAmending);
        applyIfNotNull(allowedTotalVat, ClaimField::setToNeedsAmending);
    }

    public void setPaidInFullValues() {
        // Costs Table
        applyIfNotNull(fixedFee, ClaimField::setToNotApplicable);
        applyIfNotNull(netProfitCost, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(vatClaimed, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(netDisbursementAmount, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(disbursementVatAmount, ClaimField::setAmendedToSubmitted);

        // Allowed Totals Table
        applyIfNotNull(allowedTotalInclVat, ClaimField::setToNeedsAmending);
        applyIfNotNull(allowedTotalVat, ClaimField::setToNeedsAmending);
    }

    protected void applyIfNotNull(ClaimField field, Consumer<ClaimField> f) {
        if (field != null) {
            f.accept(field);
        }
    }

    public abstract boolean getIsCrimeClaim();

    public abstract ClaimDetailsView<? extends ClaimDetails> toViewModel();

    public abstract AssessmentPost toAssessment(AssessmentMapper mapper, String userId);
}
