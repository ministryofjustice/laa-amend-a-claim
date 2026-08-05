package uk.gov.justice.laa.amend.claim.models;

import uk.gov.justice.laa.amend.claim.models.enums.ClaimHistoryEventType;
import uk.gov.justice.laa.amend.claim.models.enums.OutcomeType;

import java.time.OffsetDateTime;
import java.util.Optional;

public record ClaimHistoryEvent(
    ClaimHistoryEventType type,
    OffsetDateTime eventDateTime,
    String user,
    Optional<OutcomeType> outcomeType) {}
