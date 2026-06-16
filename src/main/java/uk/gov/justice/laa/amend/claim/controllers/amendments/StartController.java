package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidAmendableClaim;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.forms.amendment.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendment.AmendmentForms;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@HasRoleClaimAmendmentsCaseworker
@RequiredArgsConstructor
public class StartController {

  private final FeatureFlagsConfig featureFlagsConfig;

  @GetMapping
  public String startAmendment(
      HttpSession session, @PathVariable UUID submissionId, @PathVariable UUID claimId) {
    featureFlagsConfig.checkClaimAmendmentEnabled();

    var claim = getValidAmendableClaim(session, submissionId, claimId);

    var clientView = ClaimClientViewFactory.create(claim);
    var client1Form = new AmendmentForm(clientView.client1Rows());
    var amendmentForms = new AmendmentForms(client1Form);

    saveAmendmentForms(session, claimId, amendmentForms);

    return "redirect:/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }
}
