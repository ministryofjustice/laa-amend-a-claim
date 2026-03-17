package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleEscapeCaseCaseworker;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/discard")
@HasRoleEscapeCaseCaseworker
public class DiscardController {

    @GetMapping()
    public String onPageLoad(Model model, @PathVariable UUID submissionId, @PathVariable UUID claimId) {
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claimId", claimId);

        return "discard";
    }

    @PostMapping()
    public String discard(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @PathVariable UUID submissionId,
            @PathVariable UUID claimId) {
        session.removeAttribute(claimId.toString());
        String searchUrl =
                (String) Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");

        redirectAttributes.addFlashAttribute("discarded", true);

        return String.format("redirect:%s", searchUrl);
    }
}
