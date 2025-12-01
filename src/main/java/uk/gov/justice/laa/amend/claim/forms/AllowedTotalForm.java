package uk.gov.justice.laa.amend.claim.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidAllowedTotal;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidAllowedTotal
public class AllowedTotalForm {

    private String allowedTotalVat;
    private String allowedTotalInclVat;
}
