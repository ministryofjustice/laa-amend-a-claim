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
            String str = "uniqueFileNumber";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.UNIQUE_FILE_NUMBER, result);
        }

        @Test
        void shouldConvertCaseReferenceNumber() {
            String str = "caseReferenceNumber";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.CASE_REFERENCE_NUMBER, result);
        }

        @Test
        void shouldConvertClientSurname() {
            String str = "client.clientSurname";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.CLIENT_SURNAME, result);
        }

        @Test
        void shouldConvertSubmissionPeriod() {
            String str = "submission.submissionPeriod";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.SUBMISSION_PERIOD, result);
        }

        @Test
        void shouldConvertCategoryOfLaw() {
            String str = "calculatedFeeDetail.categoryOfLaw";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.CATEGORY_OF_LAW, result);
        }

        @Test
        void shouldConvertScheduleReference() {
            String str = "scheduleReference";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.SCHEDULE_REFERENCE, result);
        }

        @Test
        void shouldConvertEscapeCaseFlag() {
            String str = "calculatedFeeDetail.escapeCaseFlag";
            SortField result = SortField.fromValue(str);
            Assertions.assertEquals(SortField.ESCAPE_CASE_FLAG, result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"foo", "bar"})
        void shouldThrowExceptionForAnythingElse(String str) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> SortField.fromValue(str));
        }
    }
}
