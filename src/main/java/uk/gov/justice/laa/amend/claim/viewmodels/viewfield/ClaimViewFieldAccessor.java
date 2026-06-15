package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.function.Function;

public interface ClaimViewFieldAccessor<T, U> {
  Function<T, U> getter();
}
