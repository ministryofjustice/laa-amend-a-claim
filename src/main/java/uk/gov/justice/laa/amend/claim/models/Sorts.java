package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sorts {
    private Map<SortField, SortDirection> value;
    private boolean enabled;

    public Sorts(Sort sort) {
        this.value = Map.of(sort.getField(), sort.getDirection());
        this.enabled = true;
    }

    public static Sorts disabled() {
        return Sorts.builder().enabled(false).build();
    }

    public SortDirection getDirection(SortField field) {
        return value.getOrDefault(field, SortDirection.NONE);
    }
}
