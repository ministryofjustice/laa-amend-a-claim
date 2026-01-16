package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;

import java.util.List;

@Builder
public record ClaimSummaryFeeInsert(
    String id,
    String claimId,
    String userId
) implements Insert {

    @Override
    public String table() {
        return "claim_summary_fee";
    }

    @Override
    public List<Object> parameters() {
        return List.of(
            id,
            claimId,
            userId
        );
    }
}
