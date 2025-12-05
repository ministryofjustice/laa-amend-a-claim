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
import uk.gov.justice.laa.amend.claim.forms.AssessedTotalForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.utils.CurrencyUtils.setScale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/assessed-totals")
public class ChangeAssessedTotalsController {

    @GetMapping()
    public String onPageLoad(
        @PathVariable String claimId,
        @PathVariable String submissionId,
        Model model,
        HttpServletRequest request
    ) {
        // TODO - check if they're allowed to be here
        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId);
        AssessedTotalForm form = new AssessedTotalForm();

        ClaimField totalVatField = claim.getAssessedTotalVat();
        BigDecimal totalVat = totalVatField != null ? (BigDecimal) totalVatField.getAmended() : null;
        if (totalVat != null) {
            form.setAssessedTotalVat(setScale(totalVat).toString());
        }

        ClaimField totalInclVatField = claim.getAssessedTotalInclVat();
        BigDecimal totalInclVat = totalInclVatField != null ? (BigDecimal) totalInclVatField.getAmended() : null;
        if (totalInclVat != null) {
            form.setAssessedTotalInclVat(setScale(totalInclVat).toString());
        }

        return renderView(model, form, submissionId, claimId);
    }

    @PostMapping()
    public String onSubmit(
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @Valid @ModelAttribute("form") AssessedTotalForm form,
        BindingResult bindingResult,
        HttpSession session,
        Model model,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        // TODO - check if they're allowed to be here
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return renderView(model, form, submissionId, claimId);
        }

        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId);

        ClaimField totalVatField = claim.getAssessedTotalVat();
        BigDecimal totalVat = setScale(form.getAssessedTotalVat());
        totalVatField.setAmendedToValue(totalVat);

        ClaimField totalInclVatField = claim.getAssessedTotalInclVat();
        BigDecimal totalInclVat = setScale(form.getAssessedTotalInclVat());
        totalInclVatField.setAmendedToValue(totalInclVat);

        // Save updated Claim back to session
        session.setAttribute(claimId, claim);

        return String.format("redirect:/submissions/%s/claims/%s/review", submissionId, claimId);
    }

    private String renderView(Model model, AssessedTotalForm form, String submissionId, String claimId) {
        model.addAttribute("form", form);
        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        return "assessed-totals";
    }

    private String getRedirectUrl(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }
}
