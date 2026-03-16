package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SortFieldTest {

    @Nested
    class FromValueTests {
        @Test
        void shouldConvertUniqueFileNumber() {
            String str = "unique_file_number";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.UNIQUE_FILE_NUMBER, result);
        }

        @Test
        void shouldConvertCaseReferenceNumber() {
            String str = "case_reference_number";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.CASE_REFERENCE_NUMBER, result);
        }

        @Test
        void shouldConvertClientSurname() {
            String str = "client_surname";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.CLIENT_SURNAME, result);
        }

        @Test
        void shouldConvertSubmissionPeriod() {
            String str = "submission_period";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.SUBMISSION_PERIOD, result);
        }

        @Test
        void shouldConvertCategoryOfLaw() {
            String str = "category_of_law";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.CATEGORY_OF_LAW, result);
        }

        @Test
        void shouldConvertOfficeCode() {
            String str = "office_code";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.OFFICE_CODE, result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"foo", "bar"})
        void shouldThrowExceptionForAnythingElse(String str) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> SortField.fromValue(str));
        }
    }
}
