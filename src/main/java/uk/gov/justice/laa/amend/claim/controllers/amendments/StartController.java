package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class StartController {

  @GetMapping
  public String startAmendment(
      HttpSession session, @PathVariable UUID submissionId, @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);

    var clientView = ClaimClientViewFactory.create(claim);
    var client1Form = new AmendmentForm(clientView.client1Rows());
    var caseView = ClaimCaseViewFactory.create(claim);
    var caseTypeForm = new AmendmentForm(caseView.caseTypeRows());
    var caseDetailsForm = new AmendmentForm(caseView.caseDetailsRows());
    var amendmentForms = new AmendmentForms(client1Form, caseTypeForm, caseDetailsForm);

    saveAmendmentForms(session, claimId, amendmentForms);

    return "redirect:/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }
}
