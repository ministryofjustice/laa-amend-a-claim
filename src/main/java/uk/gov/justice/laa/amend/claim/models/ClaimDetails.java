package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        setAmendedValue(netProfitCost, BigDecimal.ZERO);
        setAmendedValue(netDisbursementAmount, BigDecimal.ZERO);
        setAmendedValue(disbursementVatAmount, BigDecimal.ZERO);
    }

    public abstract boolean getIsCrimeClaim();

    protected void setAmendedValue(ClaimField claimField, Object value) {
        if (claimField != null) {
            claimField.setAmended(value);
        }
    }

    public abstract ClaimDetailsView<? extends ClaimDetails> toViewModel();
}
