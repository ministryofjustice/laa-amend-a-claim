package uk.gov.justice.laa.payments.amend.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.payments.amend.forms.annotations.ValidAllowedTotal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidAllowedTotal
public class AllowedTotalForm {

  private String allowedTotalVat;
  private String allowedTotalInclVat;
}
