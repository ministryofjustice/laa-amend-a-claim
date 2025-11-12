package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import static uk.gov.justice.laa.amend.claim.utils.FormUtils.getClientFullName;

@Data
public class ClaimSummary implements Serializable {
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
    private ClaimFieldRow vatClaimed;
    private ClaimFieldRow fixedFee;
    private ClaimFieldRow netProfitCost;
    private ClaimFieldRow netDisbursementAmount;
    private ClaimFieldRow totalAmount;
    private ClaimFieldRow disbursementVatAmount;


    public String getClientName() {
        return getClientFullName(clientForename, clientSurname);
    }
}
