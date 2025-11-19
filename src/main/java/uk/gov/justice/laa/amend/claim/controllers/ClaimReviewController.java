package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

@Controller
@RequiredArgsConstructor
public class ClaimReviewController {

    private final HttpSession session;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/review")
    public String onPageLoad(
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        ClaimDetails claim = (ClaimDetails) session.getAttribute(claimId);

        if (claim == null) {
            return String.format("redirect:/submissions/%s/claims/%s", submissionId, claimId);
        }

        model.addAttribute("claim", claim);
        model.addAttribute("viewModel", claim.toViewModel());
        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("backUrl", String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId));
        System.out.println("***");
        System.out.println(claim.getAssessmentOutcome());
        System.out.println(claim.getAssessmentOutcome() != null && claim.getAssessmentOutcome().getCanAmendCosts());
        model.addAttribute("canAmendCosts",
                claim.getAssessmentOutcome() != null && claim.getAssessmentOutcome().getCanAmendCosts()
        );

        return "review-and-amend";
    }

    @PostMapping("/submissions/{submissionId}/claims/{claimId}/review/discard")
    public String discard(
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        // Clear session data
        session.removeAttribute(claimId);

        return "redirect:/submissions/" + submissionId + "/claims/" + claimId;
    }

    @PostMapping("/submissions/{submissionId}/claims/{claimId}/review/submit")
    public String submit(
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        // TODO: Persist changes to claims data store

        return String.format("redirect:/submissions/%s/claims/%s/confirmation", submissionId, claimId);
    }
}
