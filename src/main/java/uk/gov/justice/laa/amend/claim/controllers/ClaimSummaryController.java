package uk.gov.justice.laa.amend.claim.controllers;

import static java.lang.Boolean.TRUE;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidEscapeCaseClaim;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VALID;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VOID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleEscapeCaseCaseworker;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ClaimSummaryController {

    private final ClaimService claimService;
    private final AssessmentService assessmentService;
    private final UserRetrievalService userRetrievalService;
    private final FeatureFlagsConfig featureFlagsConfig;

    @GetMapping("/submissions/{submissionId}/claims/{claimId}")
    public String onPageLoad(
            HttpServletRequest request,
            HttpSession session,
            Model model,
            @PathVariable UUID submissionId,
            @PathVariable UUID claimId) {
        ClaimDetails claim = claimService.getClaimDetails(submissionId, claimId);
        if (!EnumSet.of(VALID, VOID).contains(claim.getStatus())) {
            log.error(
                    "Cannot assess claim {} as it has an invalid status {}. Returning 404.",
                    claimId,
                    claim.getStatus());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (claim.isHasAssessment()) {
            claim = assessmentService.getLatestAssessmentByClaim(claim);
            if (claim.getLastUpdatedUser() != null) {
                var user = userRetrievalService.getMicrosoftApiUser(claim.getLastUpdatedUser());
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

        boolean isAssessmentButtonPresent = request.isUserInRole(ROLE_ESCAPE_CASE_CASEWORKER.name())
                && claim.isValid()
                && TRUE.equals(claim.getEscaped());
        model.addAttribute("isAssessmentButtonPresent", isAssessmentButtonPresent);

        boolean isVoidButtonPresent = request.isUserInRole(ROLE_CLAIM_AMENDMENTS_CASEWORKER.name()) && claim.isValid();
        model.addAttribute("isVoidButtonPresent", isVoidButtonPresent);

        return "claim-summary";
    }

    @HasRoleEscapeCaseCaseworker
    @PostMapping("/submissions/{submissionId}/claims/{claimId}")
    public String onSubmit(@PathVariable UUID submissionId, @PathVariable UUID claimId, HttpSession session) {
        ClaimDetails claim = getValidEscapeCaseClaim(session, submissionId, claimId);

        if (claim.isHasAssessment()) {
            return String.format("redirect:/submissions/%s/claims/%s/review", submissionId, claimId);
        }

        return String.format("redirect:/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);
    }
}
