package uk.gov.justice.laa.amend.claim.forms.amendment;

import static uk.gov.justice.laa.amend.claim.forms.amendment.AmendForm.viewRowsToFormInputs;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@Getter
@Setter
public class CivilAmendForm implements AmendForm {

  private Map<String, String> inputs;

  public CivilAmendForm() {
    this.inputs = new LinkedHashMap<>();
  }

  public CivilAmendForm(LinkedHashMap<? extends ClaimViewField<?>, Object> viewRows) {
    this.inputs = viewRowsToFormInputs(viewRows);
  }

  public ClaimViewField<?> getField(String fieldName) {
    try {
      return CivilClaimDetailsViewField.valueOf(fieldName);
    } catch (IllegalArgumentException e) {
      return ClaimDetailsViewField.valueOf(fieldName);
    }
  }
}
