package uk.gov.justice.laa.amend.claim.models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.YearMonthDeserializer;
import tools.jackson.databind.ext.javatime.ser.YearMonthSerializer;
import uk.gov.justice.laa.amend.claim.viewmodels.BaseClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@Data
public class Claim implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String submissionId;
    private String claimId;
    private String claimSummaryFeeId;
    private String uniqueFileNumber;
    private String caseReferenceNumber;
    private String clientSurname;
    private String clientForename;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth submissionPeriod;

    private LocalDate caseStartDate;

    private LocalDate caseEndDate;
    private AreaOfLaw areaOfLaw;
    private String categoryOfLaw;
    private Boolean escaped;
    private Boolean vatApplicable;
    private String providerAccountNumber;
    private ClaimStatus status;

    public BaseClaimView<? extends Claim> toViewModel() {
        return new ClaimView(this);
    }

    public Boolean isValid() {
        return status == ClaimStatus.VALID;
    }

    public Boolean isVoided() {
        return status == ClaimStatus.VOID;
    }
}
