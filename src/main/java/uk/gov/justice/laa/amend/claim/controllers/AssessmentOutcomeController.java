package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.forms.AssessmentOutcomeForm;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}")
public class AssessmentOutcomeController {

   private final AssessmentService assessmentService;

    @GetMapping("/assessment-outcome")
    public String setAssessmentOutcome(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {

        AssessmentOutcomeForm form = new AssessmentOutcomeForm();

        // Load values from ClaimSummary if it exists
        Claim claimSummary = (Claim) session.getAttribute(claimId);

        if (claimSummary != null) {
            // Load assessment outcome
            form.setAssessmentOutcome(claimSummary.getAssessmentOutcome());

            // Load VAT liability from vatClaimed
            if (claimSummary.getVatClaimed() != null && claimSummary.getVatClaimed().getAmended() != null) {
                form.setLiabilityForVat((Boolean) claimSummary.getVatClaimed().getAmended());
            }
        }

        model.addAttribute("assessmentOutcomeForm", form);

        return "assessment-outcome";
    }

    @PostMapping("/assessment-outcome")
    public String selectAssessmentOutcome(
            @PathVariable(value = "submissionId") String submissionId,
            @PathVariable(value = "claimId") String claimId,
            @Valid @ModelAttribute AssessmentOutcomeForm assessmentOutcomeForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("submissionId", submissionId);
            model.addAttribute("claimId", claimId);
            return "assessment-outcome";
        }

        Claim claimSummary = (Claim) session.getAttribute(claimId);

        if (claimSummary != null) {
            OutcomeType newOutcome = assessmentOutcomeForm.getAssessmentOutcome();

            // Apply business logic based on outcome change
            assessmentService.applyAssessmentOutcome(claimSummary, newOutcome);

            // Set the assessment outcome
            claimSummary.setAssessmentOutcome(newOutcome);

            // Update VAT liability in vatClaimed ClaimField
            if (claimSummary.getVatClaimed() != null) {
                claimSummary.getVatClaimed().setAmended(assessmentOutcomeForm.getLiabilityForVat());
            }

            // Save updated ClaimSummary back to session
            session.setAttribute(claimId, claimSummary);
        }

        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claimId", claimId);

        redirectAttributes.addFlashAttribute("submissionId", submissionId);
        redirectAttributes.addFlashAttribute("claimId", claimId);

        return "redirect:/submissions/{submissionId}/claims/{claimId}/review";
    }
}
