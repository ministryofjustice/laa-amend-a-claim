package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;

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
            ClaimSummary claim = (ClaimSummary) session.getAttribute(claimId);
            ClaimFieldRow claimField = cost.getAccessor().get(claim);
            BigDecimal value = claimField != null ? (BigDecimal) claimField.getAmended() : null;

            MonetaryValueForm form = new MonetaryValueForm();
            if (value != null) {
                form.setValue(setScale(value).toString());
            }

            model.addAttribute("cost", cost);
            model.addAttribute("form", form);
            String action = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost.getPath());
            model.addAttribute("action", action);

            return "change-monetary-value";
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
            ClaimSummary claim = (ClaimSummary) session.getAttribute(claimId);

            if (bindingResult.hasErrors()) {
                model.addAttribute("cost", cost);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "change-monetary-value";
            }

            ClaimFieldRow claimField = cost.getAccessor().get(claim);
            BigDecimal value = setScale(new BigDecimal(form.getValue()));
            if (claimField != null) {
                claimField.setAmended(value);
                cost.getAccessor().set(claim, claimField);
            }
            session.setAttribute(claimId, claim);

            // TODO - Point to 'review and amend' page
            String redirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
            return "redirect:" + redirectUrl;
        } catch (ClaimMismatchException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }


    private BigDecimal setScale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
