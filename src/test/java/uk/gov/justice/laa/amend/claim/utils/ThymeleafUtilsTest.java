package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessedTotalFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.AssessmentOutcomeFormError;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;

import java.util.List;
import java.util.stream.Stream;

public class ThymeleafUtilsTest {

    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        this.messageSource = messageSource;
    }

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

            ThymeleafUtils sut = new ThymeleafUtils(messageSource);

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

            ThymeleafUtils sut = new ThymeleafUtils(messageSource);

            List<SearchFormError> result = sut.toSearchFormErrors(errors);

            List<SearchFormError> expectedResult = List.of(
                new SearchFormError("submissionDateMonth", "The submission date must be a real date")
            );

            Assertions.assertEquals(expectedResult, result);
        }
    }

    @Nested
    class toAssessmentOutcomeErrorsTests {
        @Test
        void sortErrorsByFieldOrder() {
            List<DetailedError> errors = List.of(
                    new DetailedError(
                            "assessmentOutcome",
                            null,
                            Stream.empty().toArray(),
                            "Assessment outcome error"
                    ),
                    new DetailedError(
                            "liabilityForVat",
                            null,
                            Stream.empty().toArray(),
                            "liability for VAT number error"
                    )
            );

            ThymeleafUtils sut = new ThymeleafUtils(messageSource);

            List<AssessmentOutcomeFormError> result = sut.toAssessmentOutcomeErrors(errors);

            List<AssessmentOutcomeFormError> expectedResult = List.of(
                    new AssessmentOutcomeFormError("assessmentOutcome", "Assessment outcome error"),
                    new AssessmentOutcomeFormError("liabilityForVat", "liability for VAT number error")
            );

            Assertions.assertEquals(expectedResult, result);
        }
    }

    @Nested
    class ToAssessedTotalFormErrorsTests {
        @Test
        void sortErrorsByFieldOrder() {
            List<DetailedError> errors = List.of(
                new DetailedError(
                    "assessedTotalInclVat",
                    null,
                    Stream.empty().toArray(),
                    "foo"
                ),
                new DetailedError(
                    "assessedTotalVat",
                    null,
                    Stream.empty().toArray(),
                    "bar"
                )
            );

            ThymeleafUtils sut = new ThymeleafUtils(messageSource);

            List<AssessedTotalFormError> result = sut.toAssessedTotalFormErrors(errors);

            List<AssessedTotalFormError> expectedResult = List.of(
                new AssessedTotalFormError("assessedTotalVat", "bar"),
                new AssessedTotalFormError("assessedTotalInclVat", "foo")
            );

            Assertions.assertEquals(expectedResult, result);
        }
    }
}
