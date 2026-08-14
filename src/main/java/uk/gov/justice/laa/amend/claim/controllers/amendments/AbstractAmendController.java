package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.GenericAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.forms.validators.AmendmentFormValidator;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.AmendmentsHeaderView;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

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

  protected static void requireEditable(ClaimViewField<?> field, ClaimDetails claim) {
    if (!field.isEditable(AmendmentsHeaderView.isAssessed(claim))) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }

  protected static Set<ClaimViewField<?>> lockedFields(
      Set<? extends ClaimViewField<?>> rowFields, ClaimDetails claim) {
    var claimIsAssessed = AmendmentsHeaderView.isAssessed(claim);
    return rowFields.stream()
        .map(field -> (ClaimViewField<?>) field)
        .filter(field -> !field.isEditable(claimIsAssessed))
        .collect(Collectors.toSet());
  }

  protected static AmendmentForm retainLockedInputs(
      AmendmentForm submitted, AmendmentForm original, Set<ClaimViewField<?>> lockedFields) {
    var lockedKeys =
        lockedFields.stream()
            .flatMap(field -> AmendmentForm.inputKeys(field).stream())
            .collect(Collectors.toSet());

    var merged = new AmendmentForm(original);
    submitted.getInputs().entrySet().stream()
        .filter(entry -> original.getInputs().containsKey(entry.getKey()))
        .filter(entry -> !lockedKeys.contains(entry.getKey()))
        .forEach(entry -> merged.getInputs().put(entry.getKey(), entry.getValue()));
    return merged;
  }
}
