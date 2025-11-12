package uk.gov.justice.laa.amend.claim.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidMonetaryValue;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidMonetaryValue
public class MonetaryValueForm {

    private String value;
    private String cost;
}
