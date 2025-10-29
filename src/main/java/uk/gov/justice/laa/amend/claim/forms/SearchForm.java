package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;
import uk.gov.justice.laa.amend.claim.models.SortDirection;
import uk.gov.justice.laa.amend.claim.models.Sort;

import static org.springframework.util.StringUtils.hasText;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.CASE_REFERENCE_NUMBER_INVALID_ERROR;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.CASE_REFERENCE_NUMBER_REGEX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.PROVIDER_ACCOUNT_NUMBER_INVALID_ERROR;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.PROVIDER_ACCOUNT_NUMBER_REGEX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.PROVIDER_ACCOUNT_NUMBER_REQUIRED_ERROR;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.UNIQUE_FILE_NUMBER_INVALID_ERROR;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.UNIQUE_FILE_NUMBER_REGEX;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidSubmissionDate
public class SearchForm {

    @NotBlank(message = PROVIDER_ACCOUNT_NUMBER_REQUIRED_ERROR)
    @Pattern(regexp = PROVIDER_ACCOUNT_NUMBER_REGEX, message = PROVIDER_ACCOUNT_NUMBER_INVALID_ERROR)
    private String providerAccountNumber;

    private String submissionDateMonth;

    private String submissionDateYear;

    @Pattern(regexp = UNIQUE_FILE_NUMBER_REGEX, message = UNIQUE_FILE_NUMBER_INVALID_ERROR)
    private String uniqueFileNumber;

    @Pattern(regexp = CASE_REFERENCE_NUMBER_REGEX, message = CASE_REFERENCE_NUMBER_INVALID_ERROR)
    private String caseReferenceNumber;

    public boolean anyNonEmpty() {
        return hasText(providerAccountNumber)
            || hasText(submissionDateMonth)
            || hasText(submissionDateYear)
            || hasText(uniqueFileNumber)
            || hasText(caseReferenceNumber);
    }

    public String getRedirectUrl(int page, Sort sort) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/");

        addQueryParam(builder, "providerAccountNumber", providerAccountNumber);
        addQueryParam(builder, "submissionDateMonth", submissionDateMonth);
        addQueryParam(builder, "submissionDateYear", submissionDateYear);
        addQueryParam(builder, "uniqueFileNumber", uniqueFileNumber);
        addQueryParam(builder, "caseReferenceNumber", caseReferenceNumber);

        addQueryParam(builder, "page", String.valueOf(page));
        addQueryParam(builder, "sort", sort.toString());

        return builder.build().toUriString();
    }

    public String getRedirectUrl(String field, SortDirection direction) {
        return getRedirectUrl(1, new Sort(field, direction));
    }

    private void addQueryParam(UriComponentsBuilder builder, String key, String value) {
        if (hasText(value)) {
            builder.queryParam(key, value);
        }
    }
}
