package uk.gov.justice.laa.payments.amend.controllers.claimdetails;

import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VALID;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VOID;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getValidAssessableClaim;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveAmendedFields;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveClaim;

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
import uk.gov.justice.laa.payments.amend.annotations.HasRoleEscapeCaseCaseworker;
import uk.gov.justice.laa.payments.amend.config.FeatureFlagsConfig;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.service.AssessmentService;
import uk.gov.justice.laa.payments.amend.service.ClaimHistoryService;
import uk.gov.justice.laa.payments.amend.service.ClaimService;
import uk.gov.justice.laa.payments.amend.service.UserRetrievalService;
import uk.gov.justice.laa.payments.amend.viewmodels.claimoverview.ClaimOverviewViewFactory;

@Controller
@Slf4j
public class ClaimSummaryController extends ClaimDetailsBaseController {

  private final ClaimService claimService;
  private final ClaimHistoryService claimHistoryService;

  public ClaimSummaryController(
      AssessmentService assessmentService,
      UserRetrievalService userRetrievalService,
      ClaimService claimService,
      ClaimHistoryService claimHistoryService,
      FeatureFlagsConfig featureFlagsConfig) {
    super(assessmentService, userRetrievalService, featureFlagsConfig);
    this.claimService = claimService;
    this.claimHistoryService = claimHistoryService;
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
    var amendedFields = claimHistoryService.getAmendedFields(claim);

    saveClaim(session, claimId, claim);
    saveAmendedFields(session, claimId, amendedFields);

    setCommonModelAttributes(model, session, request, claim, user);

    model.addAttribute("claim", ClaimOverviewViewFactory.create(claim));
    model.addAttribute("amendedFields", amendedFields);

    return "pages/claimdetails/claim-summary";
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
