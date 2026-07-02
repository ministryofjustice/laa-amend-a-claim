package uk.gov.justice.laa.amend.claim.forms.amendments;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

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
  void getFieldInputsCollapsesDateSubInputsIntoSingleField() {
    var rows = new LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object>();
    rows.put(CivilClaimDetailsViewField.DATE_OF_BIRTH, LocalDate.of(2002, 5, 14));
    rows.put(CivilClaimDetailsViewField.POSTCODE, "AB1 2CD");
    var form = new AmendmentForm(rows);

    var fieldInputs = form.getFieldInputs(CivilClaimDetails.class);

    assertThat(fieldInputs)
        .hasSize(2)
        .containsEntry(CivilClaimDetailsViewField.DATE_OF_BIRTH, "2002-05-14")
        .containsEntry(CivilClaimDetailsViewField.POSTCODE, "AB1 2CD");
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
}
