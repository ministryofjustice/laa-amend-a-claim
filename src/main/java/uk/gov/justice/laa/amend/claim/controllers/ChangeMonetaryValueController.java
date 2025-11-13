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
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.models.Assessment;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
        Function<Assessment, BigDecimal> getter = cost.getGetter();
        if (getter == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        // TODO - if retrieval from session returns null, redirect to session expired?
        Assessment assessment = (Assessment) session.getAttribute(String.format("%s:assessment", claimId));
        BigDecimal value = assessment != null ? getter.apply(assessment) : null;
        MonetaryValueForm form = new MonetaryValueForm();
        if (value != null) {
            form.setValue(setScale(value).toString());
        }

        model.addAttribute("cost", cost);
        model.addAttribute("form", form);
        String action = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost.getPath());
        model.addAttribute("action", action);

        return "change-monetary-value";
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
        BiConsumer<Assessment, BigDecimal> setter = cost.getSetter();
        if (setter == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("cost", cost);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "change-monetary-value";
        }

        // TODO - if retrieval from session returns null, redirect to session expired?
        Assessment assessment = (Assessment) session.getAttribute(String.format("%s:assessment", claimId));
        BigDecimal value = setScale(new BigDecimal(form.getValue()));
        setter.accept(assessment, value);
        session.setAttribute(String.format("%s:assessment", claimId), assessment);

        // TODO - Point to 'review and amend' page
        String redirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
        return "redirect:" + redirectUrl;
    }

    private BigDecimal setScale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
