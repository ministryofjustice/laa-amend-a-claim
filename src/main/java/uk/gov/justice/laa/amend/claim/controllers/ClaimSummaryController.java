package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

@Controller
@RequiredArgsConstructor
public class ClaimSummaryController {

    private final ClaimService claimService;
    private final AssessmentService assessmentService;
    private final UserRetrievalService userRetrievalService;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}")
    public String onPageLoad(
            HttpSession session,
            Model model,
            @PathVariable(value = "submissionId") UUID submissionId,
            @PathVariable(value = "claimId") UUID claimId) {
        ClaimDetails claim = claimService.getClaimDetails(submissionId, claimId);
        if (claim.isHasAssessment()) {
            claim = assessmentService.getLatestAssessmentByClaim(claim);
            if (claim.getLastAssessment() != null) {
                var user = userRetrievalService.getMicrosoftApiUser(
                        claim.getLastAssessment().getLastAssessedBy());
                model.addAttribute("user", user);
            }
        }
        session.setAttribute(claimId.toString(), claim);
        String searchUrl =
                (String) Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");
        model.addAttribute("searchUrl", searchUrl);
        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claim", claim.toViewModel());
        return "claim-summary";
    }

    @PostMapping("/submissions/{submissionId}/claims/{claimId}")
    public String onSubmit(
            @PathVariable(value = "submissionId") UUID submissionId,
            @PathVariable(value = "claimId") UUID claimId,
            HttpSession session) {
        ClaimDetails claim = (ClaimDetails) session.getAttribute(claimId.toString());

        if (claim.isHasAssessment()) {
            return String.format("redirect:/submissions/%s/claims/%s/review", submissionId, claimId);
        }

        return String.format("redirect:/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);
    }
}
