package uk.gov.justice.laa.payments.amend.models.history;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record ClaimHistoryFspEvent(
    OffsetDateTime eventDateTime,
    String user,
    BigDecimal totalBefore,
    BigDecimal totalAfter,
    List<ClaimHistoryAmendmentChange> recalculatedChanges)
    implements BaseClaimHistoryEvent {}
