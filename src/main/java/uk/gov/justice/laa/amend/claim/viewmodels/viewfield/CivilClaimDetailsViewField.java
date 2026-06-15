package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;

@Getter
public enum CivilClaimDetailsViewField implements ClaimViewField<CivilClaimDetails> {
  // Client fields
  DATE_OF_BIRTH(new Accessor<>(CivilClaimDetails::getClientDateOfBirth)),
  POSTCODE(new Accessor<>(CivilClaimDetails::getClientPostcode)),
  IS_ELIGIBLE_CLIENT(new Accessor<>(CivilClaimDetails::getIsEligibleClient)),
  CLIENT_TYPE(new Accessor<>(CivilClaimDetails::getClientType)),
  UNIQUE_CLIENT_NUMBER(new Accessor<>(CivilClaimDetails::getUniqueClientNumber)),
  HOME_OFFICE_CLIENT_NUMBER(new Accessor<>(CivilClaimDetails::getHomeOfficeClientNumber)),
  IS_POSTAL_APPLICATION_ACCEPTED(new Accessor<>(CivilClaimDetails::getIsPostalApplication));

  private final Accessor<?> accessor;

  CivilClaimDetailsViewField(Accessor<?> accessor) {
    this.accessor = accessor;
  }

  public record Accessor<T>(Function<CivilClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<CivilClaimDetails, T> {}
}
