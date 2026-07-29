package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CostClaimDetailsViewField;

class ClaimCostsViewTest {

  @Test
  void costRowsSeedsNullBoltOnAsNullInput() {
    ClaimCostsView view =
        () ->
            List.of(
                new ClaimFieldRow(Label.SUBSTANTIVE_HEARING, null, null, null, false, null),
                new ClaimFieldRow(
                    Label.NET_PROFIT_COST, BigDecimal.valueOf(100), null, null, false, null));

    var costRows = view.costRows();

    assertThat(costRows).containsEntry(CostClaimDetailsViewField.SUBSTANTIVE_HEARING, null);

    var form = new AmendmentForm(costRows);
    assertThat(form.getInputs())
        .containsEntry("substantiveHearing", null)
        .containsEntry("profitCost", "100.00");
  }
}
