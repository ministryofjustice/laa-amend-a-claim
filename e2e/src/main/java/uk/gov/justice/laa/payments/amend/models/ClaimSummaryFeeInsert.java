package uk.gov.justice.laa.payments.amend.models;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record ClaimSummaryFeeInsert(
    String id,
    String claimId,
    Integer adviceTime,
    Integer travelTime,
    Integer waitingTime,
    BigDecimal netCounselCostsAmount,
    String userId)
    implements Insert {

  @Override
  public String table() {
    return "claim_summary_fee";
  }

  @Override
  public List<Object> parameters() {
    return Arrays.asList(
        id, claimId, adviceTime, travelTime, waitingTime, netCounselCostsAmount, userId);
  }
}
