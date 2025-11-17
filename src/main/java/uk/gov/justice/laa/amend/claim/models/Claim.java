package uk.gov.justice.laa.amend.claim.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import lombok.Data;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimViewModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
public abstract class Claim implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String submissionId;
    private String claimId;
    private String uniqueFileNumber;
    private String caseReferenceNumber;
    private String clientSurname;
    private String clientForename;
    private String submittedDate;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth submissionPeriod;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate caseStartDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate caseEndDate;

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

    public abstract ClaimViewModel<? extends Claim> toViewModel();
}
