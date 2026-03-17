package uk.gov.justice.laa.amend.claim.forms;

import static org.springframework.util.StringUtils.hasText;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidCaseReferenceNumber;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidOfficeCode;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidUniqueFileNumber;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.SearchQuery;
import uk.gov.justice.laa.amend.claim.utils.DateUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidOfficeCode
@ValidSubmissionDate
@ValidUniqueFileNumber
@ValidCaseReferenceNumber
public class SearchForm {

    private String officeCode;

    private String submissionDateMonth;

    private String submissionDateYear;

    private String uniqueFileNumber;

    private String caseReferenceNumber;

    private AreaOfLaw areaOfLaw;

    private Boolean escapeCase;

    public SearchForm(SearchQuery query) {
        this.officeCode = query.getOfficeCode();
        this.submissionDateMonth = query.getSubmissionDateMonth();
        this.submissionDateYear = query.getSubmissionDateYear();
        this.uniqueFileNumber = query.getUniqueFileNumber();
        this.caseReferenceNumber = query.getCaseReferenceNumber();
        this.areaOfLaw = query.getAreaOfLaw();
        this.escapeCase = query.getEscapeCase();
    }

    public boolean anyNonEmpty() {
        return hasText(officeCode)
                || hasText(submissionDateMonth)
                || hasText(submissionDateYear)
                || hasText(uniqueFileNumber)
                || hasText(caseReferenceNumber)
                || areaOfLaw != null
                || escapeCase != null;
    }

    public String getSubmissionPeriod() {
        return DateUtils.toSubmissionPeriod(submissionDateMonth, submissionDateYear);
    }
}
