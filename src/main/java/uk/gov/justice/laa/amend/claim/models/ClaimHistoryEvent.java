package uk.gov.justice.laa.amend.claim.models;

import java.time.OffsetDateTime;
import java.util.Optional;

public record ClaimHistoryEvent(
    ClaimHistoryEventType type,
    OffsetDateTime eventDateTime,
    String user,
    Optional<OutcomeType> outcomeType) {}
