package uk.gov.justice.laa.amend.claim.forms.amendments;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.FieldType;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

@Data
public class AmendmentForm {

  private static final String DAY_SUFFIX = "-day";
  private static final String MONTH_SUFFIX = "-month";
  private static final String YEAR_SUFFIX = "-year";
  private static final List<String> DATE_SUFFIXES = List.of(DAY_SUFFIX, MONTH_SUFFIX, YEAR_SUFFIX);

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
      var field = entry.getKey();
      if (field.getType() == FieldType.DATE) {
        putDateInputs(inputs, field.name(), entry.getValue());
      } else {
        inputs.put(field.name(), formatValue(entry.getValue()));
      }
    }
    this.inputs = inputs;
  }

  public Map<ClaimViewField<?>, String> getFieldInputs(Class<?> claimDetailsType) {
    var fieldInputs = new LinkedHashMap<ClaimViewField<?>, String>();
    var processedDateFields = new HashSet<String>();

    for (var entry : inputs.entrySet()) {
      var dateFieldName = dateFieldNameOrNull(entry.getKey());
      if (dateFieldName != null) {
        if (processedDateFields.add(dateFieldName)) {
          var date = getDateValue(dateFieldName);
          fieldInputs.put(
              getField(dateFieldName, claimDetailsType), date == null ? null : date.toString());
        }
      } else {
        fieldInputs.put(getField(entry.getKey(), claimDetailsType), entry.getValue());
      }
    }

    return fieldInputs;
  }

  private static String dateFieldNameOrNull(String key) {
    for (var suffix : DATE_SUFFIXES) {
      if (key.endsWith(suffix)) {
        return key.substring(0, key.length() - suffix.length());
      }
    }
    return null;
  }

  public boolean isAmendment(String key, AmendmentForm originalForm) {
    if (isDateField(key)) {
      return !Objects.equals(originalForm.getDateValue(key), getDateValue(key));
    }

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
    return getInputs().keySet().stream()
        .map(
            key -> {
              var dateField = dateFieldNameOrNull(key);
              return dateField != null ? dateField : key;
            })
        .distinct()
        .anyMatch(key -> isAmendment(key, original));
  }

  public boolean isDateField(String fieldName) {
    return inputs.containsKey(fieldName + DAY_SUFFIX)
        || inputs.containsKey(fieldName + MONTH_SUFFIX)
        || inputs.containsKey(fieldName + YEAR_SUFFIX);
  }

  public Object getAmendedValue(String fieldName) {
    return isDateField(fieldName) ? getDateValue(fieldName) : inputs.get(fieldName);
  }

  public LocalDate getDateValue(String fieldName) {
    var day = inputs.get(fieldName + DAY_SUFFIX);
    var month = inputs.get(fieldName + MONTH_SUFFIX);
    var year = inputs.get(fieldName + YEAR_SUFFIX);

    if (isBlank(day) || isBlank(month) || isBlank(year)) {
      return null;
    }

    try {
      return LocalDate.of(
          Integer.parseInt(year.trim()),
          Integer.parseInt(month.trim()),
          Integer.parseInt(day.trim()));
    } catch (NumberFormatException | DateTimeException e) {
      return null;
    }
  }

  private static void putDateInputs(Map<String, String> inputs, String name, Object value) {
    if (value == null) {
      inputs.put(name + DAY_SUFFIX, "");
      inputs.put(name + MONTH_SUFFIX, "");
      inputs.put(name + YEAR_SUFFIX, "");
      return;
    }

    if (!(value instanceof LocalDate date)) {
      throw new IllegalArgumentException(
          "Expected LocalDate for date field '%s' but got %s".formatted(name, value.getClass()));
    }

    inputs.put(name + DAY_SUFFIX, String.valueOf(date.getDayOfMonth()));
    inputs.put(name + MONTH_SUFFIX, String.valueOf(date.getMonthValue()));
    inputs.put(name + YEAR_SUFFIX, String.valueOf(date.getYear()));
  }

  private static String formatValue(Object value) {
    return switch (value) {
      case null -> null;
      case String stringValue -> stringValue;
      case LocalDate date -> date.toString();
      default -> "TODO";
    };
  }

  private static ClaimViewField<?> getField(String fieldName, Class<?> claimDetailsType) {
    if (claimDetailsType == CrimeClaimDetails.class) {
      return getCrimeField(fieldName);
    } else if (claimDetailsType == CivilClaimDetails.class) {
      return getCivilField(fieldName);
    } else if (claimDetailsType == MediationClaimDetails.class) {
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
