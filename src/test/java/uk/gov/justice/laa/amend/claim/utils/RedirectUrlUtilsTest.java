package uk.gov.justice.laa.amend.claim.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.util.DetailedError;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.errors.SearchFormError;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.SortDirection;

import java.util.List;
import java.util.stream.Stream;

public class RedirectUrlUtilsTest {

    @Test
    void createRedirectUrlWhenAccountNumberPresent() {
        SearchForm form = new SearchForm();
        form.setProviderAccountNumber("123");

        Sort sort = Sort.builder().field("uniqueFileNumber").direction(SortDirection.ASCENDING).build();
        String result = RedirectUrlUtils.getRedirectUrl(form, 1, sort);

        Assertions.assertEquals("/?providerAccountNumber=123&page=1&sort=uniqueFileNumber,asc", result);
    }

    @Test
    void createRedirectUrlWhenAccountNumberAndDatePresent() {
        SearchForm form = new SearchForm();

        form.setProviderAccountNumber("123");
        form.setSubmissionDateMonth("3");
        form.setSubmissionDateYear("2007");

        Sort sort = Sort.builder().field("uniqueFileNumber").direction(SortDirection.ASCENDING).build();
        String result = RedirectUrlUtils.getRedirectUrl(form, 2, sort);

        Assertions.assertEquals("/?providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007&page=2&sort=uniqueFileNumber,asc", result);
    }

    @Test
    void createRedirectUrlWhenAllPresent() {
        SearchForm form = new SearchForm();

        form.setProviderAccountNumber("123");
        form.setSubmissionDateMonth("3");
        form.setSubmissionDateYear("2007");
        form.setUniqueFileNumber("456");
        form.setCaseReferenceNumber("789");

        Sort sort = Sort.builder().field("uniqueFileNumber").direction(SortDirection.ASCENDING).build();
        String result = RedirectUrlUtils.getRedirectUrl(form, 3, sort);

        Assertions.assertEquals("/?providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007&uniqueFileNumber=456&caseReferenceNumber=789&page=3&sort=uniqueFileNumber,asc", result);
    }
}
