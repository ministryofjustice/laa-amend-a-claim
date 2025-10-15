package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Claim {
    private String uniqueFileNumber;
    private String caseReferenceNumber;
    private String clientSurname;
    private LocalDate dateSubmitted;
    private String account;
    private String type;
    private Boolean escaped;

    private String referenceNumber;
    private String dateSubmittedForDisplay;
    private long dateSubmittedForSorting;
    private ClaimType status;
}
