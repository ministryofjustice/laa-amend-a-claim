package uk.gov.justice.laa.payments.amend.models;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record ClaimCaseInsert(
    String id,
    String claimId,
    String caseId,
    String uniqueCaseId,
    String outcomeCode,
    String stageReachedCode,
    String userId)
    implements Insert {

  @Override
  public String table() {
    return "claim_case";
  }

  @Override
  public List<Object> parameters() {
    return Arrays.asList(
        id, claimId, caseId, uniqueCaseId, outcomeCode, stageReachedCode, userId, userId);
  }
}
