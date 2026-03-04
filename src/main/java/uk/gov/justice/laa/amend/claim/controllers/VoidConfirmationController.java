package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/void")
@Slf4j
public class VoidConfirmationController {

    private final boolean isVoidingEnabled;

    public VoidConfirmationController(@Value("${feature-flags.is-voiding-enabled}") boolean isVoidingEnabled) {
        this.isVoidingEnabled = isVoidingEnabled;
    }

    @GetMapping
    public String onPageLoad(
            HttpSession session,
            Model model,
            HttpServletResponse response,
            @PathVariable UUID submissionId,
            @PathVariable UUID claimId)
            throws IOException {
        if (!isVoidingEnabled) {
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
            @PathVariable UUID submissionId,
            @PathVariable UUID claimId,
            HttpServletResponse response)
            throws IOException {
        if (!isVoidingEnabled) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        var claim = getValidClaim(session, submissionId, claimId);

        try {
            // TODO: BC-382: Submit the void request

            String searchUrl = (String)
                    Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");
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
