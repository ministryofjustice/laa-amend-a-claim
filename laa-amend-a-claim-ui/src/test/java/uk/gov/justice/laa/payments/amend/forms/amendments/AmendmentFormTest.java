package uk.gov.justice.laa.payments.amend.forms.amendments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

class AmendmentFormTest {

  @Test
  void seedsDateFieldAsDayMonthYearSubInputs() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, LocalDate.of(2002, 5, 14));
    rows.put(CivilClaimDetailsViewField.POSTCODE, "AB1 2CD");

    var form = new AmendmentForm(rows);

    assertThat(form.getInputs())
        .containsEntry("DATE_OF_BIRTH-day", "14")
        .containsEntry("DATE_OF_BIRTH-month", "5")
        .containsEntry("DATE_OF_BIRTH-year", "2002")
        .containsEntry("POSTCODE", "AB1 2CD")
        .doesNotContainKey("DATE_OF_BIRTH");
  }

  @Test
  void seedsNullDateFieldAsEmptySubInputs() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, null);

    var form = new AmendmentForm(rows);

    assertThat(form.getInputs())
        .containsEntry("DATE_OF_BIRTH-day", "")
        .containsEntry("DATE_OF_BIRTH-month", "")
        .containsEntry("DATE_OF_BIRTH-year", "");
  }

  @Test
  void throwsWhenDateFieldValueIsNotLocalDate() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, "not-a-date");

    assertThatThrownBy(() -> new AmendmentForm(rows))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("DATE_OF_BIRTH");
  }

  @Test
  void throwsWhenNonDateFieldValueIsLocalDate() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.POSTCODE, LocalDate.of(2002, 5, 14));

    assertThatThrownBy(() -> new AmendmentForm(rows)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void seedsBooleanFieldAsTrueFalseInput() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, true);
    rows.put(CivilClaimDetailsViewField.IS_POSTAL_APPLICATION_ACCEPTED, false);

    var form = new AmendmentForm(rows);

    assertThat(form.getInputs())
        .containsEntry("IS_ELIGIBLE_CLIENT", "true")
        .containsEntry("IS_POSTAL_APPLICATION_ACCEPTED", "false");
  }

  @Test
  void seedsNullBooleanFieldAsNullInput() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, null);

    var form = new AmendmentForm(rows);

    assertThat(form.getInputs()).containsEntry("IS_ELIGIBLE_CLIENT", null);
  }

  @Test
  void seedsBigDecimalFieldAsScaledInput() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.VALUE_OF_COSTS, BigDecimal.valueOf(10.1));

    var form = new AmendmentForm(rows);

    assertThat(form.getInputs()).containsEntry("VALUE_OF_COSTS", "10.10");
  }

  @Test
  void throwsWhenBooleanFieldValueIsNotBoolean() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, "not-a-boolean");

    assertThatThrownBy(() -> new AmendmentForm(rows))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("IS_ELIGIBLE_CLIENT");
  }

  @Test
  void getFieldInputsCollapsesDateSubInputsIntoSingleField() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, LocalDate.of(2002, 5, 14));
    rows.put(CivilClaimDetailsViewField.POSTCODE, "AB1 2CD");
    var form = new AmendmentForm(rows);

    var fieldInputs = form.getFieldValues(CivilClaimDetails.class);

    assertThat(fieldInputs)
        .hasSize(2)
        .containsEntry(CivilClaimDetailsViewField.DATE_OF_BIRTH, LocalDate.of(2002, 5, 14))
        .containsEntry(CivilClaimDetailsViewField.POSTCODE, "AB1 2CD");
  }

  @Test
  void getFieldValuesThrowsWhenTypedValueIsInvalid() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("IS_ELIGIBLE_CLIENT", "not-a-boolean")));

    assertThatThrownBy(() -> form.getFieldValues(CivilClaimDetails.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Boolean value")
        .hasMessageContaining("IS_ELIGIBLE_CLIENT");
  }

  @Test
  void getDateValueRecombinesSubInputs() {
    var form = new AmendmentForm();
    form.setInputs(
        new HashMap<>(
            Map.of(
                "DATE_OF_BIRTH-day", "14",
                "DATE_OF_BIRTH-month", "5",
                "DATE_OF_BIRTH-year", "2002")));

    assertThat(form.getDateValue("DATE_OF_BIRTH")).isEqualTo(LocalDate.of(2002, 5, 14));
  }

  @Test
  void getDateValueReturnsNullWhenAnyPartBlank() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("DOB-day", "14", "DOB-month", "", "DOB-year", "2002")));

    assertThat(form.getDateValue("DOB")).isNull();
  }

  @Test
  void getDateValueReturnsNullWhenPartsDoNotFormValidDate() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("DOB-day", "31", "DOB-month", "2", "DOB-year", "2002")));

    assertThat(form.getDateValue("DOB")).isNull();
  }

  @Test
  void getAmendedValueRecombinesDateSubInputs() {
    var form = new AmendmentForm();
    form.setInputs(
        new HashMap<>(
            Map.of(
                "DATE_OF_BIRTH-day", "14",
                "DATE_OF_BIRTH-month", "5",
                "DATE_OF_BIRTH-year", "2002")));

    assertThat(form.getAmendedValue("DATE_OF_BIRTH")).isEqualTo(LocalDate.of(2002, 5, 14));
  }

  @Test
  void getAmendedValueReturnsRawInputForNonDateField() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("POSTCODE", "AB1 2CD")));

    assertThat(form.getAmendedValue("POSTCODE")).isEqualTo("AB1 2CD");
  }

  @Test
  void getAmendedValueReturnsBooleanForBooleanField() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("IS_ELIGIBLE_CLIENT", "true")));

    assertThat(form.getAmendedValue(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT)).isEqualTo(true);
  }

  @Test
  void getAmendedValueReturnsBigDecimalForBigDecimalField() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("VALUE_OF_COSTS", "10.1")));

    assertThat(form.getAmendedValue(CivilClaimDetailsViewField.VALUE_OF_COSTS))
        .isEqualTo(BigDecimal.valueOf(10.10).setScale(2));
  }

  @Test
  void getBigDecimalValueRejectsMoreThanTwoDecimalPlacesRatherThanRounding() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("VALUE_OF_COSTS", "10.129")));

    assertThat(form.getBigDecimalValue("VALUE_OF_COSTS")).isNull();
  }

  @Test
  void seedsNumberFieldAsIntegerInput() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.TRAVEL_TIME, 200);

    var form = new AmendmentForm(rows);

    assertThat(form.getInputs()).containsEntry("TRAVEL_TIME", "200");
  }

  @Test
  void seedsNullNumberFieldAsNullInput() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.TRAVEL_TIME, null);
    var original = new AmendmentForm(rows);

    assertThat(original.getInputs()).containsEntry("TRAVEL_TIME", null);

    var current = new AmendmentForm();
    current.setInputs(new HashMap<>(Map.of("TRAVEL_TIME", "")));
    assertThat(current.isAmendment("TRAVEL_TIME", original)).isFalse();
  }

  @Test
  void isAmendmentTreatsSeededEmptyNumberAndAbsentCurrentAsUnchanged() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.TRAVEL_TIME, null);
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm();
    current.setInputs(new HashMap<>());

    assertThat(current.isAmendment("TRAVEL_TIME", original)).isFalse();
  }

  @Test
  void isAmendmentTreatsSeededEmptyBooleanAndAbsentCurrentAsUnchanged() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, null);
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm();
    current.setInputs(new HashMap<>());

    assertThat(current.isAmendment("IS_ELIGIBLE_CLIENT", original)).isFalse();
  }

  @Test
  void getAmendedValueReturnsIntegerForNumberField() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("TRAVEL_TIME", "200")));

    assertThat(form.getAmendedValue(CivilClaimDetailsViewField.TRAVEL_TIME)).isEqualTo(200);
  }

  @Test
  void getAmendedValueThrowsForNonNumericNumberInput() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("TRAVEL_TIME", "not-a-number")));

    assertThatThrownBy(() -> form.getAmendedValue(CivilClaimDetailsViewField.TRAVEL_TIME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Integer value")
        .hasMessageContaining("TRAVEL_TIME");
  }

  @Test
  void getAmendedValueReadsBigDecimalCostFieldByName() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("PROFIT_COST", "150.25")));

    assertThat(form.getAmendedValue(ClaimDetailsViewField.PROFIT_COST))
        .isEqualTo(new BigDecimal("150.25"));
  }

  @Test
  void getBooleanValueReturnsNullWhenInputBlank() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("IS_ELIGIBLE_CLIENT", "")));

    assertThat(form.getBooleanValue("IS_ELIGIBLE_CLIENT")).isNull();
  }

  @Test
  void getBooleanValueThrowsWhenInputIsNotTrueOrFalse() {
    var form = new AmendmentForm();
    form.setInputs(new HashMap<>(Map.of("IS_ELIGIBLE_CLIENT", "not-a-boolean")));

    assertThatThrownBy(() -> form.getBooleanValue("IS_ELIGIBLE_CLIENT"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Boolean value")
        .hasMessageContaining("IS_ELIGIBLE_CLIENT");
  }

  @Test
  void isAmendmentDetectsChangedDateField() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, LocalDate.of(2002, 5, 14));
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm(original);
    current.getInputs().put("DATE_OF_BIRTH-year", "2003");

    assertThat(current.isAmendment("DATE_OF_BIRTH", original)).isTrue();
  }

  @Test
  void isAmendmentReturnsFalseForUnchangedDateField() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, LocalDate.of(2002, 5, 14));
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm(original);

    assertThat(current.isAmendment("DATE_OF_BIRTH", original)).isFalse();
  }

  @Test
  void hasAmendmentsIgnoresDateSubInputReformattingThatKeepsSameDate() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, LocalDate.of(2002, 5, 14));
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm(original);
    current.getInputs().put("DATE_OF_BIRTH-day", "14 ");
    current.getInputs().put("DATE_OF_BIRTH-month", "05");

    assertThat(current.hasAmendments(original)).isFalse();
  }

  @Test
  void hasAmendmentsDetectsChangedDate() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, LocalDate.of(2002, 5, 14));
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm(original);
    current.getInputs().put("DATE_OF_BIRTH-year", "2003");

    assertThat(current.hasAmendments(original)).isTrue();
  }

  @Test
  void hasAmendmentsDetectsChangedBoolean() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, true);
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm(original);
    current.getInputs().put("IS_ELIGIBLE_CLIENT", "false");

    assertThat(current.hasAmendments(original)).isTrue();
  }

  @Test
  void hasAmendmentsByFieldTypeIgnoresUnchangedTypedFields() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.VALUE_OF_COSTS, BigDecimal.valueOf(10.10));
    rows.put(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, true);
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm(original);
    current.getInputs().put("VALUE_OF_COSTS", "10.1");
    current.getInputs().put("IS_ELIGIBLE_CLIENT", "true");

    assertThat(current.hasAmendmentsByFieldType(original, rows)).isFalse();
  }

  @Test
  void hasAmendmentsByFieldTypeDetectsChangedTypedField() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.VALUE_OF_COSTS, BigDecimal.valueOf(10.10));
    rows.put(CivilClaimDetailsViewField.IS_ELIGIBLE_CLIENT, true);
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm(original);
    current.getInputs().put("VALUE_OF_COSTS", "10.2");
    current.getInputs().put("IS_ELIGIBLE_CLIENT", "true");

    assertThat(current.hasAmendmentsByFieldType(original, rows)).isTrue();
  }

  @Test
  void isAmendmentIgnoresBigDecimalReformatting() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.VALUE_OF_COSTS, BigDecimal.valueOf(10.10));
    var original = new AmendmentForm(rows);

    var current = new AmendmentForm(original);
    current.getInputs().put("VALUE_OF_COSTS", "10.1");

    assertThat(
            current.isAmendment(
                "VALUE_OF_COSTS",
                original,
                CivilClaimDetailsViewField.VALUE_OF_COSTS.getFieldType()))
        .isFalse();
  }
}
