package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.utils.CurrencyUtils.setScale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;
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
import uk.gov.justice.laa.amend.claim.forms.AssessedTotalForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/assessed-totals")
@Slf4j
public class ChangeAssessedTotalsController {

    @GetMapping()
    public String onPageLoad(
            @PathVariable UUID claimId, @PathVariable UUID submissionId, Model model, HttpServletRequest request) {
        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId.toString());

        if (claim.getAssessedTotalVat().isNotAssessable()
                || claim.getAssessedTotalInclVat().isNotAssessable()) {
            log.warn("The assessed totals are not modifiable for claim {}. Returning 404.", claimId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        AssessedTotalForm form = new AssessedTotalForm();

        BigDecimal totalVat = (BigDecimal) claim.getAssessedTotalVat().getAssessed();
        if (totalVat != null) {
            form.setAssessedTotalVat(setScale(totalVat).toString());
        }

        BigDecimal totalInclVat = (BigDecimal) claim.getAssessedTotalInclVat().getAssessed();
        if (totalInclVat != null) {
            form.setAssessedTotalInclVat(setScale(totalInclVat).toString());
        }

        return renderView(model, form, submissionId, claimId);
    }

    @PostMapping()
    public String onSubmit(
            @PathVariable(value = "submissionId") UUID submissionId,
            @PathVariable(value = "claimId") UUID claimId,
            @Valid @ModelAttribute("form") AssessedTotalForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return renderView(model, form, submissionId, claimId);
        }

        ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId.toString());

        ClaimField totalVatField = claim.getAssessedTotalVat();
        BigDecimal totalVat = setScale(form.getAssessedTotalVat());
        totalVatField.setAssessed(totalVat);

        ClaimField totalInclVatField = claim.getAssessedTotalInclVat();
        BigDecimal totalInclVat = setScale(form.getAssessedTotalInclVat());
        totalInclVatField.setAssessed(totalInclVat);

        // Save updated Claim back to session
        session.setAttribute(claimId.toString(), claim);

        return String.format("redirect:/submissions/%s/claims/%s/review", submissionId, claimId);
    }

    private String renderView(Model model, AssessedTotalForm form, UUID submissionId, UUID claimId) {
        model.addAttribute("form", form);
        String redirectUrl = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
        model.addAttribute("redirectUrl", redirectUrl);
        return "assessed-totals";
    }
}
