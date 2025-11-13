package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.forms.AssessmentOutcomeForm;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}")
public class AssessmentOutcomeController {

    private final ClaimService claimService;
    private final ClaimResultMapper claimResultMapper;
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
        ClaimSummary claimSummary = (ClaimSummary) session.getAttribute(claimId);
        String assessmentOutcome = null;
        String liabilityForVat = null;

        if (claimSummary != null) {
            // Load assessment outcome
            if (claimSummary.getAssessmentOutcome() != null) {
                assessmentOutcome = claimSummary.getAssessmentOutcome().getFormValue();
            }

            // Load VAT liability from vatClaimed
            if (claimSummary.getVatClaimed() != null && claimSummary.getVatClaimed().getAmended() != null) {
                Boolean vatAmended = (Boolean) claimSummary.getVatClaimed().getAmended();
                liabilityForVat = vatAmended ? "yes" : "no";
            }
        }

        form.setAssessmentOutcome(assessmentOutcome);
        form.setLiabilityForVat(liabilityForVat);

        ClaimResponse claimResponse = claimService.getClaim(submissionId, claimId);
        Claim claim = claimResultMapper.mapToClaim(claimResponse);

        model.addAttribute("claim", claim);
        model.addAttribute("assessmentOutcomeForm", form);
        model.addAttribute("assessmentOutcome", assessmentOutcome);
        model.addAttribute("liabilityForVat", liabilityForVat);

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

        ClaimSummary claimSummary = (ClaimSummary) session.getAttribute(claimId);

        if (claimSummary != null) {
            OutcomeType newOutcome = OutcomeType.fromFormValue(assessmentOutcomeForm.getAssessmentOutcome());

            // Apply business logic based on outcome change
            assessmentService.applyAssessmentOutcome(claimSummary, newOutcome);

            // Set the assessment outcome
            claimSummary.setAssessmentOutcome(newOutcome);

            // Update VAT liability in vatClaimed ClaimFieldRow
            if (claimSummary.getVatClaimed() != null) {
                Boolean vatLiable = "yes".equalsIgnoreCase(assessmentOutcomeForm.getLiabilityForVat());
                claimSummary.getVatClaimed().setAmended(vatLiable);
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
