package uk.gov.justice.laa.amend.claim.models;

public class SearchData {
    private String providerAccountNumber;
    private String submissionMonth;
    private String submissionYear;
    private String ufn;
    private String crn;
    private boolean expectedResults;

    public SearchData() {}

    public String getProviderAccountNumber() {
        return providerAccountNumber;
    }

    public void setProviderAccountNumber(String providerAccountNumber) {
        this.providerAccountNumber = providerAccountNumber;
    }

    public String getSubmissionMonth() {
        return submissionMonth;
    }

    public void setSubmissionMonth(String submissionMonth) {
        this.submissionMonth = submissionMonth;
    }

    public String getSubmissionYear() {
        return submissionYear;
    }

    public void setSubmissionYear(String submissionYear) {
        this.submissionYear = submissionYear;
    }

    public String getUfn() {
        return ufn;
    }

    public void setUfn(String ufn) {
        this.ufn = ufn;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public boolean isExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(boolean expectedResults) {
        this.expectedResults = expectedResults;
    }
}
