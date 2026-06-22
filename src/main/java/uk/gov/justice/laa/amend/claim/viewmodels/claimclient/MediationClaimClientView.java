package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISABILITY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.ETHNICITY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.FORENAME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.GENDER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.SURNAME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.asMediationField;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLIENT_2_DATE_OF_BIRTH;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLIENT_2_DISABILITY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLIENT_2_ETHNICITY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLIENT_2_FORENAME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLIENT_2_GENDER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLIENT_2_POSTCODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLIENT_2_SURNAME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLIENT_2_UCN;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.DATE_OF_BIRTH;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.IS_CLIENT_2_LEGALLY_AIDED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.IS_LEGALLY_AIDED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.IS_POSTAL_APPLICATION_ACCEPTED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.POSTCODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.UNIQUE_CLIENT_NUMBER;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record MediationClaimClientView(
    LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> client1Rows,
    LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> client2Rows)
    implements ClaimClientView<ClaimViewField<MediationClaimDetails>> {

  public MediationClaimClientView(MediationClaimDetails claim) {
    this(createClient1Rows(claim), createClient2Rows(claim));
  }

  public Class<?> claimDetailsType() {
    return MediationClaimDetails.class;
  }

  private static LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> createClient1Rows(
      MediationClaimDetails claim) {
    var fields =
        Stream.of(
            asMediationField(FORENAME),
            asMediationField(SURNAME),
            DATE_OF_BIRTH,
            UNIQUE_CLIENT_NUMBER,
            POSTCODE,
            asMediationField(GENDER),
            asMediationField(ETHNICITY),
            asMediationField(DISABILITY),
            IS_LEGALLY_AIDED,
            IS_POSTAL_APPLICATION_ACCEPTED);

    return toFieldMap(fields, claim);
  }

  private static LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> createClient2Rows(
      MediationClaimDetails claim) {
    Stream<ClaimViewField<MediationClaimDetails>> fields =
        Stream.of(
            CLIENT_2_FORENAME,
            CLIENT_2_SURNAME,
            CLIENT_2_DATE_OF_BIRTH,
            CLIENT_2_UCN,
            CLIENT_2_POSTCODE,
            CLIENT_2_GENDER,
            CLIENT_2_ETHNICITY,
            CLIENT_2_DISABILITY,
            IS_CLIENT_2_LEGALLY_AIDED,
            IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED);

    return toFieldMap(fields, claim);
  }
}
