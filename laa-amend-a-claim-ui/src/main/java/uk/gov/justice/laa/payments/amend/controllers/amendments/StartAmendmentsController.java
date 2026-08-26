package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getValidClaim;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.payments.amend.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.payments.amend.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.payments.amend.config.features.Feature;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.payments.amend.viewmodels.claimclient.ClaimClientViewFactory;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class StartAmendmentsController {

  @GetMapping
  public String startAmendment(
      HttpSession session, @PathVariable UUID submissionId, @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);

    var clientView = ClaimClientViewFactory.create(claim);
    var client1Form = new AmendmentForm(clientView.client1Rows());
    var caseView = ClaimCaseViewFactory.create(claim);
    var caseTypeForm = new AmendmentForm(caseView.caseTypeRows());
    var caseDetailsForm = new AmendmentForm(caseView.caseDetailsRows());
    var costsView = ClaimCostsViewFactory.create(claim);
    var costsForm = new AmendmentForm(costsView.costRows());
    var requestedByForm = new RequestedByForm();
    var requestedReasonForm = new RequestedReasonForm();

    boolean isMediation = AreaOfLaw.MEDIATION.equals(claim.getAreaOfLaw());
    var client2Form = isMediation ? new AmendmentForm(clientView.client2Rows()) : null;
    var amendmentForms =
        AmendmentForms.builder()
            .client1(client1Form)
            .client2(client2Form)
            .caseType(caseTypeForm)
            .caseDetails(caseDetailsForm)
            .costs(costsForm)
            .requestedBy(requestedByForm)
            .requestedReason(requestedReasonForm)
            .build();

    saveAmendmentForms(session, claimId, amendmentForms);

    return "redirect:/submissions/%s/claims/%s/amendments/requested-by"
        .formatted(submissionId, claimId);
  }
}
