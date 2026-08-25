package uk.gov.justice.laa.payments.amend.forms;

import static uk.gov.justice.laa.payments.amend.constants.AmendClaimConstants.ASSESSMENT_OUTCOME_REQUIRED_ERROR;
import static uk.gov.justice.laa.payments.amend.constants.AmendClaimConstants.CONTINGENCY_ASSESSMENT_REQUIRED_ERROR;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;

@Setter
@Getter
public class AssessmentOutcomeForm {

  @NotNull(message = ASSESSMENT_OUTCOME_REQUIRED_ERROR)
  private OutcomeType assessmentOutcome;

  @NotNull(message = CONTINGENCY_ASSESSMENT_REQUIRED_ERROR)
  private Boolean contingencyAssessment;
}
