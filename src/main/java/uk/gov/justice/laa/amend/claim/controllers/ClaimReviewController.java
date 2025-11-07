package uk.gov.justice.laa.amend.claim.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.amend.claim.models.TableRow;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.ClaimTableRowService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

@Controller
@RequiredArgsConstructor
public class ClaimReviewController {

    private final ClaimService claimService;
    private final ClaimTableRowService claimTableRowService;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/review")
    public String onPageLoad(
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        ClaimResponse claimResponse = claimService.getClaim(submissionId, claimId);

        model.addAttribute("headers", TableRow.getHeaders());
        model.addAttribute("tableRows", claimTableRowService.buildTableRows(claimResponse));

        return "review-and-amend";
    }
}
