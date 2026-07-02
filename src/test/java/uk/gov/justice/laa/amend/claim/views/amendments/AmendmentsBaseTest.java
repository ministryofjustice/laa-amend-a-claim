package uk.gov.justice.laa.amend.claim.views.amendments;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import uk.gov.justice.laa.amend.claim.views.ViewTestBase;

public abstract class AmendmentsBaseTest extends ViewTestBase {

  final String overviewUrl;
  final String overviewCaseUrl;

  final String clientUrl;
  final String caseUrl;
  final String costsUrl;

  final String amendClientUrl;
  final String amendCaseTypeUrl;
  final String amendCaseDetailsUrl;

  final String checkUrl;

  final DateTimeFormatter testFormatter =
      new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy").toFormatter();

  AmendmentsBaseTest() {
    overviewUrl = "/submissions/%s/claims/%s".formatted(submissionId, claimId);
    overviewCaseUrl = "/submissions/%s/claims/%s/case".formatted(submissionId, claimId);

    clientUrl = "/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
    caseUrl = "/submissions/%s/claims/%s/amendments/case".formatted(submissionId, claimId);
    costsUrl = "/submissions/%s/claims/%s/amendments/costs".formatted(submissionId, claimId);

    amendClientUrl =
        "/submissions/%s/claims/%s/amendments/amend-client".formatted(submissionId, claimId);
    amendCaseTypeUrl =
        "/submissions/%s/claims/%s/amendments/amend-fee-code".formatted(submissionId, claimId);
    amendCaseDetailsUrl =
        "/submissions/%s/claims/%s/amendments/amend-case-details".formatted(submissionId, claimId);

    checkUrl = "/submissions/%s/claims/%s/amendments/check".formatted(submissionId, claimId);
  }
}
