package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.forms.AssessmentOutcomeForm;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}")
public class AssessmentOutcomeController {

    private final ClaimService claimService;
    private final ClaimResultMapper claimResultMapper;

    @GetMapping("/assessment-outcome")
    public String setAssessmentOutcome(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {

        AssessmentOutcomeForm form = new AssessmentOutcomeForm();
        ClaimResponse claimResponse = claimService.getClaim(submissionId, claimId);
        Claim claim = claimResultMapper.mapToClaim(claimResponse);

        session.setAttribute(claimId, claim);

        model.addAttribute("claim", claim);
        model.addAttribute("assessmentOutcomeForm", form);

        String assessmentOutcome = (String) session.getAttribute("assessmentOutcome");
        String liabilityForVAT = (String) session.getAttribute("liabilityForVAT");

        model.addAttribute("assessmentOutcome", assessmentOutcome);
        model.addAttribute("liabilityForVAT", liabilityForVAT);

        return "assessment-outcome";
    }

    @PostMapping("/assessment-outcome")
    public String selectAssessmentOutcome(
            @PathVariable(value = "submissionId") String submissionId,
            @PathVariable(value = "claimId") String claimId,
            @ModelAttribute AssessmentOutcomeForm assessmentOutcomeForm,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        session.setAttribute("assessmentOutcome", assessmentOutcomeForm.getAssessmentOutcome());
        session.setAttribute("liabilityForVAT", assessmentOutcomeForm.getLiabilityForVAT());

        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claimId", claimId);

        System.out.println(assessmentOutcomeForm.getAssessmentOutcome());
        System.out.println(assessmentOutcomeForm.getAssessmentOutcome());
        System.out.println(assessmentOutcomeForm.getAssessmentOutcome());
        System.out.println(assessmentOutcomeForm.getAssessmentOutcome());
        System.out.println(assessmentOutcomeForm.getAssessmentOutcome());

        System.out.println(assessmentOutcomeForm.getLiabilityForVAT());
        System.out.println(assessmentOutcomeForm.getLiabilityForVAT());
        System.out.println(assessmentOutcomeForm.getLiabilityForVAT());
        System.out.println(assessmentOutcomeForm.getLiabilityForVAT());

        redirectAttributes.addFlashAttribute("submissionId", submissionId);
        redirectAttributes.addFlashAttribute("claimId", claimId);

        return "redirect:/submissions/{submissionId}/claims/{claimId}/assessment-outcome";
    }
}
