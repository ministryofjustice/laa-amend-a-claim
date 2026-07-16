package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public final class FieldOptions {

  public static final List<FieldOption> ADVICE_TYPE = of("FTF", "REM");
  public static final List<FieldOption> AIT_HEARING_CENTRE =
      List.of(
          option("01", "01 - Birmingham"),
          option("02", "02 - Bradford"),
          option("03", "03 - Harmondsworth"),
          option("04", "04 - London Field House"),
          option("05", "05 - London Hatton Cross"),
          option("06", "06 - London Taylor House"),
          option("07", "07 - Manchester"),
          option("08", "08 - Newport"),
          option("09", "09 - North Shields"),
          option("10", "10 - Notts"),
          option("11", "11 - Stoke"),
          option("12", "12 - Surbiton"),
          option("13", "13 - Walsall"),
          option("14", "14 - Yarls Wood"),
          option("15", "15 - N/A Application Only"),
          option("16", "16 - Other"));
  public static final List<FieldOption> CASE_STAGE =
      concat(numberedRange("FPL", 1, 21), numberedRange("FPC", 1, 3), numberedRange("MHL", 1, 10));
  public static final List<FieldOption> DESIGNATED_ACCREDITED_REPRESENTATIVE =
      of("1", "2", "3", "4", "5");
  public static final List<FieldOption> EXEMPTION_CRITERIA_SATISFIED =
      concat(
          numberedRange("DV", 1, 19, 3),
          numberedRange("CA", 1, 8, 3),
          of("TR001", "CN001", "UA001"));
  public static final List<FieldOption> MEETINGS_ATTENDED = numberedRange("MTGA", 1, 24, 2);
  public static final List<FieldOption> MEDIATION_REFERRAL =
      List.of(
          option("02", "02 - Referral from Solicitor"),
          option("03", "03 - Referral from Court"),
          option("04", "04 - Referral from CAB"),
          option("05", "05 - Referral from other advice agency or telephone helpline"),
          option("06", "06 - Referral from Relate or other relationship counselling"),
          option("07", "07 - Referral from GP/NHS"),
          option("08", "08 - Client self-referred"),
          option("09", "09 - Other"),
          option("10", "10 - Unknown"),
          option("11", "11 - Separated Parents Information Programme (SPIP)"));
  public static final List<FieldOption> OUTCOME =
      concat(
          numberedRange("CN", 1, 13),
          numberedRange("CP", 1, 28),
          numberedRange("PL", 1, 14),
          of("A", "B", "S", "C", "P"));
  public static final List<FieldOption> STAGE_REACHED =
      concat(
          letterRange("INV", 'A', 'M'),
          letterRange("PRI", 'A', 'E'),
          of(
              "PROC", "PROD", "PROE", "PROF", "PROH", "PROI", "PROJ", "PROK", "PROL", "PROP",
              "PROT", "PROU", "PROV", "PROW"),
          letterRange("APP", 'A', 'C'),
          of("ASMS", "ASPL", "ASAS"),
          of("YOUE", "YOUF", "YOUK", "YOUL", "YOUX", "YOUY"),
          of("VOID"));
  public static final List<FieldOption> STANDARD_FEE_CATEGORY =
      of(
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

  private FieldOptions() {}

  @SafeVarargs
  private static List<FieldOption> concat(List<FieldOption>... optionLists) {
    return Arrays.stream(optionLists).flatMap(List::stream).toList();
  }

  private static List<FieldOption> letterRange(
      String prefix, char startInclusive, char endInclusive) {
    return IntStream.rangeClosed(startInclusive, endInclusive)
        .mapToObj(letter -> prefix + (char) letter)
        .map(value -> new FieldOption(value, value))
        .toList();
  }

  private static FieldOption option(String value, String label) {
    return new FieldOption(value, label);
  }

  private static List<FieldOption> of(String... values) {
    return Arrays.stream(values).map(value -> new FieldOption(value, value)).toList();
  }

  private static List<FieldOption> numberedRange(
      String prefix, int startInclusive, int endInclusive) {
    return numberedRange(prefix, startInclusive, endInclusive, 2);
  }

  private static List<FieldOption> numberedRange(
      String prefix, int startInclusive, int endInclusive, int width) {
    return IntStream.rangeClosed(startInclusive, endInclusive)
        .mapToObj(number -> prefix + String.format("%0" + width + "d", number))
        .map(value -> new FieldOption(value, value))
        .toList();
  }
}
