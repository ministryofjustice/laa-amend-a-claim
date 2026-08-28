package uk.gov.justice.laa.payments.amend.models.history;

import java.time.OffsetDateTime;

public interface BaseClaimHistoryEvent {
  OffsetDateTime eventDateTime();

  String user();
}
