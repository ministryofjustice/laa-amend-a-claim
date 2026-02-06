package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record CalculatedFeeDetailInsert(
        String id, String claimSummaryFeeId, String claimId, String feeCode, boolean escaped, String userId)
        implements Insert {

    @Override
    public String table() {
        return "calculated_fee_detail";
    }

    @Override
    public List<Object> parameters() {
        return Arrays.asList(id, claimSummaryFeeId, claimId, feeCode, escaped, userId);
    }
}
