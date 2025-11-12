package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.models.Assessment;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
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
        Function<Assessment, BigDecimal> getter = GETTERS.get(cost);
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
        BiConsumer<Assessment, BigDecimal> setter = SETTERS.get(cost);
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

    private static final Map<Cost, Function<Assessment, BigDecimal>> GETTERS = Map.of(
        Cost.PROFIT_COSTS, Assessment::getNetProfitCostsAmount,
        Cost.DISBURSEMENTS, Assessment::getDisbursementAmount,
        Cost.DISBURSEMENTS_VAT, Assessment::getDisbursementVatAmount,
        Cost.COUNSEL_COSTS, Assessment::getNetCostOfCounselAmount,
        Cost.DETENTION_TRAVEL_AND_WAITING_COSTS, Assessment::getTravelAndWaitingCostsAmount,
        Cost.JR_FORM_FILLING_COSTS, Assessment::getJrFormFillingAmount,
        Cost.TRAVEL_COSTS, Assessment::getNetTravelCostsAmount,
        Cost.WAITING_COSTS, Assessment::getNetWaitingCostsAmount
    );

    private static final Map<Cost, BiConsumer<Assessment, BigDecimal>> SETTERS = Map.of(
        Cost.PROFIT_COSTS, Assessment::setNetProfitCostsAmount,
        Cost.DISBURSEMENTS, Assessment::setDisbursementAmount,
        Cost.DISBURSEMENTS_VAT, Assessment::setDisbursementVatAmount,
        Cost.COUNSEL_COSTS, Assessment::setNetCostOfCounselAmount,
        Cost.DETENTION_TRAVEL_AND_WAITING_COSTS, Assessment::setTravelAndWaitingCostsAmount,
        Cost.JR_FORM_FILLING_COSTS, Assessment::setJrFormFillingAmount,
        Cost.TRAVEL_COSTS, Assessment::setNetTravelCostsAmount,
        Cost.WAITING_COSTS, Assessment::setNetWaitingCostsAmount
    );

    private BigDecimal setScale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
