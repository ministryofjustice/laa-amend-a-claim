package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Claim {
    private String uniqueFileNumber;
    private String submissionId;
    private String claimId;
    private String caseReferenceNumber;
    private String clientSurname;
    private String clientForename;
    private LocalDate dateSubmitted;
    private String account;
    private String type;
    private Boolean escaped;
    private LocalDate caseStartDate;
    private LocalDate caseEndDate;

    private String caseStartDateForDisplay;
    private String caseEndDateForDisplay;

    private String feeScheme;
    private String matterTypeCode;
    private String referenceNumber;
    private String dateSubmittedForDisplay;
    private String categoryOfLaw;
    private long dateSubmittedForSorting;
}
