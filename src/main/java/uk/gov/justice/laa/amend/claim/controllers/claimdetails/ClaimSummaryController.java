package uk.gov.justice.laa.amend.claim.controllers.claimdetails;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidAssessableClaim;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VALID;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VOID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.EnumSet;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleEscapeCaseCaseworker;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

@Controller
@Slf4j
public class ClaimSummaryController extends ClaimDetailsBaseController {

  private final ClaimService claimService;

  public ClaimSummaryController(
      AssessmentService assessmentService,
      UserRetrievalService userRetrievalService,
      ClaimService claimService,
      FeatureFlagsConfig featureFlagsConfig) {
    super(assessmentService, userRetrievalService, featureFlagsConfig);
    this.claimService = claimService;
  }

  @GetMapping("/submissions/{submissionId}/claims/{claimId}")
  public String onPageLoad(
      HttpServletRequest request,
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    ClaimDetails claim = claimService.getClaimDetails(submissionId, claimId);
    if (!EnumSet.of(VALID, VOID).contains(claim.getStatus())) {
      log.error(
          "Cannot view claim {} as it has an invalid status {}. Returning 404.",
          claimId,
          claim.getStatus());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    var user = setLatestAssessment(claim);

    session.setAttribute(claimId.toString(), claim);

    setCommonModelAttributes(model, session, request, claim, user);

    model.addAttribute("claim", claim.toViewModel());

    return "claimdetails/claim-summary";
  }

  @HasRoleEscapeCaseCaseworker
  @PostMapping("/submissions/{submissionId}/claims/{claimId}")
  public String onSubmit(
      @PathVariable UUID submissionId, @PathVariable UUID claimId, HttpSession session) {
    ClaimDetails claim = getValidAssessableClaim(session, submissionId, claimId);

    if (claim.isHasAssessment()) {
      return String.format("redirect:/submissions/%s/claims/%s/review", submissionId, claimId);
    }

    return String.format(
        "redirect:/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);
  }
}
