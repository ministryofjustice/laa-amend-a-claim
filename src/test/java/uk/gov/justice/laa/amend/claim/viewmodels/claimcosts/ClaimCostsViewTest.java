package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.PROFIT_COST;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

class ClaimCostsViewTest {

  @Test
  void costRowsSeedsNullBoltOnAsNullInput() {
    var costFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    costFields.put(
        SUBSTANTIVE_HEARING,
        new ClaimFieldRow(Label.SUBSTANTIVE_HEARING, null, null, null, false, null));
    costFields.put(
        PROFIT_COST,
        new ClaimFieldRow(Label.NET_PROFIT_COST, BigDecimal.valueOf(100), null, null, false, null));
    ClaimCostsView view = () -> costFields;

    var costRows = view.costRows();

    assertThat(costRows).containsEntry(SUBSTANTIVE_HEARING, null);

    var form = new AmendmentForm(costRows);
    assertThat(form.getInputs())
        .containsEntry("SUBSTANTIVE_HEARING", null)
        .containsEntry("PROFIT_COST", "100.00");
  }
}
