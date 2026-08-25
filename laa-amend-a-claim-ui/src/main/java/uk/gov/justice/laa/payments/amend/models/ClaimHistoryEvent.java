package uk.gov.justice.laa.payments.amend.models;

import java.time.OffsetDateTime;
import java.util.Optional;
import uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;

public record ClaimHistoryEvent(
    ClaimHistoryEventType type,
    OffsetDateTime eventDateTime,
    String user,
    Optional<OutcomeType> outcomeType) {}
