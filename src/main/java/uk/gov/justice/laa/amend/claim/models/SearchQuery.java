package uk.gov.justice.laa.amend.claim.models;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;

@Getter
@Setter
@NoArgsConstructor
public class SearchQuery {

    @Min(1)
    private int page = 1;

    private Sort sort;
    private String providerAccountNumber;
    private String submissionDateMonth;
    private String submissionDateYear;
    private String uniqueFileNumber;
    private String caseReferenceNumber;

    public SearchQuery(SearchForm form, Sort sort) {
        this.sort = sort;
        this.providerAccountNumber = form.getProviderAccountNumber();
        this.submissionDateMonth = form.getSubmissionDateMonth();
        this.submissionDateYear = form.getSubmissionDateYear();
        this.uniqueFileNumber = form.getUniqueFileNumber();
        this.caseReferenceNumber = form.getCaseReferenceNumber();
    }

    public void rejectUnknownParams(HttpServletRequest request) {
        Set<String> allowed = Arrays.stream(this.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        request.getParameterMap().keySet().forEach(param -> {
            if (!allowed.contains(param)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown query parameter: " + param);
            }
        });
    }

    public String getRedirectUrl() {
        return getRedirectUrl(page, sort);
    }

    public String getRedirectUrl(SortField field, SortDirection direction) {
        return getRedirectUrl(1, new Sort(field, direction));
    }

    public String getRedirectUrl(Sort sort) {
        return getRedirectUrl(page, sort);
    }

    private String getRedirectUrl(int page, Sort sort) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/");

        addQueryParam(builder, "providerAccountNumber", providerAccountNumber);
        addQueryParam(builder, "submissionDateMonth", submissionDateMonth);
        addQueryParam(builder, "submissionDateYear", submissionDateYear);
        addQueryParam(builder, "uniqueFileNumber", uniqueFileNumber);
        addQueryParam(builder, "caseReferenceNumber", caseReferenceNumber);

        addQueryParam(builder, "page", String.valueOf(page));
        addQueryParam(builder, "sort", Objects.toString(sort, null));

        return builder.build().toUriString();
    }

    private void addQueryParam(UriComponentsBuilder builder, String key, String value) {
        if (hasText(value)) {
            builder.queryParam(key, value);
        }
    }
}
