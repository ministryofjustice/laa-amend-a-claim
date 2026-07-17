package uk.gov.justice.laa.amend.claim.views.amendments;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import uk.gov.justice.laa.amend.claim.views.ViewTestBase;

public abstract class AmendmentsBaseTest extends ViewTestBase {

  final String overviewUrl;
  final String overviewCaseUrl;

  final String clientUrl;
  final String caseUrl;
  final String costsUrl;

  final String amendClientUrl;
  final String amendFeeCodeUrl;
  final String amendMatterTypeCodeUrl;
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
    amendFeeCodeUrl =
        "/submissions/%s/claims/%s/amendments/amend-fee-code".formatted(submissionId, claimId);
    amendMatterTypeCodeUrl =
        "/submissions/%s/claims/%s/amendments/amend-matter-type".formatted(submissionId, claimId);
    amendCaseDetailsUrl =
        "/submissions/%s/claims/%s/amendments/amend-case-details".formatted(submissionId, claimId);

    checkUrl = "/submissions/%s/claims/%s/amendments/check".formatted(submissionId, claimId);
  }

  protected void assertBooleanSelectRow(
      List<Element> row, String label, String currentValue, String inputId, boolean expectedValue) {
    assertCellContainsText(row.getFirst(), label);
    assertCellContainsText(row.get(1), currentValue);

    Element select = selectFirst(row.get(2), "select.govuk-select");
    Assertions.assertEquals(inputId, select.attr("id"), "Boolean select id");
    Assertions.assertEquals(2, select.select("option[value=true], option[value=false]").size());
    Element selectLabel = selectFirst(row.get(2), "label[for=%s]".formatted(inputId));
    Assertions.assertEquals(label, selectLabel.text());

    Element selectedOption = selectFirst(select, "option[selected]");
    Assertions.assertEquals(Boolean.toString(expectedValue), selectedOption.attr("value"));
    Assertions.assertEquals(expectedValue ? "Yes" : "No", selectedOption.text());
  }
}
