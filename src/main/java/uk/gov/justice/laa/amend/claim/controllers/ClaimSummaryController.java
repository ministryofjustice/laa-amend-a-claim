package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            Authentication authentication,
            @PathVariable(value = "submissionId") String submissionId,
            @PathVariable(value = "claimId") String claimId
    ) {
        var claimDetails = claimService.getClaimDetails(submissionId, claimId);
        if (claimDetails.isHasAssessment()) {
            claimDetails = assessmentService.getLatestAssessmentByClaim(claimDetails);
            if (claimDetails.getLastAssessment() != null) {
                var user = userRetrievalService.getGraphUser(authentication, claimDetails.getLastAssessment().getLastAssessedBy());
                model.addAttribute("user", user);
            }
        }
        session.setAttribute(claimId, claimDetails);
        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claim", claimDetails.toViewModel());
        return "claim-summary";
    }
}
