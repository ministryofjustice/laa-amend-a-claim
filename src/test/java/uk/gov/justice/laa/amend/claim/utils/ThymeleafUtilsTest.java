package uk.gov.justice.laa.amend.claim.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessedTotalFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessmentOutcomeFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafString;

public class ThymeleafUtilsTest {

    @Nested
    class ToSearchFormErrorsTests {
        @Test
        void sortErrorsByFieldOrder() {
            List<DetailedError> errors = List.of(
                    new DetailedError(
                            "submissionDateYear", null, Stream.empty().toArray(), "Submission date year error"),
                    new DetailedError("uniqueFileNumber", null, Stream.empty().toArray(), "Unique file number error"),
                    new DetailedError("officeCode", null, Stream.empty().toArray(), "Office code error"),
                    new DetailedError(
                            "submissionDateMonth", null, Stream.empty().toArray(), "Submission date month error"),
                    new DetailedError(
                            "caseReferenceNumber", null, Stream.empty().toArray(), "Case reference number error"));

            ThymeleafUtils sut = new ThymeleafUtils();

            List<SearchFormError> result = sut.toSearchFormErrors(errors);

            List<SearchFormError> expectedResult = List.of(
                    new SearchFormError("officeCode", "Office code error"),
                    new SearchFormError("submissionDateMonth", "Submission date month error"),
                    new SearchFormError("submissionDateYear", "Submission date year error"),
                    new SearchFormError("uniqueFileNumber", "Unique file number error"),
                    new SearchFormError("caseReferenceNumber", "Case reference number error"));

            Assertions.assertEquals(expectedResult, result);
        }

        @Test
        void filterOutDuplicateErrorMessages() {
            List<DetailedError> errors = List.of(
                    new DetailedError(
                            "submissionDateYear",
                            null,
                            Stream.empty().toArray(),
                            "The submission date must be a real date"),
                    new DetailedError(
                            "submissionDateMonth",
                            null,
                            Stream.empty().toArray(),
                            "The submission date must be a real date"));

            ThymeleafUtils sut = new ThymeleafUtils();

            List<SearchFormError> result = sut.toSearchFormErrors(errors);

            List<SearchFormError> expectedResult =
                    List.of(new SearchFormError("submissionDateMonth", "The submission date must be a real date"));

            Assertions.assertEquals(expectedResult, result);
        }
    }

    @Nested
    class ToAssessmentOutcomeErrorsTests {
        @Test
        void sortErrorsByFieldOrder() {
            List<DetailedError> errors = List.of(
                    new DetailedError("assessmentOutcome", null, Stream.empty().toArray(), "Assessment outcome error"),
                    new DetailedError(
                            "liabilityForVat", null, Stream.empty().toArray(), "liability for VAT number error"));

            ThymeleafUtils sut = new ThymeleafUtils();

            List<AssessmentOutcomeFormError> result = sut.toAssessmentOutcomeErrors(errors);

            List<AssessmentOutcomeFormError> expectedResult = List.of(
                    new AssessmentOutcomeFormError("assessmentOutcome", "Assessment outcome error"),
                    new AssessmentOutcomeFormError("liabilityForVat", "liability for VAT number error"));

            Assertions.assertEquals(expectedResult, result);
        }
    }

    @Nested
    class ToAssessedTotalFormErrorsTests {
        @Test
        void sortErrorsByFieldOrder() {
            List<DetailedError> errors = List.of(
                    new DetailedError(
                            "assessedTotalInclVat", null, Stream.empty().toArray(), "foo"),
                    new DetailedError("assessedTotalVat", null, Stream.empty().toArray(), "bar"));

            ThymeleafUtils sut = new ThymeleafUtils();

            List<AssessedTotalFormError> result = sut.toAssessedTotalFormErrors(errors);

            List<AssessedTotalFormError> expectedResult = List.of(
                    new AssessedTotalFormError("assessedTotalVat", "bar"),
                    new AssessedTotalFormError("assessedTotalInclVat", "foo"));

            Assertions.assertEquals(expectedResult, result);
        }
    }

    @Nested
    class GetFormattedValueTests {

        @Test
        void formatsOffsetDateTimeInLondonTimeZoneDuringGmt() {
            // December is GMT (UTC+0), so London time == UTC time
            OffsetDateTime utcDateTime = OffsetDateTime.of(LocalDateTime.of(2025, 12, 18, 16, 11, 27), ZoneOffset.UTC);
            ThymeleafUtils sut = new ThymeleafUtils();

            ThymeleafString result = sut.getFormattedValue(utcDateTime);

            Assertions.assertInstanceOf(ThymeleafMessage.class, result);
            ThymeleafMessage message = (ThymeleafMessage) result;
            Assertions.assertEquals("fulldate.format", message.getKey());
            Assertions.assertEquals("18 December 2025", message.getParams()[0]);
            Assertions.assertEquals("16:11:27", message.getParams()[1]);
        }

        @Test
        void formatsOffsetDateTimeInLondonTimeZoneDuringBst() {
            // June is BST (UTC+1): UTC 14:30:00 = London 15:30:00
            OffsetDateTime utcDateTime = OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC);
            ThymeleafUtils sut = new ThymeleafUtils();

            ThymeleafString result = sut.getFormattedValue(utcDateTime);

            Assertions.assertInstanceOf(ThymeleafMessage.class, result);
            ThymeleafMessage message = (ThymeleafMessage) result;
            Assertions.assertEquals("fulldate.format", message.getKey());
            Assertions.assertEquals("15 June 2025", message.getParams()[0]);
            Assertions.assertEquals("15:30:00", message.getParams()[1]);
        }
    }
}
