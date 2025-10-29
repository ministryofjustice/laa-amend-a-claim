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

        String result = RedirectUrlUtils.getRedirectUrl(form, 1, new Sort("uniqueFileNumber", SortDirection.ASCENDING));

        Assertions.assertEquals("/?providerAccountNumber=123&page=1&sort=uniqueFileNumber,asc", result);
    }

    @Test
    void createRedirectUrlWhenAccountNumberAndDatePresent() {
        SearchForm form = new SearchForm();

        form.setProviderAccountNumber("123");
        form.setSubmissionDateMonth("3");
        form.setSubmissionDateYear("2007");

        String result = RedirectUrlUtils.getRedirectUrl(form, 2, new Sort("uniqueFileNumber", SortDirection.ASCENDING));

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

        String result = RedirectUrlUtils.getRedirectUrl(form, 3, new Sort("uniqueFileNumber", SortDirection.ASCENDING));

        Assertions.assertEquals("/?providerAccountNumber=123&submissionDateMonth=3&submissionDateYear=2007&uniqueFileNumber=456&caseReferenceNumber=789&page=3&sort=uniqueFileNumber,asc", result);
    }
}
