package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;

import java.util.List;
import java.util.stream.Stream;

public class ThymeleafUtilsTest {

    @Nested
    class toSearchFormErrorsTests {
        @Test
        void sortErrorsByFieldOrder() {
            List<DetailedError> errors = List.of(
                new DetailedError(
                    "submissionDateYear",
                    null,
                    Stream.empty().toArray(),
                    "Submission date year error"
                ),
                new DetailedError(
                    "uniqueFileNumber",
                    null,
                    Stream.empty().toArray(),
                    "Unique file number error"
                ),
                new DetailedError(
                    "providerAccountNumber",
                    null,
                    Stream.empty().toArray(),
                    "Provider account number error"
                ),
                new DetailedError(
                    "submissionDateMonth",
                    null,
                    Stream.empty().toArray(),
                    "Submission date month error"
                ),
                new DetailedError(
                    "caseReferenceNumber",
                    null,
                    Stream.empty().toArray(),
                    "Case reference number error"
                )
            );

            ThymeleafUtils sut = new ThymeleafUtils();

            List<SearchFormError> result = sut.toSearchFormErrors(errors);

            List<SearchFormError> expectedResult = List.of(
                new SearchFormError("providerAccountNumber", "Provider account number error"),
                new SearchFormError("submissionDateMonth", "Submission date month error"),
                new SearchFormError("submissionDateYear", "Submission date year error"),
                new SearchFormError("uniqueFileNumber", "Unique file number error"),
                new SearchFormError("caseReferenceNumber", "Case reference number error")
            );

            Assertions.assertEquals(expectedResult, result);
        }

        @Test
        void filterOutDuplicateErrorMessages() {
            List<DetailedError> errors = List.of(
                new DetailedError(
                    "submissionDateYear",
                    null,
                    Stream.empty().toArray(),
                    "The submission date must be a real date"
                ),
                new DetailedError(
                    "submissionDateMonth",
                    null,
                    Stream.empty().toArray(),
                    "The submission date must be a real date"
                )
            );

            ThymeleafUtils sut = new ThymeleafUtils();

            List<SearchFormError> result = sut.toSearchFormErrors(errors);

            List<SearchFormError> expectedResult = List.of(
                new SearchFormError("submissionDateMonth", "The submission date must be a real date")
            );

            Assertions.assertEquals(expectedResult, result);
        }
    }
}
