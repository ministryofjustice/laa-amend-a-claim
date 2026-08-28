package uk.gov.justice.laa.payments.amend.models.history;

import java.time.OffsetDateTime;

public record ClaimHistoryVoidedEvent(OffsetDateTime eventDateTime, String user)
    implements BaseClaimHistoryEvent {}
