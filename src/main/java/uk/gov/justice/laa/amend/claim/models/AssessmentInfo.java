package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class AssessmentInfo implements Serializable {
    private OffsetDateTime lastAssessmentDate;
    private String lastAssessedBy;
    private OutcomeType lastAssessmentOutcome;
}
