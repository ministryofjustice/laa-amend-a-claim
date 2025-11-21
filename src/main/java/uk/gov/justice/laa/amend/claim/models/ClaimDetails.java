package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Function;

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
        setAmendedToValue(netProfitCost, BigDecimal.ZERO);
        setAmendedToValue(netDisbursementAmount, BigDecimal.ZERO);
        setAmendedToValue(disbursementVatAmount, BigDecimal.ZERO);
    }

    public void setReducedToFixedFeeValues() {
        setAmendedToCalculated(vatClaimed);
        setAmendedToCalculated(fixedFee);
        setAmendedToValue(netProfitCost, null);
        setAmendedToCalculated(netDisbursementAmount);
        setAmendedToCalculated(totalAmount);
        setAmendedToCalculated(disbursementVatAmount);
    }

    public abstract boolean getIsCrimeClaim();

    protected void setAmendedToValue(ClaimField claimField, Object value) {
        setAmended(claimField, cf -> value);
    }

    protected void setAmendedToCalculated(ClaimField claimField) {
        setAmended(claimField, ClaimField::getCalculated);
    }

    private void setAmended(ClaimField claimField, Function<ClaimField, Object> f) {
        if (claimField != null) {
            claimField.setAmended(f.apply(claimField));
        }
    }

    public abstract ClaimDetailsView<? extends ClaimDetails> toViewModel();
}
