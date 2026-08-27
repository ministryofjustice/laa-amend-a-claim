package uk.gov.justice.laa.amend.claim.models.history;

import java.time.OffsetDateTime;
import java.util.List;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

public record ClaimHistory(
    List<BaseClaimHistoryEvent> events,
    MicrosoftApiUser lastUpdatedUser,
    OffsetDateTime lastUpdatedDateTime) {}
