package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.service.ClaimService;

@Controller
@RequiredArgsConstructor
public class ClaimSummaryController {

    private final ClaimService claimService;


    @GetMapping("/submissions/{submissionId}/claims/{claimId}")
    public String onPageLoad(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        var claimDetails = claimService.getClaimDetails(submissionId, claimId);
        if (claimDetails == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim or submission not found");
        }
        session.setAttribute(claimId, claimDetails);

        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claim", claimDetails.toViewModel());
        return "claim-summary";
    }
}
