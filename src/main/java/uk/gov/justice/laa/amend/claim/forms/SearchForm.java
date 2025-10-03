package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidSubmissionDate
public class SearchForm {
    @NotBlank(message = "{index.providerAccountNumber.error.required}")
    private String providerAccountNumber;
    private String submissionDateMonth;
    private String submissionDateYear;
    private String referenceNumber;

    public boolean allEmpty() {
        return (providerAccountNumber == null || providerAccountNumber.isBlank())
            && (submissionDateMonth == null)
            && (submissionDateYear == null)
            && (referenceNumber == null || referenceNumber.isBlank());
    }
}
