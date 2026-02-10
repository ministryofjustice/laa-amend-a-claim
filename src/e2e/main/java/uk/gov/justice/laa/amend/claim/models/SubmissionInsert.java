package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record SubmissionInsert(
        String id,
        String bulkSubmissionId,
        String officeAccountNumber,
        String submissionPeriod,
        String areaOfLaw,
        String userId)
        implements Insert {

    @Override
    public String table() {
        return "submission";
    }

    @Override
    public List<Object> parameters() {
        return Arrays.asList(id, bulkSubmissionId, officeAccountNumber, submissionPeriod, areaOfLaw, userId, userId);
    }
}
