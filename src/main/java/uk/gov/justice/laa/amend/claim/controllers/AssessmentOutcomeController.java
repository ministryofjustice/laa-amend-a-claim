package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import uk.gov.justice.laa.amend.claim.forms.AssessmentOutcomeForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}")
public class AssessmentOutcomeController {

    private final AssessmentService assessmentService;

    @GetMapping("/assessment-outcome")
    public String setAssessmentOutcome(
        HttpServletRequest request,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId);

        AssessmentOutcomeForm form = new AssessmentOutcomeForm();
        form.setAssessmentOutcome(claim.getAssessmentOutcome());

        // Load VAT liability from vatClaimed
        if (claim.getVatClaimed() != null && claim.getVatClaimed().getAssessed() != null) {
            form.setLiabilityForVat((Boolean) claim.getVatClaimed().getAssessed());
        }

        return renderView(model, form, submissionId, claimId);
    }

    @PostMapping("/assessment-outcome")
    public String selectAssessmentOutcome(
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @Valid @ModelAttribute AssessmentOutcomeForm form,
        BindingResult bindingResult,
        HttpSession session,
        Model model,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return renderView(model, form, submissionId, claimId);
        }

        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId);

        OutcomeType newOutcome = form.getAssessmentOutcome();

        // Apply business logic based on outcome change
        assessmentService.applyAssessmentOutcome(claim, newOutcome);

        // Set the assessment outcome
        claim.setAssessmentOutcome(newOutcome);

        // Update VAT liability in vatClaimed
        if (claim.getVatClaimed() != null) {
            claim.getVatClaimed().setAssessed(form.getLiabilityForVat());
        }

        // Save updated Claim back to session
        session.setAttribute(claimId, claim);

        return String.format("redirect:/submissions/%s/claims/%s/review", submissionId, claimId);
    }

    private String renderView(Model model, AssessmentOutcomeForm form, String submissionId, String claimId) {
        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claimId", claimId);
        model.addAttribute("assessmentOutcomeForm", form);

        return "assessment-outcome";
    }
}
