package uk.gov.justice.laa.amend.claim.models;

import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record ClaimHistorySummary(
    MicrosoftApiUser lastUpdatedUser,
    OffsetDateTime lastUpdatedDateTime,
    Set<String> amendedFields) {

  public static ClaimHistorySummary empty() {
    return ClaimHistorySummary.builder().amendedFields(Set.of()).build();
  }
}
