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
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class ChangeMonetaryValueController {

    private final String prefix = "profitCosts";

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/profit-costs")
    public String getProfitCosts(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId
    ) {
        ClaimResponse claim = (ClaimResponse) session.getAttribute(claimId);
        BigDecimal netProfitCostsAmount = claim != null ? claim.getNetProfitCostsAmount() : null;
        MonetaryValueForm form = new MonetaryValueForm();
        form.setValue(netProfitCostsAmount);

        model.addAttribute("prefix", prefix);
        model.addAttribute("form", form);
        String action = String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId);
        model.addAttribute("action", action);

        return "change-monetary-value";
    }

    @PostMapping("/submissions/{submissionId}/claims/{claimId}/profit-costs")
    public String postProfitCosts(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @Valid @ModelAttribute("form") MonetaryValueForm form,
        BindingResult bindingResult,
        HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("prefix", prefix);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "change-monetary-value";
        }

        ClaimResponse claim = (ClaimResponse) session.getAttribute(claimId);
        //TODO - what if claim is null?
        claim.setNetProfitCostsAmount(form.getValue());
        String redirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
        return "redirect:" + redirectUrl;
    }
}
