package uk.gov.justice.laa.amend.claim.models.history;

import java.time.OffsetDateTime;

public interface BaseClaimHistoryEvent {
  OffsetDateTime eventDateTime();

  String user();
}
