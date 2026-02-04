package uk.gov.justice.laa.amend.claim.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.SortDirection;

public class SortConverterTest {

    @Test
    void shouldDefaultToAscendingUniqueFileNumberIfSourceIsNull() {
        String source = null;
        SortConverter converter = new SortConverter();
        Sort result = converter.convert(source);
        Assertions.assertEquals("uniqueFileNumber", result.getField());
        Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
    }

    @Test
    void shouldConvertAscendingField() {
        String source = "uniqueFileNumber,asc";
        SortConverter converter = new SortConverter();
        Sort result = converter.convert(source);
        Assertions.assertEquals("uniqueFileNumber", result.getField());
        Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
    }

    @Test
    void shouldConvertDescendingField() {
        String source = "caseReferenceNumber,desc";
        SortConverter converter = new SortConverter();
        Sort result = converter.convert(source);
        Assertions.assertEquals("caseReferenceNumber", result.getField());
        Assertions.assertEquals(SortDirection.DESCENDING, result.getDirection());
    }
}
