package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import java.util.LinkedHashMap;
import java.util.List;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CostClaimDetailsViewField;

public interface ClaimCostsView {

  List<ClaimFieldRow> rows();

  default LinkedHashMap<ClaimViewField<ClaimDetails>, Object> costRows() {
    var costRows = new LinkedHashMap<ClaimViewField<ClaimDetails>, Object>();
    rows()
        .forEach(row -> costRows.put(CostClaimDetailsViewField.byKey(row.key()), row.submitted()));
    return costRows;
  }

  static boolean hasAssessment(ClaimDetails claim) {
    return claim.isHasAssessment() && claim.getLastAssessment() != null;
  }
}
