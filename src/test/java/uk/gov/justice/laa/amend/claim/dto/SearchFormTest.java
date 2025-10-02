package uk.gov.justice.laa.amend.claim.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearchFormTest {

    @Test
    void testWithNullValues() {
        SearchForm form = new SearchForm(
            null,
            null,
            null,
            null
        );

        Assertions.assertNull(form.providerAccountNumber());
        Assertions.assertNull(form.submissionDateMonth());
        Assertions.assertNull(form.submissionDateYear());
        Assertions.assertNull(form.referenceNumber());
    }

    @Test
    void testRecordCreationAndGetters() {
        SearchForm form = new SearchForm(
            "ACC123",
            5,
            2023,
            "REF456"
        );

        Assertions.assertEquals("ACC123", form.providerAccountNumber());
        Assertions.assertEquals(5, form.submissionDateMonth());
        Assertions.assertEquals(2023, form.submissionDateYear());
        Assertions.assertEquals("REF456", form.referenceNumber());
    }
}
