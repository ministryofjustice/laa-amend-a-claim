package uk.gov.justice.laa.amend.claim.viewmodels.claimoverview;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public interface ClaimOverviewView {

  LinkedHashMap<? extends ClaimViewField<?>, Object> summaryRows();

  LinkedHashMap<? extends ClaimViewField<?>, ClaimFieldRow> summaryClaimFieldRows();

  List<ClaimFieldRow> assessedTotals();

  List<ClaimFieldRow> allowedTotals();

  boolean hasAssessment();

  static boolean hasAssessment(ClaimDetails claim) {
    return claim.isHasAssessment() && claim.getLastAssessment() != null;
  }

  static List<ClaimFieldRow> createAssessedTotals(ClaimDetails claim) {
    return toClaimFieldRows(claim.getAssessedTotalFields());
  }

  static List<ClaimFieldRow> createAllowedTotals(ClaimDetails claim) {
    return toClaimFieldRows(claim.getAllowedTotalFields());
  }

  static <C extends ClaimDetails> void putField(
      LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> summaryFields,
      ClaimViewField<C> field,
      C claim) {
    var row = toClaimFieldRow((ClaimField) field.getGetter().getter().apply(claim));
    if (row != null) {
      summaryFields.put(field, row);
    }
  }

  private static List<ClaimFieldRow> toClaimFieldRows(Stream<ClaimField> claimFields) {
    return claimFields
        .filter(Objects::nonNull)
        .map(ClaimOverviewView::toClaimFieldRow)
        .filter(Objects::nonNull)
        .toList();
  }

  private static ClaimFieldRow toClaimFieldRow(ClaimField claimField) {
    return claimField == null ? null : claimField.toClaimFieldRow();
  }
}
