package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class Sorts {
    private Map<String, SortDirection> value;

    public Sorts(Sort sort) {
        this.value = Map.of(sort.getField(), sort.getDirection());
    }

    public SortDirection getDirection(String field) {
        return value.getOrDefault(field, SortDirection.NONE);
    }
}
