package uk.gov.justice.laa.payments.amend.models.history;

import java.time.OffsetDateTime;

public record ClaimHistoryCreatedEvent(OffsetDateTime eventDateTime, String user, boolean escaped)
    implements BaseClaimHistoryEvent {}
