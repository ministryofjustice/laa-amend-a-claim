package uk.gov.justice.laa.amend.claim.views.claimdetails;

import uk.gov.justice.laa.amend.claim.views.ViewTestBase;

public abstract class ClaimDetailsBaseTest extends ViewTestBase {

  final String overviewUrl;
  final String clientUrl;
  final String caseUrl;
  final String costsUrl;
  final String historyUrl;

  ClaimDetailsBaseTest() {
    overviewUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
    clientUrl = String.format("/submissions/%s/claims/%s/client", submissionId, claimId);
    caseUrl = String.format("/submissions/%s/claims/%s/case", submissionId, claimId);
    costsUrl = String.format("/submissions/%s/claims/%s/costs", submissionId, claimId);
    historyUrl = String.format("/submissions/%s/claims/%s/history", submissionId, claimId);
  }
}
