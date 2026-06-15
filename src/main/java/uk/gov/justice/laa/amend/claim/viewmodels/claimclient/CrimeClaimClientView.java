package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISABILITY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.ETHNICITY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.GENDER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.INITIAL;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.SURNAME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.asCrimeField;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record CrimeClaimClientView(
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> client1Rows,
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> client2Rows)
    implements ClaimClientView<ClaimViewField<CrimeClaimDetails>> {

  public CrimeClaimClientView(CrimeClaimDetails claim) {
    this(createRows(claim), new LinkedHashMap<>());
  }

  public Class<?> claimDetailsType() {
    return CrimeClaimDetails.class;
  }

  private static LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> createRows(
      CrimeClaimDetails claim) {
    var fields =
        Stream.of(
            asCrimeField(INITIAL),
            asCrimeField(SURNAME),
            asCrimeField(GENDER),
            asCrimeField(ETHNICITY),
            asCrimeField(DISABILITY));

    return toFieldMap(fields, claim);
  }
}
