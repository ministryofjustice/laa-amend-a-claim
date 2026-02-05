package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@Builder
public class Sort {
    private SortField field;
    private SortDirection direction;

    @Override
    public String toString() {
        return direction.getValue() != null ? String.format("%s,%s", field.getValue(), direction.getValue()) : null;
    }

    public static Sort defaults() {
        Sort sort = Sort.builder().build();
        sort.applyDefaults();
        return sort;
    }

    public Sort(String str) {
        if (str != null) {
            Pattern pattern = Pattern.compile("^(\\w+),(\\w+)$");
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                this.field = SortField.fromValue(matcher.group(1));
                this.direction = SortDirection.fromValue(matcher.group(2));
            } else {
                throw new IllegalArgumentException("Could not parse sort string: " + str);
            }
        } else {
            applyDefaults();
        }
    }

    private void applyDefaults() {
        this.field = SortField.UNIQUE_FILE_NUMBER;
        this.direction = SortDirection.ASCENDING;
    }
}
