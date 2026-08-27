package uk.gov.justice.laa.payments.amend.views.claimdetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.jsoup.nodes.Document;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.payments.amend.models.ClaimHistorySummary;
import uk.gov.justice.laa.payments.amend.service.ClaimHistoryService;
import uk.gov.justice.laa.payments.amend.views.ViewTestBase;

public abstract class ClaimDetailsBaseTest extends ViewTestBase {

  @MockitoBean protected ClaimHistoryService claimHistoryService;

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

  protected void assertRowsHaveAmendedTags(Document doc, String cardTitle, String... rowLabels) {
    for (String rowLabel : rowLabels) {
      assertSummaryListRowHasAmendedTag(getSummaryListRowInCard(doc, cardTitle, rowLabel));
    }
  }

  protected void mockClaimHistorySummary(String... fields) {
    when(claimHistoryService.getClaimHistorySummary(any()))
        .thenReturn(ClaimHistorySummary.builder().amendedFields(Set.of(fields)).build());
  }
}
