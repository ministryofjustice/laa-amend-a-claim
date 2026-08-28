package uk.gov.justice.laa.payments.amend.models.history;

import java.time.OffsetDateTime;
import java.util.List;

public record ClaimHistoryAmendedEvent(
    OffsetDateTime eventDateTime,
    String user,
    List<ClaimHistoryAmendmentChange> amendmentChanges,
    String requestedByCode,
    String amendmentReasonCode)
    implements BaseClaimHistoryEvent {}
