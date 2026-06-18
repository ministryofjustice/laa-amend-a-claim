package uk.gov.justice.laa.amend.claim.views.amendments;

import uk.gov.justice.laa.amend.claim.views.ViewTestBase;

public abstract class AmendmentsBaseTest extends ViewTestBase {

  final String overviewUrl;

  final String clientUrl;
  final String caseUrl;
  final String costsUrl;

  final String amendClientUrl;

  final String checkUrl;

  AmendmentsBaseTest() {
    overviewUrl = "/submissions/%s/claims/%s".formatted(submissionId, claimId);

    clientUrl = "/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
    caseUrl = "/submissions/%s/claims/%s/amendments/case".formatted(submissionId, claimId);
    costsUrl = "/submissions/%s/claims/%s/amendments/costs".formatted(submissionId, claimId);

    amendClientUrl =
        "/submissions/%s/claims/%s/amendments/amend-client".formatted(submissionId, claimId);

    checkUrl = "/submissions/%s/claims/%s/amendments/check".formatted(submissionId, claimId);
  }
}
