package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_SORT_FIELD;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_SORT_ORDER;

@Data
@AllArgsConstructor
@Builder
public class Sort {
    private String field;
    private SortDirection direction;

    @Override
    public String toString() {
        return direction.getValue() != null ? String.format("%s,%s", field, direction.getValue()) : null;
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
                this.field = matcher.group(1);
                this.direction = SortDirection.fromValue(matcher.group(2));
            } else {
                applyDefaults();
            }
        } else {
            applyDefaults();
        }
    }

    private void applyDefaults() {
        this.field = DEFAULT_SORT_FIELD;
        this.direction = DEFAULT_SORT_ORDER;
    }
}
