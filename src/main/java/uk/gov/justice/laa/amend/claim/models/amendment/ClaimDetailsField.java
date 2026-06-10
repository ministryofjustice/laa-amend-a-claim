package uk.gov.justice.laa.amend.claim.models.amendment;

import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

@Getter
public enum ClaimDetailsField implements BaseClaimDetailsField<ClaimDetails> {
  INITIAL(new Accessor<>(ClaimDetails::getClientForename)),
  SURNAME(new Accessor<>(ClaimDetails::getClientSurname)),
  GENDER(new Accessor<>(ClaimDetails::getClientGender)),
  ETHNICITY(new Accessor<>(ClaimDetails::getClientEthnicity)),
  DISABILITY(new Accessor<>(ClaimDetails::getClientDisability));

  private final Accessor<?> accessor;

  ClaimDetailsField(Accessor<?> accessor) {
    this.accessor = accessor;
  }

  public record Accessor<T>(Function<ClaimDetails, T> getter)
      implements ClaimFieldAccessor<ClaimDetails, T> {}
}
