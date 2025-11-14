package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimViewModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public abstract class Claim2 implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String submissionId;
    private String claimId;
    private String uniqueFileNumber;
    private String caseReferenceNumber;
    private String clientSurname;
    private String clientForename;
    private String submittedDate;
    private LocalDate caseStartDate;
    private LocalDate caseEndDate;
    private String feeScheme;
    private String categoryOfLaw;
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

    @JsonIgnore
    public String getClientName() {
        if (clientForename != null & clientSurname != null) {
            return String.format("%s %s", clientForename, clientSurname);
        } else if (clientForename != null) {
            return clientForename;
        } else if (clientSurname != null) {
            return clientSurname;
        } else {
            return null;
        }
    }

    public void setNilledValues() {
        if (netProfitCost != null) {
            netProfitCost.setAmended(BigDecimal.ZERO);
        }
        if (netDisbursementAmount != null) {
            netDisbursementAmount.setAmended(BigDecimal.ZERO);
        }
        if (disbursementVatAmount != null) {
            disbursementVatAmount.setAmended(BigDecimal.ZERO);
        }
    }

    public abstract ClaimViewModel<? extends Claim2> toViewModel();
}
