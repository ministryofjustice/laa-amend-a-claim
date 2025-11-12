package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.amend.claim.mappers.ClaimSummaryMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

@Controller
@RequiredArgsConstructor
public class ClaimSummaryController {

    private final ClaimService claimService;
    private final ClaimSummaryMapper claimSummaryMapper;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}")
    public String onPageLoad(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        ClaimResponse claimResponse = claimService.getClaim(submissionId, claimId);
        boolean isCrimeClaim = isCrimeClaim(claimResponse);

        ClaimSummary claimSummary = isCrimeClaim ? claimSummaryMapper.mapToCrimeClaimSummary(claimResponse) : claimSummaryMapper.mapToCivilClaimSummary(claimResponse);
        session.setAttribute(claimId, claimSummary);
        // TODO - this is just placeholder code at the moment, and will likely be moved or removed altogether
        // ---
        //session.setAttribute(claimId, claimResponse);
        //session.setAttribute(String.format("%s:assessment", claimId), new Assessment(claimResponse));
        // ---

        model.addAttribute("claim", claimSummary);
        model.addAttribute("isCrimeClaim", isCrimeClaim);

        return "claim-summary";
    }

    //TODO: Use areaOfLaw from submission
    private boolean isCrimeClaim(ClaimResponse claim) {
        var feeCalculationPatch = claim != null && claim.getFeeCalculationResponse() != null ? claim.getFeeCalculationResponse() : null;
        return feeCalculationPatch != null && ("CRIME").equals(feeCalculationPatch.getCategoryOfLaw());
    }

}
