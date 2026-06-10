package uk.gov.justice.laa.amend.claim.models.amendment;

import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;

@Getter
public enum CivilClaimDetailsField implements BaseClaimDetailsField<CivilClaimDetails> {
  DATE_OF_BIRTH(new Accessor<>(CivilClaimDetails::getClientDateOfBirth));

  private final CivilClaimDetailsField.Accessor<?> accessor;

  CivilClaimDetailsField(CivilClaimDetailsField.Accessor<?> accessor) {
    this.accessor = accessor;
  }

  public record Accessor<T>(Function<CivilClaimDetails, T> getter)
      implements ClaimFieldAccessor<CivilClaimDetails, T> {}
}
