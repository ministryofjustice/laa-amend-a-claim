package uk.gov.justice.laa.amend.claim.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidAssessedTotal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidAssessedTotal
public class AssessedTotalForm {

    private String assessedTotalVat;
    private String assessedTotalInclVat;
}
