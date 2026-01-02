package uk.gov.justice.laa.amend.claim.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchConfig {
    @JsonProperty("providerAccountNumber")
    private String providerAccountNumber;

    @JsonProperty("submissionMonth")
    private String submissionMonth;

    @JsonProperty("submissionYear")
    private String submissionYear;

    @JsonProperty("ufn")
    private String ufn;

    @JsonProperty("crn")
    private String crn;

    @JsonProperty("expectedResults")
    private boolean expectedResults;

    // Getters
    public String getProviderAccountNumber() { return providerAccountNumber; }
    public String getSubmissionMonth() { return submissionMonth; }
    public String getSubmissionYear() { return submissionYear; }
    public String getUfn() { return ufn; }
    public String getCrn() { return crn; }
    public boolean isExpectedResults() { return expectedResults; }
}