package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CLIENT_TYPE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.DATE_OF_BIRTH;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.HOME_OFFICE_CLIENT_NUMBER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.IS_POSTAL_APPLICATION_ACCEPTED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.POSTCODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.UNIQUE_CLIENT_NUMBER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISABILITY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.ETHNICITY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.FORENAME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.GENDER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.SURNAME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.asCivilField;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record CivilClaimClientView(
    LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> client1Rows,
    LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> client2Rows)
    implements ClaimClientView<ClaimViewField<CivilClaimDetails>> {

  public CivilClaimClientView(CivilClaimDetails claim) {
    this(createRows(claim), new LinkedHashMap<>());
  }

  public Class<?> claimDetailsType() {
    return CivilClaimDetails.class;
  }

  private static LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> createRows(
      CivilClaimDetails claim) {
    var fields =
        Stream.of(
            asCivilField(FORENAME),
            asCivilField(SURNAME),
            DATE_OF_BIRTH,
            asCivilField(GENDER),
            asCivilField(ETHNICITY),
            asCivilField(DISABILITY),
            POSTCODE,
            IS_ELIGIBLE_CLIENT,
            CLIENT_TYPE,
            UNIQUE_CLIENT_NUMBER,
            HOME_OFFICE_CLIENT_NUMBER,
            IS_POSTAL_APPLICATION_ACCEPTED);

    return toFieldMap(fields, claim);
  }
}
