package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.ClaimService;

@AllArgsConstructor
@UserControllerAdvice.Enabled
@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/void")
@HasRoleClaimAmendmentsCaseworker
@Slf4j
public class VoidConfirmationController {

    private final ClaimService claimService;
    private final FeatureFlagsConfig featureFlagsConfig;

    @GetMapping
    public String onPageLoad(
            HttpSession session,
            Model model,
            HttpServletResponse response,
            @PathVariable UUID submissionId,
            @PathVariable UUID claimId)
            throws IOException {
        if (!featureFlagsConfig.getIsVoidingEnabled()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        var claim = getValidClaim(session, submissionId, claimId);

        return renderView(model, claim, submissionId, claimId, false);
    }

    @PostMapping
    public String onSubmit(
            HttpSession session,
            Model model,
            @ModelAttribute("userId") UUID userId,
            @PathVariable UUID submissionId,
            @PathVariable UUID claimId,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response)
            throws IOException {
        if (!featureFlagsConfig.getIsVoidingEnabled()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        var claim = getValidClaim(session, submissionId, claimId);

        try {
            claimService.voidClaim(claimId, userId);

            String searchUrl = (String)
                    Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");
            redirectAttributes.addFlashAttribute("voided", true);
            session.removeAttribute(claimId.toString());

            return "redirect:" + searchUrl;
        } catch (Exception ex) {
            log.error("Failed to void assessment for claim ID: {}", claimId, ex);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return renderView(model, claim, submissionId, claimId, true);
        }
    }

    private String renderView(
            Model model, ClaimDetails claim, UUID submissionId, UUID claimId, boolean submissionFailed) {
        model.addAttribute("claim", claim.toViewModel());
        model.addAttribute("claimId", claimId);
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("submissionFailed", submissionFailed);

        return "void-confirmation";
    }
}
