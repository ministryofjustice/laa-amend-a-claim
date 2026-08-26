package uk.gov.justice.laa.payments.amend.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.payments.amend.forms.annotations.ValidAssessedTotal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidAssessedTotal
public class AssessedTotalForm {

  private String assessedTotalVat;
  private String assessedTotalInclVat;
}
