package uk.gov.justice.laa.payments.amend.utils;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MatterTypeUtils {

  private static final String DELIMITER = "[+:]";
  private static final String PART_SUFFIX = "#";

  public static final String MATTER_TYPE_CODE = "claim.matterTypeCode";

  public static final int FIRST_PART = 0;
  public static final int SECOND_PART = 1;

  public static final String MATTER_TYPE_CODE_1 = MATTER_TYPE_CODE + PART_SUFFIX + FIRST_PART;
  public static final String MATTER_TYPE_CODE_2 = MATTER_TYPE_CODE + PART_SUFFIX + SECOND_PART;

  public static String part(String matterTypeCode, int part) {
    if (matterTypeCode == null || matterTypeCode.isBlank()) {
      return null;
    }
    String[] split = matterTypeCode.split(DELIMITER);
    return part < split.length ? split[part] : null;
  }

  public static Set<String> changedPartIdentifiers(String before, String after) {
    Set<String> identifiers = new LinkedHashSet<>();
    if (!Objects.equals(part(before, FIRST_PART), part(after, FIRST_PART))) {
      identifiers.add(MATTER_TYPE_CODE_1);
    }
    if (!Objects.equals(part(before, SECOND_PART), part(after, SECOND_PART))) {
      identifiers.add(MATTER_TYPE_CODE_2);
    }
    return identifiers;
  }
}
