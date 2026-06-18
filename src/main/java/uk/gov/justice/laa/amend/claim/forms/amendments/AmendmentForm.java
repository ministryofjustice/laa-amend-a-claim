package uk.gov.justice.laa.amend.claim.forms.amendments;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Data;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

@Data
public class AmendmentForm {

  private Map<String, String> inputs;

  public AmendmentForm() {
    this.inputs = new HashMap<>();
  }

  public AmendmentForm(AmendmentForm form) {
    this.inputs = new LinkedHashMap<>(form.getInputs());
  }

  public AmendmentForm(LinkedHashMap<? extends ClaimViewField<?>, Object> viewRows) {
    var inputs = new HashMap<String, String>();
    for (var entry : viewRows.entrySet()) {
      inputs.put(entry.getKey().name(), formatValue(entry.getValue()));
    }
    this.inputs = inputs;
  }

  public Map<ClaimViewField<?>, String> getFieldInputs(Class<?> claimDetailsType) {
    return inputs.entrySet().stream()
        .collect(
            Collectors.toMap(entry -> getField(entry.getKey(), claimDetailsType), Entry::getValue));
  }

  public boolean isAmendment(String key, AmendmentForm originalForm) {
    var original = originalForm.getInputs().get(key);
    var current = inputs.get(key);

    if (original == null && current == null) {
      return false;
    }

    if (original == null || current == null) {
      return true;
    }

    return !originalForm.getInputs().get(key).equals(getInputs().get(key));
  }

  public boolean hasAmendments(AmendmentForm original) {
    return getInputs().keySet().stream().anyMatch(key -> isAmendment(key, original));
  }

  private static String formatValue(Object value) {
    if (value == null) {
      return null;
    }

    if (value instanceof String stringValue) {
      return stringValue;
    }

    // TODO: Once we have handled all possible types of renderable values we can remove this default
    // and throw an exception instead
    return "TODO";
  }

  private static ClaimViewField<?> getField(String fieldName, Class<?> claimDetailsType) {
    if (claimDetailsType == CrimeClaimDetailsViewField.class) {
      return getCrimeField(fieldName);
    } else if (claimDetailsType == CivilClaimDetailsViewField.class) {
      return getCivilField(fieldName);
    } else if (claimDetailsType == MediationClaimDetailsViewField.class) {
      return getMediationField(fieldName);
    }
    throw new IllegalArgumentException("Unsupported claim details type");
  }

  private static ClaimViewField<?> getCrimeField(String fieldName) {
    try {
      return CrimeClaimDetailsViewField.valueOf(fieldName);
    } catch (IllegalArgumentException e) {
      return ClaimDetailsViewField.valueOf(fieldName);
    }
  }

  private static ClaimViewField<?> getCivilField(String fieldName) {
    try {
      return CivilClaimDetailsViewField.valueOf(fieldName);
    } catch (IllegalArgumentException e) {
      return ClaimDetailsViewField.valueOf(fieldName);
    }
  }

  private static ClaimViewField<?> getMediationField(String fieldName) {
    try {
      return MediationClaimDetailsViewField.valueOf(fieldName);
    } catch (IllegalArgumentException e) {
      return ClaimDetailsViewField.valueOf(fieldName);
    }
  }
}
