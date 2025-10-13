package uk.gov.justice.laa.amend.claim.forms.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.utils.StringUtils;

import java.util.Map;

@Data
@AllArgsConstructor
public abstract class FormError implements Comparable<FormError> {
    private String fieldName;
    private String message;

    public FormError(DetailedError error) {
        this.fieldName = error.getFieldName();
        this.message = error.getMessage();
    }

    public String getFieldId() {
        return StringUtils.toFieldId(fieldName);
    }

    @Override
    public int compareTo(FormError other) {
        if (other == null) {
            return 1;
        } else {
            return Integer.compare(
                getFieldOrder(this.getFieldName()),
                getFieldOrder(other.getFieldName())
            );
        }
    }

    private int getFieldOrder(String fieldName) {
        return getFieldOrderMap().getOrDefault(fieldName, Integer.MAX_VALUE);
    }

    protected abstract Map<String, Integer> getFieldOrderMap();
}
