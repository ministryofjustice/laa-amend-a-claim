package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class FieldOptionsTest {

  @Test
  void caseStageOptionsMatchDataDictionary() {
    assertOptionValues(
        FieldOptions.CASE_STAGE,
        "FPL01",
        "FPL02",
        "FPL03",
        "FPL04",
        "FPL05",
        "FPL06",
        "FPL07",
        "FPL08",
        "FPL09",
        "FPL10",
        "FPL11",
        "FPL12",
        "FPL13",
        "FPL14",
        "FPL15",
        "FPL16",
        "FPL17",
        "FPL18",
        "FPL19",
        "FPL20",
        "FPL21",
        "FPC01",
        "FPC02",
        "FPC03",
        "MHL01",
        "MHL02",
        "MHL03",
        "MHL04",
        "MHL05",
        "MHL06",
        "MHL07",
        "MHL08",
        "MHL09",
        "MHL10");
  }

  @Test
  void stageReachedOptionsMatchDataDictionary() {
    assertOptionValues(
        FieldOptions.STAGE_REACHED,
        "INVA",
        "INVB",
        "INVC",
        "INVD",
        "INVE",
        "INVF",
        "INVG",
        "INVH",
        "INVI",
        "INVJ",
        "INVK",
        "INVL",
        "INVM",
        "PRIA",
        "PRIB",
        "PRIC",
        "PRID",
        "PRIE",
        "PROC",
        "PROD",
        "PROE",
        "PROF",
        "PROH",
        "PROI",
        "PROJ",
        "PROK",
        "PROL",
        "PROP",
        "PROT",
        "PROU",
        "PROV",
        "PROW",
        "APPA",
        "APPB",
        "APPC",
        "ASMS",
        "ASPL",
        "ASAS",
        "YOUE",
        "YOUF",
        "YOUK",
        "YOUL",
        "YOUX",
        "YOUY",
        "VOID");
  }

  @Test
  void outcomeOptionsMatchDataDictionary() {
    assertOptionValues(
        FieldOptions.OUTCOME,
        "CN01",
        "CN02",
        "CN03",
        "CN04",
        "CN05",
        "CN06",
        "CN07",
        "CN08",
        "CN09",
        "CN10",
        "CN11",
        "CN12",
        "CN13",
        "CP01",
        "CP02",
        "CP03",
        "CP04",
        "CP05",
        "CP06",
        "CP07",
        "CP08",
        "CP09",
        "CP10",
        "CP11",
        "CP12",
        "CP13",
        "CP14",
        "CP15",
        "CP16",
        "CP17",
        "CP18",
        "CP19",
        "CP20",
        "CP21",
        "CP22",
        "CP23",
        "CP24",
        "CP25",
        "CP26",
        "CP27",
        "CP28",
        "PL01",
        "PL02",
        "PL03",
        "PL04",
        "PL05",
        "PL06",
        "PL07",
        "PL08",
        "PL09",
        "PL10",
        "PL11",
        "PL12",
        "PL13",
        "PL14",
        "A",
        "B",
        "S",
        "C",
        "P");
  }

  @Test
  void aitHearingCentreOptionsMatchDataDictionary() {
    assertThat(FieldOptions.AIT_HEARING_CENTRE)
        .containsExactly(
            new FieldOption("01", "01 - Birmingham"),
            new FieldOption("02", "02 - Bradford"),
            new FieldOption("03", "03 - Harmondsworth"),
            new FieldOption("04", "04 - London Field House"),
            new FieldOption("05", "05 - London Hatton Cross"),
            new FieldOption("06", "06 - London Taylor House"),
            new FieldOption("07", "07 - Manchester"),
            new FieldOption("08", "08 - Newport"),
            new FieldOption("09", "09 - North Shields"),
            new FieldOption("10", "10 - Notts"),
            new FieldOption("11", "11 - Stoke"),
            new FieldOption("12", "12 - Surbiton"),
            new FieldOption("13", "13 - Walsall"),
            new FieldOption("14", "14 - Yarls Wood"),
            new FieldOption("15", "15 - N/A Application Only"),
            new FieldOption("16", "16 - Other"));
  }

  @Test
  void designatedAccreditedRepresentativeOptionsMatchDataDictionary() {
    assertOptionValues(FieldOptions.DESIGNATED_ACCREDITED_REPRESENTATIVE, "1", "2", "3", "4", "5");
  }

  @Test
  void meetingsAttendedOptionsMatchDataDictionary() {
    assertOptionValues(
        FieldOptions.MEETINGS_ATTENDED,
        "MTGA01",
        "MTGA02",
        "MTGA03",
        "MTGA04",
        "MTGA05",
        "MTGA06",
        "MTGA07",
        "MTGA08",
        "MTGA09",
        "MTGA10",
        "MTGA11",
        "MTGA12",
        "MTGA13",
        "MTGA14",
        "MTGA15",
        "MTGA16",
        "MTGA17",
        "MTGA18",
        "MTGA19",
        "MTGA20",
        "MTGA21",
        "MTGA22",
        "MTGA23",
        "MTGA24");
  }

  @Test
  void adviceTypeOptionsMatchDataDictionary() {
    assertOptionValues(FieldOptions.ADVICE_TYPE, "FTF", "REM");
  }

  @Test
  void exemptionCriteriaSatisfiedOptionsMatchDataDictionary() {
    assertOptionValues(
        FieldOptions.EXEMPTION_CRITERIA_SATISFIED,
        "DV001",
        "DV002",
        "DV003",
        "DV004",
        "DV005",
        "DV006",
        "DV007",
        "DV008",
        "DV009",
        "DV010",
        "DV011",
        "DV012",
        "DV013",
        "DV014",
        "DV015",
        "DV016",
        "DV017",
        "DV018",
        "DV019",
        "CA001",
        "CA002",
        "CA003",
        "CA004",
        "CA005",
        "CA006",
        "CA007",
        "CA008",
        "TR001",
        "CN001",
        "UA001");
  }

  @Test
  void standardFeeCategoryOptionsMatchDataDictionary() {
    assertOptionValues(
        FieldOptions.STANDARD_FEE_CATEGORY,
        "1A",
        "2A",
        "1B",
        "2B",
        "1A-HSF",
        "1B-HSF",
        "1A-LSF",
        "1B-LSF",
        "1EW",
        "1SO",
        "2",
        "3",
        "ULF",
        "UHF",
        "CLF",
        "CHF",
        "Sending Hearing Fixed Fee");
  }

  @Test
  void referralOptionsMatchDataDictionary() {
    assertThat(FieldOptions.MEDIATION_REFERRAL)
        .containsExactly(
            new FieldOption("02", "02 - Referral from Solicitor"),
            new FieldOption("03", "03 - Referral from Court"),
            new FieldOption("04", "04 - Referral from CAB"),
            new FieldOption("05", "05 - Referral from other advice agency or telephone helpline"),
            new FieldOption("06", "06 - Referral from Relate or other relationship counselling"),
            new FieldOption("07", "07 - Referral from GP/NHS"),
            new FieldOption("08", "08 - Client self-referred"),
            new FieldOption("09", "09 - Other"),
            new FieldOption("10", "10 - Unknown"),
            new FieldOption("11", "11 - Separated Parents Information Programme (SPIP)"));
  }

  private void assertOptionValues(List<FieldOption> options, String... values) {
    assertThat(options).extracting(FieldOption::value).containsExactly(values);
    assertThat(options).extracting(FieldOption::label).containsExactly(values);
  }
}
