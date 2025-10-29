package uk.gov.justice.laa.amend.claim.utils;

import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.SortDirection;

import static org.springframework.util.StringUtils.hasText;

public class RedirectUrlUtils {

    public static String getRedirectUrl(SearchForm form, int page, Sort sort) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/");

        addQueryParam(builder, "providerAccountNumber", form.getProviderAccountNumber());
        addQueryParam(builder, "submissionDateMonth", form.getSubmissionDateMonth());
        addQueryParam(builder, "submissionDateYear", form.getSubmissionDateYear());
        addQueryParam(builder, "uniqueFileNumber", form.getUniqueFileNumber());
        addQueryParam(builder, "caseReferenceNumber", form.getCaseReferenceNumber());

        addQueryParam(builder, "page", String.valueOf(page));
        addQueryParam(builder, "sort", sort.toString());

        return builder.build().toUriString();
    }

    public static String getRedirectUrl(SearchForm form, String field, SortDirection direction) {
        return getRedirectUrl(form, new Sort(field, direction));
    }

    public static String getRedirectUrl(SearchForm form, Sort sort) {
        return getRedirectUrl(form, 1, sort);
    }

    public static String getRedirectUrl(SearchForm form) {
        return getRedirectUrl(form, new Sort());
    }

    private static void addQueryParam(UriComponentsBuilder builder, String key, String value) {
        if (hasText(value)) {
            builder.queryParam(key, value);
        }
    }
}
