package uk.gov.justice.laa.amend.claim.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import lombok.Data;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimView;
import uk.gov.justice.laa.amend.claim.viewmodels.BaseClaimView;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

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
    private String categoryOfLaw;
    private Boolean escaped;
    private Boolean vatApplicable;
    private String scheduleReference;

    public BaseClaimView<? extends Claim> toViewModel() {
        return new ClaimView(this);
    }

    public String reviewAssessmentChangeUrl(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/assessment-outcome",  submissionId, claimId);
    }
}
