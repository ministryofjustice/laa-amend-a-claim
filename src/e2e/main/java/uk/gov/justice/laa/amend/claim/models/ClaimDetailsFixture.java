package uk.gov.justice.laa.amend.claim.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClaimDetailsFixture {

    private boolean addAssessmentOutcomeDisabled;
    private String claimType;
    private String providerAccount;

    private String ufn;

    private boolean hasAssessment;

    private String outcome;

    private Map<String, String[]> values = new LinkedHashMap<>();
    private Map<String, String[]> assessedTotals = new LinkedHashMap<>();
    private Map<String, String[]> allowedTotals = new LinkedHashMap<>();

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getProviderAccount() {
        return providerAccount;
    }

    public void setProviderAccount(String providerAccount) {
        this.providerAccount = providerAccount;
    }

    public String getUfn() {
        return ufn;
    }

    public void setUfn(String ufn) {
        this.ufn = ufn;
    }

    public boolean isHasAssessment() {
        return hasAssessment;
    }

    public void setHasAssessment(boolean hasAssessment) {
        this.hasAssessment = hasAssessment;
    }

    public boolean isAddAssessmentOutcomeDisabled() {
        return addAssessmentOutcomeDisabled;
    }

    public void setAddAssessmentOutcomeDisabled(boolean addAssessmentOutcomeDisabled) {
        this.addAssessmentOutcomeDisabled = addAssessmentOutcomeDisabled;
    }

    public Map<String, String[]> getValues() {
        return values;
    }

    public void setValues(Map<String, String[]> values) {
        this.values = values;
    }

    public Map<String, String[]> getAssessedTotals() {
        return assessedTotals;
    }

    public void setAssessedTotals(Map<String, String[]> assessedTotals) {
        this.assessedTotals = assessedTotals;
    }

    public Map<String, String[]> getAllowedTotals() {
        return allowedTotals;
    }

    public void setAllowedTotals(Map<String, String[]> allowedTotals) {
        this.allowedTotals = allowedTotals;
    }

    @Override
    public String toString() {
        return claimType;
    }
}
