package uk.gov.justice.laa.amend.claim.controllers.claimdetails;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getClaim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.config.InquestConfig;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.forms.InquestForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.InquestDataService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

@Controller
@RequiresFeatureFlag(Feature.INQUEST_TAB)
public class ClaimInquestController extends ClaimDetailsBaseController {

  private final InquestDataService inquestDataService;

  public ClaimInquestController(
      AssessmentService assessmentService,
      UserRetrievalService userRetrievalService,
      FeatureFlagsConfig featureFlagsConfig,
      InquestConfig inquestConfig,
      InquestDataService inquestDataService) {
    super(assessmentService, userRetrievalService, featureFlagsConfig, inquestConfig);
    this.inquestDataService = inquestDataService;
  }

  @GetMapping("/submissions/{submissionId}/claims/{claimId}/inquest")
  public String onPageLoad(
      HttpServletRequest request,
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    ClaimDetails claim = requireInquestEligibleClaim(session, submissionId, claimId);

    InquestForm form =
        inquestDataService.get(claimId).map(InquestForm::from).orElseGet(InquestForm::new);
    model.addAttribute("form", form);
    model.addAttribute("departments", inquestDataService.getInquestDepartments());

    var user = setLatestAssessment(claim);
    setCommonModelAttributes(model, session, request, claim, user);

    return "claimdetails/claim-inquest";
  }

  private ClaimDetails requireInquestEligibleClaim(
      HttpSession session, UUID submissionId, UUID claimId) {
    ClaimDetails claim = getClaim(session, submissionId, claimId);
    if (!isInquestEligible(claim)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim is not inquest-eligible");
    }
    return claim;
  }
}
