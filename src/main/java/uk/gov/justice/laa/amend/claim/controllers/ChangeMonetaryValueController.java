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
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/")
public class ChangeMonetaryValueController {

    @GetMapping("{cost}")
    public String getMonetaryValue(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @PathVariable(value = "cost") Cost cost,
        HttpServletResponse response
    ) throws IOException {
        try {
            // TODO - if retrieval from session returns null, redirect to session expired?
            Claim claim = (Claim) session.getAttribute(claimId);
            ClaimField claimField = cost.getAccessor().get(claim);
            BigDecimal value = claimField != null ? (BigDecimal) claimField.getAmended() : null;

            MonetaryValueForm form = new MonetaryValueForm();
            if (value != null) {
                form.setValue(setScale(value).toString());
            }

            return renderView(model, form, cost, submissionId, claimId);
        } catch (ClaimMismatchException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @PostMapping("{cost}")
    public String postMonetaryValue(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @PathVariable(value = "cost") Cost cost,
        HttpServletResponse response,
        @Valid @ModelAttribute("form") MonetaryValueForm form,
        BindingResult bindingResult
    ) throws IOException {
        try {
            // TODO - if retrieval from session returns null, redirect to session expired?
            Claim claim = (Claim) session.getAttribute(claimId);

            if (bindingResult.hasErrors()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return renderView(model, form, cost, submissionId, claimId);
            }

            ClaimField claimField = cost.getAccessor().get(claim);
            BigDecimal value = setScale(new BigDecimal(form.getValue()));
            if (claimField != null) {
                claimField.setAmended(value);
                cost.getAccessor().set(claim, claimField);
            }
            session.setAttribute(claimId, claim);

            return "redirect:" + getRedirectUrl(submissionId, claimId);
        } catch (ClaimMismatchException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    private String renderView(Model model, MonetaryValueForm form, Cost cost, String submissionId, String claimId) {
        model.addAttribute("cost", cost);
        model.addAttribute("form", form);
        model.addAttribute("action", getAction(submissionId, claimId, cost));
        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        return "change-monetary-value";
    }

    private String getAction(String submissionId, String claimId, Cost cost) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost.getPath());
    }

    private String getRedirectUrl(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }

    private BigDecimal setScale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
