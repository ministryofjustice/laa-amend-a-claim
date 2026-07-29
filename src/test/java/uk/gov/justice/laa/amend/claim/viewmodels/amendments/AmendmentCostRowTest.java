package uk.gov.justice.laa.amend.claim.viewmodels.amendments;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;

class AmendmentCostRowTest {

  @Test
  void nullBooleanRowIsNotEditable() {
    var row = new ClaimFieldRow(Label.SUBSTANTIVE_HEARING, null, null, null, false, null);

    var costRow = AmendmentCostRow.from(row);

    assertThat(costRow.editable()).isFalse();
    assertThat(costRow.type()).isEqualTo(FieldType.BOOLEAN);
  }

  @Test
  void nullRowIsNotEditable() {
    var row = new ClaimFieldRow(Label.FIXED_FEE, null, null, null, false, null);

    assertThat(AmendmentCostRow.from(row).editable()).isFalse();
  }

  @Test
  void rowWithValueIsEditable() {
    var row =
        new ClaimFieldRow(Label.NET_PROFIT_COST, BigDecimal.valueOf(100), null, null, false, null);

    var costRow = AmendmentCostRow.from(row);

    assertThat(costRow.editable()).isTrue();
    assertThat(costRow.type()).isEqualTo(FieldType.BIG_DECIMAL);
  }
}
