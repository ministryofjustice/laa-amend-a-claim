package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public interface ClaimCostsView {

  LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> costFields();

  default LinkedHashMap<ClaimViewField<?>, Object> costRows() {
    var costRows = new LinkedHashMap<ClaimViewField<?>, Object>();
    costFields().forEach((field, row) -> costRows.put(field, row.submitted()));
    return costRows;
  }

  static <C extends ClaimDetails> void putField(
      LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> costFields,
      ClaimViewField<C> field,
      C claim) {
    var claimField = (ClaimField) field.getGetter().getter().apply(claim);
    costFields.put(field, claimField.toCustomClaimFieldRow());
  }

  static boolean hasAssessment(ClaimDetails claim) {
    return claim.isHasAssessment() && claim.getLastAssessment() != null;
  }
}
