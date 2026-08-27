package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules.model.RuleDto;
import uk.gov.justice.laa.amend.claim.forms.validators.Validator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

public class MandatoryValueValidator extends Validator {

  public boolean isValid(ClaimDetails claimDetails, String value, RuleDto rule) {

    if (rule.areasOfLaw().contains(claimDetails.getAreaOfLaw().name())) {
      return value != null && !value.isBlank();
    } else {
      return true;
    }
  }
}
