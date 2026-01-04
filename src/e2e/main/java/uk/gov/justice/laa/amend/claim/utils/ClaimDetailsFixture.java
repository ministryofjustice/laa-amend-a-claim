package uk.gov.justice.laa.amend.claim.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClaimDetailsFixture {

    private boolean addAssessmentOutcomeDisabled;

    private Map<String, String[]> values = new LinkedHashMap<>();

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
}