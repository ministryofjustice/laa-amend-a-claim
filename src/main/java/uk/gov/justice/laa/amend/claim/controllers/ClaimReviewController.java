package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_ID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ClaimReviewController {

    private final AssessmentService assessmentService;
    private final ClaimStatusHandler claimStatusHandler;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/review")
    public String onPageLoad(
            HttpServletRequest request,
            Model model,
            @PathVariable(value = "submissionId") UUID submissionId,
            @PathVariable(value = "claimId") UUID claimId) {
        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId.toString());

        if (claim.getAssessmentOutcome() == null) {
            return String.format("redirect:/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);
        }
        return renderView(model, claim, submissionId, claimId);
    }

    @PostMapping("/submissions/{submissionId}/claims/{claimId}/review")
    public String submit(
            HttpServletRequest request,
            HttpSession session,
            Model model,
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable(value = "submissionId") UUID submissionId,
            @PathVariable(value = "claimId") UUID claimId,
            HttpServletResponse response) {
        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId.toString());
        ClaimDetailsView<? extends ClaimDetails> viewModel = claim.toViewModel();
        if (viewModel.getErrors().isEmpty()) {
            String userId = oidcUser.getClaim("oid");
            try {
                CreateAssessment201Response result = assessmentService.submitAssessment(claim, UUID.fromString(userId));
                session.removeAttribute(claimId.toString());
                UUID assessmentId = result.getId();
                session.setAttribute(ASSESSMENT_ID, assessmentId);
                return String.format(
                        "redirect:/submissions/%s/claims/%s/assessments/%s", submissionId, claimId, assessmentId);
            } catch (Exception e) {
                log.error("Failed to submit assessment for claim ID: {}", claimId, e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return renderView(model, claim, viewModel, submissionId, claimId, true, false);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return renderView(model, claim, viewModel, submissionId, claimId, false, true);
        }
    }

    private String renderView(Model model, ClaimDetails claim, UUID submissionId, UUID claimId) {
        return renderView(model, claim, claim.toViewModel(), submissionId, claimId, false, false);
    }

    private String renderView(
            Model model,
            ClaimDetails claim,
            ClaimDetailsView<? extends ClaimDetails> viewModel,
            UUID submissionId,
            UUID claimId,
            boolean submissionFailed,
            boolean validationFailed) {
        // Populate with the right status of Claim fields based on outcome status and assessed values
        claimStatusHandler.updateFieldStatuses(claim, claim.getAssessmentOutcome());
        model.addAttribute("claim", claim);
        model.addAttribute("viewModel", viewModel);
        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("submissionFailed", submissionFailed);
        model.addAttribute("validationFailed", validationFailed);

        return "review-and-amend";
    }
}
