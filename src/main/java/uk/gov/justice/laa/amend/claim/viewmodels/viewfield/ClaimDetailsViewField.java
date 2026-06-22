package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

@Getter
public enum ClaimDetailsViewField implements ClaimViewField<ClaimDetails> {
  // Common client fields
  INITIAL(new Accessor<>(ClaimDetails::getClientForename)),
  FORENAME(new Accessor<>(ClaimDetails::getClientForename)),
  SURNAME(new Accessor<>(ClaimDetails::getClientSurname)),
  GENDER(new Accessor<>(ClaimDetails::getClientGender)),
  ETHNICITY(new Accessor<>(ClaimDetails::getClientEthnicity)),
  DISABILITY(new Accessor<>(ClaimDetails::getClientDisability));

  private final Accessor<?> accessor;

  ClaimDetailsViewField(Accessor<?> accessor) {
    this.accessor = accessor;
  }

  public record Accessor<T>(Function<ClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<ClaimDetails, T> {}
}
