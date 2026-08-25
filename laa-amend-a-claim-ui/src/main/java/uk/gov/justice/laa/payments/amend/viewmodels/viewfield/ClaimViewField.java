package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.Claim;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.Amendability;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;

public interface ClaimViewField<T extends Claim> {

  List<String> ROW_LABEL_KEY_PREFIXES =
      List.of("claimCase.rows.", "claimClient.rows.", "claimCosts.rows.", "claimSummary.rows.");

  String name();

  <V> ClaimViewFieldGetter<T, V> getGetter();

  FieldType getFieldType();

  String getClaimsApiFieldName();

  default String getFeeApiFieldName() {
    return null;
  }

  ClaimViewFieldPatcher<?> getPatcher();

  default String label(MessageSource messageSource) {
    var codes =
        ROW_LABEL_KEY_PREFIXES.stream().map(prefix -> prefix + name()).toArray(String[]::new);
    return messageSource.getMessage(new DefaultMessageSourceResolvable(codes), Locale.UK);
  }

  default ClaimPatch.Builder applyPatch(ClaimPatch.Builder patchBuilder, Object value) {
    return getPatcher().apply(patchBuilder, value);
  }

  default Amendability getAmendability() {
    return Amendability.ALWAYS;
  }

  default boolean isEditable(boolean claimIsAssessed) {
    return switch (getAmendability()) {
      case ALWAYS -> true;
      case UNTIL_ASSESSED -> !claimIsAssessed;
      case NEVER -> false;
    };
  }

  default List<FieldOption> getOptions() {
    return List.of();
  }

  static <C extends ClaimDetails> LinkedHashMap<ClaimViewField<C>, Object> toFieldMap(
      Stream<ClaimViewField<C>> fields, C claim) {
    LinkedHashMap<ClaimViewField<C>, Object> fieldMap = new LinkedHashMap<>();
    fields.forEach(field -> fieldMap.put(field, field.getGetter().getter().apply(claim)));
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
