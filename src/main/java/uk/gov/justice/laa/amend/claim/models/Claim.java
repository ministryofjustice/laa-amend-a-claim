package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PERIOD_FORMAT;

@Data
public class Claim {
    private String uniqueFileNumber;
    private String submissionId;
    private String claimId;
    private String caseReferenceNumber;
    private String clientSurname;
    private String clientForename;
    private YearMonth submissionPeriod;
    private LocalDate caseStartDate;
    private LocalDate caseEndDate;
    private String feeScheme;
    private String categoryOfLaw;
    private String matterTypeCode;
    private String scheduleReference;
    private Boolean escaped;
    private String providerAccountNumber;

    public String getAccountNumber() {
        return scheduleReference != null ? scheduleReference.split("/")[0] : null;
    }

    public String getReferenceNumber() {
        return uniqueFileNumber != null ? uniqueFileNumber : caseReferenceNumber;
    }

    public String getCaseStartDateForDisplay() {
        return caseStartDate != null ? caseStartDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    public String getCaseEndDateForDisplay() {
        return caseEndDate != null ? caseEndDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    public String getSubmissionPeriodForDisplay() {
        return submissionPeriod != null ? submissionPeriod.format(DateTimeFormatter.ofPattern(DEFAULT_PERIOD_FORMAT)) : null;
    }

    public long getSubmissionPeriodForSorting() {
        return submissionPeriod != null ? submissionPeriod.atDay(1).toEpochDay() : 0;
    }

    public String getEscapedForDisplay() {
        return (escaped != null && escaped) ? "index.results.escaped.yes" : "index.results.escaped.no";
    }
}
