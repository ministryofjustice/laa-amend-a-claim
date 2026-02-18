package uk.gov.justice.laa.amend.claim.forms;

import static org.springframework.util.StringUtils.hasText;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidCaseReferenceNumber;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidProviderAccountNumber;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidUniqueFileNumber;
import uk.gov.justice.laa.amend.claim.models.SearchQuery;
import uk.gov.justice.laa.amend.claim.utils.DateUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidProviderAccountNumber
@ValidSubmissionDate
@ValidUniqueFileNumber
@ValidCaseReferenceNumber
public class SearchForm {

    private String providerAccountNumber;

    private String submissionDateMonth;

    private String submissionDateYear;

    private String uniqueFileNumber;

    private String caseReferenceNumber;

    public SearchForm(SearchQuery query) {
        this.providerAccountNumber = query.getProviderAccountNumber();
        this.submissionDateMonth = query.getSubmissionDateMonth();
        this.submissionDateYear = query.getSubmissionDateYear();
        this.uniqueFileNumber = query.getUniqueFileNumber();
        this.caseReferenceNumber = query.getCaseReferenceNumber();
    }

    public boolean anyNonEmpty() {
        return hasText(providerAccountNumber)
                || hasText(submissionDateMonth)
                || hasText(submissionDateYear)
                || hasText(uniqueFileNumber)
                || hasText(caseReferenceNumber);
    }

    public String getSubmissionPeriod() {
        return DateUtils.toSubmissionPeriod(submissionDateMonth, submissionDateYear);
    }
}
