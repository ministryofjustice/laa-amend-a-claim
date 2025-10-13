package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.INDEX_PROVIDER_ACCOUNT_NUMBER_ERROR_INVALID;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.INDEX_PROVIDER_ACCOUNT_NUMBER_ERROR_REQUIRED;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.INDEX_REFERENCE_NUMBER_ERROR_INVALID;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.PROVIDER_ACCOUNT_REGEX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.REF_NUMBER_REGEX;
import static uk.gov.justice.laa.amend.claim.utils.FormUtils.nonEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidSubmissionDate
public class SearchForm {

    @NotBlank(message = INDEX_PROVIDER_ACCOUNT_NUMBER_ERROR_REQUIRED)
    @Pattern(regexp = PROVIDER_ACCOUNT_REGEX, message = INDEX_PROVIDER_ACCOUNT_NUMBER_ERROR_INVALID)
    private String providerAccountNumber;

    private String submissionDateMonth;

    private String submissionDateYear;

    @Pattern(regexp = REF_NUMBER_REGEX, message = INDEX_REFERENCE_NUMBER_ERROR_INVALID)
    private String referenceNumber;

    public boolean anyNonEmpty() {
        return nonEmpty(providerAccountNumber)
            || nonEmpty(submissionDateMonth)
            || nonEmpty(submissionDateYear)
            || nonEmpty(referenceNumber);
    }

    public String getRedirectUrl(int page) {
        String redirectUrl = String.format("/?page=%d", page);
        if (nonEmpty(this.getProviderAccountNumber())) {
            redirectUrl += String.format("&providerAccountNumber=%s", this.getProviderAccountNumber());
        }
        if (nonEmpty(this.getSubmissionDateMonth())) {
            redirectUrl += String.format("&submissionDateMonth=%s", this.getSubmissionDateMonth());
        }
        if (nonEmpty(this.getSubmissionDateYear())) {
            redirectUrl += String.format("&submissionDateYear=%s", this.getSubmissionDateYear());
        }
        if (nonEmpty(this.getReferenceNumber())) {
            redirectUrl += String.format("&referenceNumber=%s", this.getReferenceNumber());
        }
        return redirectUrl;
    }
}
