package uk.gov.justice.laa.amend.claim.forms.amendment;

import static uk.gov.justice.laa.amend.claim.forms.amendment.AmendForm.viewRowsToFormInputs;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

@Getter
@Setter
public class CrimeAmendForm implements AmendForm {

  private Map<String, String> inputs;

  public CrimeAmendForm() {
    this.inputs = new LinkedHashMap<>();
  }

  public CrimeAmendForm(LinkedHashMap<? extends ClaimViewField<?>, Object> viewRows) {
    this.inputs = viewRowsToFormInputs(viewRows);
  }

  public ClaimViewField<?> getField(String fieldName) {
    try {
      return CrimeClaimDetailsViewField.valueOf(fieldName);
    } catch (IllegalArgumentException e) {
      return ClaimDetailsViewField.valueOf(fieldName);
    }
  }
}
