package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ClaimViewFieldTest {

  @Test
  void getOptionsIncludingReturnsConfiguredOptionsWhenSelectedValueIsBlank() {
    var field = CivilClaimDetailsViewField.CASE_STAGE;

    assertThat(field.getOptionsIncluding(null)).containsExactlyElementsOf(field.getOptions());
    assertThat(field.getOptionsIncluding("")).containsExactlyElementsOf(field.getOptions());
  }

  @Test
  void getOptionsIncludingReturnsConfiguredOptionsWhenSelectedValueAlreadyExists() {
    var field = CivilClaimDetailsViewField.CASE_STAGE;

    assertThat(field.getOptionsIncluding("FPL01")).containsExactlyElementsOf(field.getOptions());
  }

  @Test
  void getOptionsIncludingPrependsSelectedValueWhenItIsNotConfigured() {
    var field = CivilClaimDetailsViewField.CASE_STAGE;

    assertThat(field.getOptionsIncluding("UNKNOWN"))
        .startsWith(new FieldOption("UNKNOWN", "UNKNOWN"), new FieldOption("FPL01", "FPL01"));
  }
}
