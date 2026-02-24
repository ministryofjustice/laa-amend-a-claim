package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

@Data
public class SearchData {
    private String providerAccountNumber;
    private String submissionMonth;
    private String submissionYear;
    private String ufn;
    private String crn;
    private String areaOfLaw;
    private String escapeCase;
    private boolean expectedResults;
}
