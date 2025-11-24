package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ClaimDetails extends Claim {
    private String areaOfLaw;
    private String feeScheme;
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
    private OutcomeType assessmentOutcome;
    private LocalDate submittedDate;

    public void setNilledValues() {
        applyIfNotNull(netProfitCost, cf -> cf.setNilled(BigDecimal.ZERO));
        applyIfNotNull(netDisbursementAmount, cf -> cf.setNilled(BigDecimal.ZERO));
        applyIfNotNull(disbursementVatAmount, cf -> cf.setNilled(BigDecimal.ZERO));
    }

    public void setReducedToFixedFeeValues() {
        applyIfNotNull(vatClaimed, ClaimField::setAmendedToCalculated);
        applyIfNotNull(fixedFee, ClaimField::setAmendedToCalculated);
        applyIfNotNull(netProfitCost, ClaimField::setToNeedsAmending);
        applyIfNotNull(netDisbursementAmount, ClaimField::setAmendedToCalculated);
        applyIfNotNull(totalAmount, ClaimField::setAmendedToCalculated);
        applyIfNotNull(disbursementVatAmount, ClaimField::setAmendedToCalculated);
    }

    public void setPaidInFullValues() {
        applyIfNotNull(vatClaimed, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(fixedFee, ClaimField::setAmendedToCalculated);
        applyIfNotNull(netProfitCost, ClaimField::setToNeedsAmending);
        applyIfNotNull(netDisbursementAmount, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(totalAmount, ClaimField::setAmendedToCalculated);
        applyIfNotNull(disbursementVatAmount, ClaimField::setAmendedToSubmitted);
    }

    protected void applyIfNotNull(ClaimField field, Consumer<ClaimField> f) {
        if (field != null) {
            f.accept(field);
        }
    }

    public abstract boolean getIsCrimeClaim();

    public abstract ClaimDetailsView<? extends ClaimDetails> toViewModel();
}
