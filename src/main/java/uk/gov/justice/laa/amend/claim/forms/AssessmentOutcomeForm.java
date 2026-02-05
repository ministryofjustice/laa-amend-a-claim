package uk.gov.justice.laa.amend.claim.forms;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_OUTCOME_REQUIRED_ERROR;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.LIABILITY_FOR_VAT_REQUIRED_ERROR;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

@Setter
@Getter
public class AssessmentOutcomeForm {

    @NotNull(message = ASSESSMENT_OUTCOME_REQUIRED_ERROR)
    private OutcomeType assessmentOutcome;

    @NotNull(message = LIABILITY_FOR_VAT_REQUIRED_ERROR)
    private Boolean liabilityForVat;
}
