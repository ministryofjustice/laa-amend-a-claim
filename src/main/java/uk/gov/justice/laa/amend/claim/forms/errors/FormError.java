package uk.gov.justice.laa.amend.claim.forms.errors;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.utils.FormUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class FormError implements Comparable<FormError> {
  private String fieldName;
  private String message;
  private Object[] args;

  protected FormError(DetailedError error) {
    this.fieldName = error.getFieldName();
    this.message = error.getMessage();
    this.args = error.getArguments();
  }

  protected FormError(String fieldName, String message) {
    this.fieldName = fieldName;
    this.message = message;
    this.args = new Object[] {};
  }

  public String getFieldId() {
    return FormUtils.toFieldId(fieldName);
  }

  @Override
  public int compareTo(FormError other) {
    if (other == null) {
      return 1;
    } else {
      return Integer.compare(
          getFieldOrder(this.getFieldName()), getFieldOrder(other.getFieldName()));
    }
  }

  private int getFieldOrder(String fieldName) {
    return getFieldOrderMap().getOrDefault(fieldName, Integer.MAX_VALUE);
  }

  protected abstract Map<String, Integer> getFieldOrderMap();
}
