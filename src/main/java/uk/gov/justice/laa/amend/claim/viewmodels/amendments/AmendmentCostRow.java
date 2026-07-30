package uk.gov.justice.laa.amend.claim.viewmodels.amendments;

import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CostClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

public record AmendmentCostRow(
    String key, Object submittedValue, Object calculatedValue, FieldType type, boolean editable) {

  public static AmendmentCostRow from(ClaimFieldRow row) {
    var field = CostClaimDetailsViewField.byKey(row.key());
    return new AmendmentCostRow(
        row.key(), row.submitted(), row.calculated(), field.getType(), field.isEditable());
  }
}
