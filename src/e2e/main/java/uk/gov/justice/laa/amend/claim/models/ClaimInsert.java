package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record ClaimInsert(String id, String submissionId, String uniqueFileNumber, String userId, Boolean hasAssessment)
        implements Insert {

    @Override
    public String table() {
        return "claim";
    }

    @Override
    public List<Object> parameters() {
        return Arrays.asList(
                id, submissionId, uniqueFileNumber, userId, userId, hasAssessment != null ? hasAssessment : false);
    }
}
