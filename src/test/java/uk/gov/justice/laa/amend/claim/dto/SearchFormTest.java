package uk.gov.justice.laa.amend.claim.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearchFormTest {

    @Test
    void testWithNullValues() {
        SearchForm form = new SearchForm();

        Assertions.assertNull(form.getProviderAccountNumber());
        Assertions.assertNull(form.getSubmissionDateMonth());
        Assertions.assertNull(form.getSubmissionDateYear());
        Assertions.assertNull(form.getReferenceNumber());
    }

    @Test
    void testRecordCreationAndGetters() {
        SearchForm form = new SearchForm(
            "123",
            3,
            2007,
            "456"
        );

        Assertions.assertEquals("123", form.getProviderAccountNumber());
        Assertions.assertEquals(3, form.getSubmissionDateMonth());
        Assertions.assertEquals(2007, form.getSubmissionDateYear());
        Assertions.assertEquals("456", form.getReferenceNumber());
    }

    @Test
    void testAllEmptyReturnsTrueWhenAllValuesAreNull() {
        SearchForm form = new SearchForm();
        Assertions.assertTrue(form.allEmpty());
    }

    @Test
    void testAllEmptyReturnsTrueWhenAllValuesAreBlank() {
        SearchForm form = new SearchForm(
            "",
            null,
            null,
            ""
        );

        Assertions.assertTrue(form.allEmpty());
    }

    @Test
    void testAllEmptyReturnsFalseWhenValuesAreNotNull() {
        SearchForm form = new SearchForm(
            "123",
            3,
            2007,
            "456"
        );

        Assertions.assertFalse(form.allEmpty());
    }
}
