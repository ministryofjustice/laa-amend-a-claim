package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.GenericAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.validators.AmendmentFormValidator;

@RequiredArgsConstructor
public abstract class AbstractAmendController {

  private final List<GenericAmendmentFieldValidator> genericAmendmentFieldValidators;
  private final List<FieldSpecificAmendmentValidator> fieldSpecificAmendmentValidators;

  protected void initBinder(
      WebDataBinder binder, HttpSession session, UUID submissionId, UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    binder.addValidators(
        new AmendmentFormValidator(
            claim, genericAmendmentFieldValidators, fieldSpecificAmendmentValidators));
  }
}
