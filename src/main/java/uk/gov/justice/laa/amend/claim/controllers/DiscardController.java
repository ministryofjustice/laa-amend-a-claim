package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/discard")
public class DiscardController {

    @GetMapping()
    public String onPageLoad(
            Model model,
            @PathVariable(value = "submissionId") String submissionId,
            @PathVariable(value = "claimId") String claimId) {
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claimId", claimId);

        return "discard";
    }

    @PostMapping()
    public String discard(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @PathVariable(value = "submissionId") String submissionId,
            @PathVariable(value = "claimId") String claimId) {
        session.removeAttribute(claimId);

        redirectAttributes.addFlashAttribute("discarded", true);

        return "redirect:/";
    }
}
