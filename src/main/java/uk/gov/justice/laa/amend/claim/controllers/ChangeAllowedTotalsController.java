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
import uk.gov.justice.laa.amend.claim.forms.AllowedTotalForm;
import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.utils.CurrencyUtils.setScale;


@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/allowed-totals")
public class ChangeAllowedTotalsController {

    @GetMapping()
    public String loadPage(@PathVariable String claimId,
                           @PathVariable String submissionId,
                           Model model,
                           HttpSession session) {

        ClaimDetails claim = (ClaimDetails) session.getAttribute(claimId);
        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        AllowedTotalForm allowedTotalForm = new AllowedTotalForm();

        if (claim != null) {
            ClaimField allowedTotalVatField = claim.getAllowedTotalVat();
            ClaimField allowedTotalInclVatField = claim.getAllowedTotalInclVat();

            BigDecimal allowedTotalVat = allowedTotalVatField != null ? (BigDecimal) allowedTotalVatField.getAmended() : null;
            BigDecimal allowedTotalInclVat = allowedTotalInclVatField != null ? (BigDecimal) allowedTotalInclVatField.getAmended() : null;

            if (allowedTotalVat != null) {
                allowedTotalForm.setAllowedTotalVat(setScale(allowedTotalVat).toString());
            }

            if (allowedTotalInclVat != null) {
                allowedTotalForm.setAllowedTotalInclVat(setScale(allowedTotalInclVat).toString());
            }
        }

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
            ClaimField allowedTotalVatField = claim.getAllowedTotalVat();
            ClaimField allowedTotalInclVatField = claim.getAllowedTotalInclVat();

            BigDecimal allowedTotalInclVat = setScale(allowedTotalForm.getAllowedTotalInclVat());
            BigDecimal allowedTotalVat = setScale(allowedTotalForm.getAllowedTotalVat());

            allowedTotalInclVatField.setAmended(allowedTotalInclVat);
            allowedTotalVatField.setAmended(allowedTotalVat);

            allowedTotalVatField.setStatus(AmendStatus.AMENDABLE);
            allowedTotalInclVatField.setStatus(AmendStatus.AMENDABLE);

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
