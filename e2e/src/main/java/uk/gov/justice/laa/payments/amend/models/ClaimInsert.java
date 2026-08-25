package uk.gov.justice.laa.payments.amend.models;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record ClaimInsert(
    String id,
    String submissionId,
    String status,
    Integer lineNumber,
    String uniqueFileNumber,
    String matterType,
    String crimeMatterType,
    String feeCode,
    String outreachLocation,
    String referralSource,
    String userId,
    Boolean hasAssessment)
    implements Insert {

  @Override
  public String table() {
    return "claim";
  }

  @Override
  public List<Object> parameters() {
    return Arrays.asList(
        id,
        submissionId,
        status != null ? status : "VALID",
        lineNumber != null ? lineNumber : 1,
        uniqueFileNumber,
        matterType != null ? matterType : "IMCB:IRVL",
        crimeMatterType,
        feeCode != null ? feeCode : "INVC",
        outreachLocation,
        referralSource,
        userId,
        userId,
        hasAssessment != null ? hasAssessment : false);
  }
}
