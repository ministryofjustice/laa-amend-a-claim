package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;

import java.util.List;

@Builder
public record CalculatedFeeDetailInsert(
    String id,
    String claimSummaryFeeId,
    String claimId,
    String userId
) implements Insert {

    @Override
    public String table() {
        return "calculated_fee_detail";
    }

    @Override
    public List<Object> parameters() {
        return List.of(
            id,
            claimSummaryFeeId,
            claimId,
            userId
        );
    }
}
