package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;

import java.util.List;

@Builder
public record BulkSubmissionInsert(
    String id,
    String userId
) implements Insert {

    @Override
    public String table() {
        return "bulk_submission";
    }

    @Override
    public List<Object> parameters() {
        return List.of(
            id,
            userId,
            userId
        );
    }
}
