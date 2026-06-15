package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;

@Getter
public enum MediationClaimDetailsViewField implements ClaimViewField<MediationClaimDetails> {

  // Client fields
  DATE_OF_BIRTH(new Accessor<>(MediationClaimDetails::getClientDateOfBirth)),
  POSTCODE(new Accessor<>(MediationClaimDetails::getClientPostcode)),
  UNIQUE_CLIENT_NUMBER(new Accessor<>(MediationClaimDetails::getUniqueClientNumber)),
  IS_LEGALLY_AIDED(new Accessor<>(MediationClaimDetails::getIsClientLegallyAided)),
  IS_POSTAL_APPLICATION_ACCEPTED(
      new Accessor<>(MediationClaimDetails::getIsClientPostalApplicationAccepted)),

  // Client 2 fields
  CLIENT_2_FORENAME(new Accessor<>(MediationClaimDetails::getClient2Forename)),
  CLIENT_2_SURNAME(new Accessor<>(MediationClaimDetails::getClient2Surname)),
  CLIENT_2_DATE_OF_BIRTH(new Accessor<>(MediationClaimDetails::getClient2DateOfBirth)),
  CLIENT_2_UCN(new Accessor<>(MediationClaimDetails::getClient2Ucn)),
  CLIENT_2_POSTCODE(new Accessor<>(MediationClaimDetails::getClient2Postcode)),
  CLIENT_2_GENDER(new Accessor<>(MediationClaimDetails::getClient2Gender)),
  CLIENT_2_ETHNICITY(new Accessor<>(MediationClaimDetails::getClient2Ethnicity)),
  CLIENT_2_DISABILITY(new Accessor<>(MediationClaimDetails::getClient2Disability)),
  IS_CLIENT_2_LEGALLY_AIDED(new Accessor<>(MediationClaimDetails::getIsClient2LegallyAided)),
  IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED(
      new Accessor<>(MediationClaimDetails::getIsClient2PostalApplicationAccepted));

  private final Accessor<?> accessor;

  MediationClaimDetailsViewField(Accessor<?> accessor) {
    this.accessor = accessor;
  }

  public record Accessor<T>(Function<MediationClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<MediationClaimDetails, T> {}
}
