package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

@Getter
public enum CrimeClaimDetailsViewField implements ClaimViewField<CrimeClaimDetails> {
  // Case fields
  // TODO: The remainder of the fields will need to be populated. Just putting one in here for now
  // so that we have the class to work with as there are no unique crime client fields.
  SCHEME_ID(new Accessor<>(CrimeClaimDetails::getSchemeId));

  private final Accessor<?> accessor;

  CrimeClaimDetailsViewField(Accessor<?> accessor) {
    this.accessor = accessor;
  }

  public record Accessor<T>(Function<CrimeClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<CrimeClaimDetails, T> {}
}
