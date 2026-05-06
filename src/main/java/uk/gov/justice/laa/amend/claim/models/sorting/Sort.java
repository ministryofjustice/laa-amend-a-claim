package uk.gov.justice.laa.amend.claim.models.sorting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class Sort<T extends SortField> {
  protected T field;
  protected SortDirection direction;

  @Override
  public String toString() {
    return direction.getValue() != null
        ? String.format("%s,%s", field.getValue(), direction.getValue())
        : null;
  }

  public Sort(String str) {
    if (str != null) {
      Pattern pattern = Pattern.compile("^(\\w+),(\\w+)$");
      Matcher matcher = pattern.matcher(str);
      if (matcher.matches()) {
        this.field = createField(matcher.group(1));
        this.direction = SortDirection.fromValue(matcher.group(2));
      } else {
        throw new IllegalArgumentException("Could not parse sort string: " + str);
      }
    } else {
      applyDefaults();
    }
  }

  abstract void applyDefaults();

  abstract T createField(String value);
}
