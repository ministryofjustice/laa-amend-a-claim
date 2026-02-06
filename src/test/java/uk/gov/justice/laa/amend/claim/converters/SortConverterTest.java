package uk.gov.justice.laa.amend.claim.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.SortDirection;
import uk.gov.justice.laa.amend.claim.models.SortField;

public class SortConverterTest {

    @Test
    void shouldDefaultToAscendingUniqueFileNumberIfSourceIsNull() {
        String source = null;
        SortConverter converter = new SortConverter();
        Sort result = converter.convert(source);
        Assertions.assertEquals(SortField.UNIQUE_FILE_NUMBER, result.getField());
        Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
    }

    @Test
    void shouldConvertAscendingField() {
        String source = "uniqueFileNumber,asc";
        SortConverter converter = new SortConverter();
        Sort result = converter.convert(source);
        Assertions.assertEquals(SortField.UNIQUE_FILE_NUMBER, result.getField());
        Assertions.assertEquals(SortDirection.ASCENDING, result.getDirection());
    }

    @Test
    void shouldConvertDescendingField() {
        String source = "caseReferenceNumber,desc";
        SortConverter converter = new SortConverter();
        Sort result = converter.convert(source);
        Assertions.assertEquals(SortField.CASE_REFERENCE_NUMBER, result.getField());
        Assertions.assertEquals(SortDirection.DESCENDING, result.getDirection());
    }

    @Test
    void shouldThrow400ExceptionWhenInvalidSort() {
        String source = "foo,bar";
        SortConverter converter = new SortConverter();

        ResponseStatusException exception =
                Assertions.assertThrows(ResponseStatusException.class, () -> converter.convert(source));

        Assertions.assertEquals(400, exception.getStatusCode().value());
    }
}
