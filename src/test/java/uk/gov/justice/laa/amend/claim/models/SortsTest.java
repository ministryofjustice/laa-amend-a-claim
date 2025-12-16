package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class SortsTest {

    @Test
    void defaultToNoneIfFieldNotFound() {
        Sorts sorts = new Sorts();
        sorts.setValue(Map.of("uniqueFileNumber", SortDirection.ASCENDING));
        Assertions.assertEquals(SortDirection.NONE, sorts.getDirection("caseReferenceNumber"));
    }

    @Test
    void returnDirectionIfFieldFound() {
        Sorts sorts = new Sorts();
        sorts.setValue(Map.of("uniqueFileNumber", SortDirection.ASCENDING));
        Assertions.assertEquals(SortDirection.ASCENDING, sorts.getDirection("uniqueFileNumber"));
    }

    @Test
    void returnsValueWhenEnabled() {
        Sort sort = Sort.builder().field("uniqueFileNumber").direction(SortDirection.ASCENDING).build();
        Sorts sorts = new Sorts(sort);
        Assertions.assertEquals(SortDirection.ASCENDING, sorts.getDirection("uniqueFileNumber"));
        Assertions.assertTrue(sorts.isEnabled());
    }

    @Test
    void returnsNullValueWhenDisabled() {
        Sorts sorts = Sorts.disabled();
        Assertions.assertNull(sorts.getValue());
        Assertions.assertFalse(sorts.isEnabled());
    }
}
