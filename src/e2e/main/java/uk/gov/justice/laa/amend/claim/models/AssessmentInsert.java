package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record AssessmentInsert(String id, String claimSummaryFeeId, String claimId, String userId) implements Insert {

    @Override
    public String table() {
        return "assessment";
    }

    @Override
    public List<Object> parameters() {
        return Arrays.asList(id, claimSummaryFeeId, claimId, userId, userId);
    }
}
