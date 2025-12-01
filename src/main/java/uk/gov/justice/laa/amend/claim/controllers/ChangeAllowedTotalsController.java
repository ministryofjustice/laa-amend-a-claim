package uk.gov.justice.laa.amend.claim.controllers;

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
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;
import uk.gov.justice.laa.amend.claim.forms.AllowedTotalForm;
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.ClaimFieldAccessor;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;


@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/allowed-totals")
public class ChangeAllowedTotalsController {

    private final AssessmentService assessmentService;

    @GetMapping()
    public String loadPage(@PathVariable String claimId,
                           @PathVariable String submissionId,
                           Model model,
                           HttpServletResponse response) {

        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        AllowedTotalForm allowedTotalForm = new AllowedTotalForm();

        return renderView(model, allowedTotalForm, submissionId, claimId);
    }

    @PostMapping()
    public String setTotals(
            @PathVariable(value = "submissionId") String submissionId,
            @PathVariable(value = "claimId") String claimId,
            @Valid @ModelAttribute("allowedTotalForm") AllowedTotalForm allowedTotalForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return renderView(model, allowedTotalForm, submissionId, claimId);
        }

        ClaimDetails claim = (ClaimDetails) session.getAttribute(claimId);


        if (claim != null) {
            assessmentService.applyAllowedTotals(claim, allowedTotalForm);

            // Save updated Claim back to session
            session.setAttribute(claimId, claim);
        }

        return String.format("redirect:/submissions/%s/claims/%s/review", submissionId, claimId);
    }


    private String renderView(Model model, AllowedTotalForm form, String submissionId, String claimId) {
        model.addAttribute("allowedTotalForm", form);
        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        return "allowed-totals";
    }

    private String getRedirectUrl(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }

}
