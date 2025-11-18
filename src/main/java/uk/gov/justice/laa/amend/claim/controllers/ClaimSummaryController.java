package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

@Controller
@RequiredArgsConstructor
public class ClaimSummaryController {

    private final ClaimService claimService;
    private final ClaimMapper claimMapper;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}")
    public String onPageLoad(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        ClaimResponse claimResponse = claimService.getClaim(submissionId, claimId);
        SubmissionResponse submissionResponse = claimService.getSubmission(submissionId);
        if (claimResponse == null || submissionResponse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim or submission not found");
        }
        ClaimDetails claimDetails = claimMapper.mapToClaimDetails(claimResponse, submissionResponse);
        session.setAttribute(claimId, claimDetails);

        // TODO - when the claim is null/empty we should render an error screen. We can
        //  remove these from the model when those changes are made and amend the tests to reflect it

        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claim", claimDetails.toViewModel());
        return "claim-summary";
    }
}
