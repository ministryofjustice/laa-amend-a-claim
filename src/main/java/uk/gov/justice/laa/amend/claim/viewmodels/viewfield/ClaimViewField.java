package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;

public interface ClaimViewField<T extends Claim> {
  String name();

  <U> ClaimViewFieldAccessor<T, U> getAccessor();

  static <C extends ClaimDetails> LinkedHashMap<ClaimViewField<C>, Object> toFieldMap(
      Stream<ClaimViewField<C>> fields, C claim) {
    LinkedHashMap<ClaimViewField<C>, Object> fieldMap = new LinkedHashMap<>();
    fields.forEach(field -> fieldMap.put(field, field.getAccessor().getter().apply(claim)));
    return fieldMap;
  }

  @SuppressWarnings("unchecked")
  static ClaimViewField<CrimeClaimDetails> asCrimeField(
      ClaimViewField<? super CrimeClaimDetails> field) {
    return (ClaimViewField<CrimeClaimDetails>) field;
  }

  @SuppressWarnings("unchecked")
  static ClaimViewField<CivilClaimDetails> asCivilField(
      ClaimViewField<? super CivilClaimDetails> field) {
    return (ClaimViewField<CivilClaimDetails>) field;
  }

  @SuppressWarnings("unchecked")
  static ClaimViewField<MediationClaimDetails> asMediationField(
      ClaimViewField<? super MediationClaimDetails> field) {
    return (ClaimViewField<MediationClaimDetails>) field;
  }
}
