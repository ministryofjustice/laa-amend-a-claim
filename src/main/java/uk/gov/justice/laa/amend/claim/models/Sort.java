package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_SORT_FIELD;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_SORT_ORDER;

@Data
@AllArgsConstructor
public class Sort {
    private String field;
    private SortDirection direction;

    @Override
    public String toString() {
        return String.format("%s,%s", field, direction.getValue());
    }

    public Sort() {
        this.field = DEFAULT_SORT_FIELD;
        this.direction = DEFAULT_SORT_ORDER;
    }

    public Sort(String str) {
        try {
            String[] values = str.split(",");
            this.field = values[0];
            this.direction = SortDirection.fromValue(values[1]);
        } catch (Exception ex) {
            this.field = DEFAULT_SORT_FIELD;
            this.direction = DEFAULT_SORT_ORDER;
        }
    }
}
