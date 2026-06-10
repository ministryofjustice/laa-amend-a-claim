package uk.gov.justice.laa.amend.claim.models.amendment;

import java.util.function.Function;

public interface ClaimFieldAccessor<T, U> {
  Function<T, U> getter();
}
