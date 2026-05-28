package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import java.util.List;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

public interface ClaimCostsView {

  List<ClaimFieldRow> rows();

  static boolean hasAssessment(ClaimDetails claim) {
    return claim.isHasAssessment() && claim.getLastAssessment() != null;
  }
}
