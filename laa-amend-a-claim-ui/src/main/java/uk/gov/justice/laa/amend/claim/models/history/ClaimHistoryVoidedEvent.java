package uk.gov.justice.laa.amend.claim.models.history;

import java.time.OffsetDateTime;

public record ClaimHistoryVoidedEvent(OffsetDateTime eventDateTime, String user)
    implements BaseClaimHistoryEvent {}
