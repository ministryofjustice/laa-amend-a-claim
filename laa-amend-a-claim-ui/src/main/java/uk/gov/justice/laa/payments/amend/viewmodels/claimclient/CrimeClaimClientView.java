package uk.gov.justice.laa.payments.amend.viewmodels.claimclient;

import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.DISABILITY;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.ETHNICITY;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.GENDER;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.SURNAME;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField.asCrimeField;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField.toFieldMap;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField.INITIAL;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public record CrimeClaimClientView(
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> client1Rows,
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> client2Rows)
    implements ClaimClientView<ClaimViewField<CrimeClaimDetails>> {

  public CrimeClaimClientView(CrimeClaimDetails claim) {
    this(createRows(claim), new LinkedHashMap<>());
  }

  private static LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> createRows(
      CrimeClaimDetails claim) {
    var fields =
        Stream.of(
            INITIAL,
            asCrimeField(SURNAME),
            asCrimeField(GENDER),
            asCrimeField(ETHNICITY),
            asCrimeField(DISABILITY));

    return toFieldMap(fields, claim);
  }
}
