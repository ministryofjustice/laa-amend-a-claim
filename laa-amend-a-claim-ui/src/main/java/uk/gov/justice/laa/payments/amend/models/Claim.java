package uk.gov.justice.laa.payments.amend.models;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.YearMonthDeserializer;
import tools.jackson.databind.ext.javatime.ser.YearMonthSerializer;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.viewmodels.BaseClaimView;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimView;

@Data
public class Claim {

  private UUID submissionId;
  private UUID claimId;
  private UUID claimSummaryFeeId;
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
  private String officeCode;
  private ClaimStatus status;
  private String uniqueCaseId;

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
