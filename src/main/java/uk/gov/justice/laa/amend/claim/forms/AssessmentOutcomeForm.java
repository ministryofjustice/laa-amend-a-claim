package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_OUTCOME_REQUIRED_ERROR;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.LIABILITY_FOR_VAT_REQUIRED_ERROR;

@Setter
@Getter
public class AssessmentOutcomeForm {

    @NotBlank(message = ASSESSMENT_OUTCOME_REQUIRED_ERROR)
    private String assessmentOutcome;

    @NotBlank(message = LIABILITY_FOR_VAT_REQUIRED_ERROR)
    private String liabilityForVAT;

}
