package uk.gov.justice.laa.payments.amend.models.history;

import java.time.OffsetDateTime;
import java.util.List;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;

public record ClaimHistory(
    List<BaseClaimHistoryEvent> events,
    MicrosoftApiUser lastUpdatedUser,
    OffsetDateTime lastUpdatedDateTime) {}
