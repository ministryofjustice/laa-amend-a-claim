package uk.gov.justice.laa.amend.claim.forms.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.helpers.StringUtils;

@Data
@AllArgsConstructor
public abstract class FormError<T> implements Comparable<T> {
    private String fieldName;
    private String message;

    public FormError(DetailedError error) {
        this.fieldName = error.getFieldName();
        this.message = error.getMessage();
    }

    public String getFieldId() {
        return StringUtils.toFieldId(fieldName);
    }
}
