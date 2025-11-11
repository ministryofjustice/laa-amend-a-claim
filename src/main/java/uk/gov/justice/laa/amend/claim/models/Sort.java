package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_SORT_FIELD;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_SORT_ORDER;

@Data
@AllArgsConstructor
public class Sort {
    private String field;
    private SortDirection direction;

    @Override
    public String toString() {
        if (direction == SortDirection.NONE) {
            return null;
        }
        return String.format("%s,%s", field, direction.getValue());
    }

    public Sort() {
        applyDefaults();
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
