package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;

import static uk.gov.justice.laa.amend.claim.forms.helpers.StringUtils.isEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidSubmissionDate
public class SearchForm {

    @NotBlank(message = "{index.providerAccountNumber.error.required}")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "{index.providerAccountNumber.error.invalid}")
    private String providerAccountNumber;

    private String submissionDateMonth;

    private String submissionDateYear;

    @Pattern(regexp = "^[a-zA-Z0-9/.\\-\\s]*$", message = "{index.referenceNumber.error.invalid}")
    private String referenceNumber;

    public boolean allEmpty() {
        return isEmpty(providerAccountNumber)
            && isEmpty(submissionDateMonth)
            && isEmpty(submissionDateYear)
            && isEmpty(referenceNumber);
    }
}
