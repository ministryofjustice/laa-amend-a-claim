package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;

public record ClaimViewFieldPatcher<T>(
    Class<T> patchType, BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher) {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public ClaimPatch.Builder apply(ClaimPatch.Builder patchBuilder, Object value) {
    var converted = convertValue(value);
    if (converted == null) {
      return patcher.apply(patchBuilder, null);
    }
    return patcher.apply(patchBuilder, patchType.cast(converted));
  }

  // In some cases ClaimPatch has a different type to that used in the amend app, so these fields
  // need converting first.
  private Object convertValue(Object value) {
    if (value == null) {
      return null;
    }
    if (patchType == String.class && value instanceof LocalDate date) {
      return formatDateValue(date);
    }
    return value;
  }

  private static String formatDateValue(LocalDate value) {
    return value.format(DATE_FORMATTER);
  }
}
