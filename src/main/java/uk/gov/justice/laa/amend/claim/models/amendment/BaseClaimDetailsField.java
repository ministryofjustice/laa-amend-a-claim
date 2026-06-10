package uk.gov.justice.laa.amend.claim.models.amendment;

import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

public interface BaseClaimDetailsField<T extends ClaimDetails> {
  String name();

  <U> ClaimFieldAccessor<T, U> getAccessor();
}
