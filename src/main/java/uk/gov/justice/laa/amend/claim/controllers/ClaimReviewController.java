package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.amend.claim.models.Assessment;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimValuesTableRow;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.ClaimTableRowService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

@Controller
@RequiredArgsConstructor
public class ClaimReviewController {

    private final ClaimService claimService;
    private final ClaimTableRowService claimTableRowService;
    private final HttpSession session;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/review")
    public String onPageLoad(
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        ClaimResponse claimResponse = claimService.getClaim(submissionId, claimId);
        Assessment assessment = (Assessment) session.getAttribute("application");

        if (assessment == null) {
            return "redirect:/submissions/" + submissionId + "/claims/" + claimId;
        }

        model.addAttribute("headers", ClaimValuesTableRow.getHeaders());
        model.addAttribute("tableRows", claimTableRowService.buildTableRows(claimResponse));
        model.addAttribute("assessment", assessment);
        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);

        return "review-and-amend";
    }
}
