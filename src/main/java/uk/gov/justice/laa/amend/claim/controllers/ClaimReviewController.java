package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

@Controller
@RequiredArgsConstructor
public class ClaimReviewController {

    private final AssessmentService assessmentService;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/review")
    public String onPageLoad(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        ClaimDetails claim = (ClaimDetails) session.getAttribute(claimId);

        if (claim == null) {
            return String.format("redirect:/submissions/%s/claims/%s", submissionId, claimId);
        }

        return renderView(model, claim, submissionId, claimId, false);
    }

    @PostMapping("/submissions/{submissionId}/claims/{claimId}/review/discard")
    public String discard(
        HttpSession session,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        // Clear session data
        session.removeAttribute(claimId);

        return String.format("redirect:/submissions/%s/claims/%s", submissionId, claimId);
    }

    @PostMapping("/submissions/{submissionId}/claims/{claimId}/review")
    public String submit(
        HttpSession session,
        Model model,
        @AuthenticationPrincipal OidcUser oidcUser,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        HttpServletResponse response
    ) {
        ClaimDetails claim = (ClaimDetails) session.getAttribute(claimId);
        String userId = oidcUser.getClaim("oid");
        try {
            assessmentService.submitAssessment(claim, userId);
            return String.format("redirect:/submissions/%s/claims/%s/confirmation", submissionId, claimId);
        } catch (WebClientResponseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return renderView(model, claim, submissionId, claimId, true);
        }
    }

    private String renderView(Model model, ClaimDetails claim, String submissionId, String claimId, boolean submissionFailed) {
        model.addAttribute("claim", claim);
        model.addAttribute("viewModel", claim.toViewModel());
        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("backUrl", String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId));
        model.addAttribute("submissionFailed", submissionFailed);

        return "review-and-amend";
    }
}
