package uk.gov.justice.laa.amend.claim.forms.errors;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.util.DetailedError;

public class SearchFormErrorTest {

    @Test
    void errorsShouldBeSorted() {
        List<DetailedError> errors = List.of(
                new DetailedError("submissionDateMonth", null, null, "message 2"),
                new DetailedError("uniqueFileNumber", null, null, "message 4"),
                new DetailedError("providerAccountNumber", null, null, "message 1"),
                new DetailedError("caseReferenceNumber", null, null, "message 5"),
                new DetailedError("submissionDateYear", null, null, "message 3"));

        List<SearchFormError> result =
                errors.stream().map(SearchFormError::new).sorted().toList();

        List<SearchFormError> expectedResult = List.of(
                new SearchFormError("providerAccountNumber", "message 1"),
                new SearchFormError("submissionDateMonth", "message 2"),
                new SearchFormError("submissionDateYear", "message 3"),
                new SearchFormError("uniqueFileNumber", "message 4"),
                new SearchFormError("caseReferenceNumber", "message 5"));

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void fieldIdsShouldBeCorrect() {
        List<SearchFormError> errors = List.of(
                new SearchFormError("providerAccountNumber", "message 1"),
                new SearchFormError("submissionDateMonth", "message 2"),
                new SearchFormError("submissionDateYear", "message 3"),
                new SearchFormError("uniqueFileNumber", "message 4"),
                new SearchFormError("caseReferenceNumber", "message 5"));

        List<String> result = errors.stream().map(FormError::getFieldId).toList();

        List<String> expectedResult = List.of(
                "provider-account-number",
                "submission-date-month",
                "submission-date-year",
                "unique-file-number",
                "case-reference-number");

        Assertions.assertEquals(expectedResult, result);
    }
}
