package uk.gov.justice.laa.amend.claim.forms.amendment;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public interface AmendForm extends Serializable {

  @Serial long serialVersionUID = 1L;

  Map<String, String> getInputs();

  void setInputs(Map<String, String> inputs);

  ClaimViewField<?> getField(String fieldName);

  default Map<ClaimViewField<?>, String> getFieldInputs() {
    return getInputs().entrySet().stream()
        .collect(Collectors.toMap(entry -> getField(entry.getKey()), Entry::getValue));
  }

  static Map<String, String> viewRowsToFormInputs(
      LinkedHashMap<? extends ClaimViewField<?>, Object> viewRows) {
    return viewRows.entrySet().stream()
        .collect(
            Collectors.toMap(
                entry -> String.valueOf(entry.getKey().name()),
                entry -> formatValue(entry.getValue()),
                (a, b) -> b,
                LinkedHashMap::new));
  }

  static String formatValue(Object value) {
    if (value == null) {
      return "";
    }

    if (value instanceof String stringValue) {
      return stringValue;
    }

    // TODO: Once we have handled all possible types of renderable values we can remove this default
    // and throw an exception instead
    return "TODO";
  }

  default boolean isAmendment(ClaimViewField<?> key, Object originalValue) {
    if (!getInputs().containsKey(key.name())) {
      return false;
    }

    // TODO: Once we have handled all possible types of renderable values we can remove this check.
    // For now don't show any amendments for unsupported field types
    if (!(originalValue instanceof String)) {
      return false;
    }

    var originalFormatted = formatValue(originalValue);
    var currentValue = getInputs().get(key.name());

    return !originalFormatted.equals(currentValue);
  }

  default boolean hasAmendments(LinkedHashMap<? extends ClaimViewField<?>, Object> viewRows) {
    return viewRows.entrySet().stream()
        .anyMatch(entry -> isAmendment(entry.getKey(), entry.getValue()));
  }
}
