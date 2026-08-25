package uk.gov.justice.laa.payments.amend.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.payments.amend.forms.annotations.ValidMonetaryValue;
import uk.gov.justice.laa.payments.amend.models.enums.Cost;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidMonetaryValue
public class MonetaryValueForm {

  private String value;
  private Cost cost;
}
