package uk.gov.justice.laa.amend.claim.viewmodels.amendments;

import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CostClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public record AmendmentCostRow(
    String key, Object submittedValue, Object calculatedValue, FieldType type, boolean editable) {

  public static AmendmentCostRow from(ClaimFieldRow row) {
    return new AmendmentCostRow(
        row.key(),
        row.submitted(),
        row.calculated(),
        CostClaimDetailsViewField.byKey(row.key()).getType(),
        isEditable(row.submitted()));
  }

  private static boolean isEditable(Object submitted) {
    return submitted != null;
  }
}
