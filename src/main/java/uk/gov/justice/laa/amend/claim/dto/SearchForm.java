package uk.gov.justice.laa.amend.claim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchForm {
    @NotBlank(message = "{index.providerAccountNumber.error.required}")
    private String providerAccountNumber;
    private Integer submissionDateMonth;
    private Integer submissionDateYear;
    private String referenceNumber;

    public boolean allEmpty() {
        return (providerAccountNumber == null || providerAccountNumber.isBlank())
            && (submissionDateMonth == null)
            && (submissionDateYear == null)
            && (referenceNumber == null || referenceNumber.isBlank());
    }
}
