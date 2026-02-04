package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class SortsTest {

    @Test
    void defaultToNoneIfFieldNotFound() {
        Sorts sorts = new Sorts();
        sorts.setValue(Map.of(SortField.UNIQUE_FILE_NUMBER, SortDirection.ASCENDING));
        Assertions.assertEquals(SortDirection.NONE, sorts.getDirection(SortField.CASE_REFERENCE_NUMBER));
    }

    @Test
    void returnDirectionIfFieldFound() {
        Sorts sorts = new Sorts();
        sorts.setValue(Map.of(SortField.UNIQUE_FILE_NUMBER, SortDirection.ASCENDING));
        Assertions.assertEquals(SortDirection.ASCENDING, sorts.getDirection(SortField.UNIQUE_FILE_NUMBER));
    }

    @Test
    void returnsValueWhenEnabled() {
        Sort sort = Sort.builder().field(SortField.UNIQUE_FILE_NUMBER).direction(SortDirection.ASCENDING).build();
        Sorts sorts = new Sorts(sort);
        Assertions.assertEquals(SortDirection.ASCENDING, sorts.getDirection(SortField.UNIQUE_FILE_NUMBER));
        Assertions.assertTrue(sorts.isEnabled());
    }

    @Test
    void returnsNullValueWhenDisabled() {
        Sorts sorts = Sorts.disabled();
        Assertions.assertNull(sorts.getValue());
        Assertions.assertFalse(sorts.isEnabled());
    }
}
