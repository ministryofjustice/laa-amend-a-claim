package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch;

public record ClaimViewFieldPatcher<T>(
    Class<T> patchType,
    BiFunction<ClaimAmendmentPatch.Builder, T, ClaimAmendmentPatch.Builder> patcher) {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public ClaimAmendmentPatch.Builder apply(ClaimAmendmentPatch.Builder patchBuilder, Object value) {
    var converted = convertValue(value);
    if (converted == null) {
      return patcher.apply(patchBuilder, null);
    }
    return patcher.apply(patchBuilder, patchType.cast(converted));
  }

  // In some cases ClaimAmendmentPatch has a different type to that used in the amend app, so these
  // fields need converting first.
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
