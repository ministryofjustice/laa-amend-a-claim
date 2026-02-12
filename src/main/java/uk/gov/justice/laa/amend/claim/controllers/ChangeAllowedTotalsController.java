package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.utils.CurrencyUtils.setScale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.forms.AllowedTotalForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/allowed-totals")
@Slf4j
public class ChangeAllowedTotalsController {

    @GetMapping()
    public String onPageLoad(
            @PathVariable String claimId, @PathVariable String submissionId, Model model, HttpServletRequest request) {
        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId);

        if (claim.getAllowedTotalVat().isNotAssessable()
                || claim.getAllowedTotalInclVat().isNotAssessable()) {
            log.warn("The allowed totals are not modifiable for claim {}. Returning 404.", claimId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        AllowedTotalForm allowedTotalForm = new AllowedTotalForm();

        BigDecimal allowedTotalVat = (BigDecimal) claim.getAllowedTotalVat().getAssessed();
        if (allowedTotalVat != null) {
            allowedTotalForm.setAllowedTotalVat(setScale(allowedTotalVat).toString());
        }

        BigDecimal allowedTotalInclVat =
                (BigDecimal) claim.getAllowedTotalInclVat().getAssessed();
        if (allowedTotalInclVat != null) {
            allowedTotalForm.setAllowedTotalInclVat(
                    setScale(allowedTotalInclVat).toString());
        }

        return renderView(model, allowedTotalForm, submissionId, claimId);
    }

    @PostMapping()
    public String onSubmit(
            @PathVariable(value = "submissionId") String submissionId,
            @PathVariable(value = "claimId") String claimId,
            @Valid @ModelAttribute("form") AllowedTotalForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return renderView(model, form, submissionId, claimId);
        }

        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId);

        ClaimField allowedTotalVatField = claim.getAllowedTotalVat();
        BigDecimal allowedTotalVat = setScale(form.getAllowedTotalVat());
        allowedTotalVatField.setAssessed(allowedTotalVat);

        ClaimField allowedTotalInclVatField = claim.getAllowedTotalInclVat();
        BigDecimal allowedTotalInclVat = setScale(form.getAllowedTotalInclVat());
        allowedTotalInclVatField.setAssessed(allowedTotalInclVat);

        ClaimField assessedTotalVatField = claim.getAssessedTotalVat();
        if (assessedTotalVatField.isNotAssessable()) {
            assessedTotalVatField.setAssessed(allowedTotalVat);
        }

        ClaimField assessedTotalInclVatField = claim.getAssessedTotalInclVat();
        if (assessedTotalInclVatField.isNotAssessable()) {
            assessedTotalInclVatField.setAssessed(allowedTotalInclVat);
        }

        // Save updated Claim back to session
        session.setAttribute(claimId, claim);

        return String.format("redirect:/submissions/%s/claims/%s/review", submissionId, claimId);
    }

    private String renderView(Model model, AllowedTotalForm form, String submissionId, String claimId) {
        model.addAttribute("form", form);
        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        return "allowed-totals";
    }

    private String getRedirectUrl(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }
}
