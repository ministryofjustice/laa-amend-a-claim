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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

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

    private static final String PROFIT_COSTS = "profit-costs";
    private static final String DISBURSEMENTS = "disbursements";
    private static final String DISBURSEMENTS_VAT = "disbursements-vat";
    private static final String COUNSEL_COSTS = "counsel-costs";
    private static final String DETENTION_TRAVEL_AND_WAITING_COSTS = "detention-travel-and-waiting-costs";
    private static final String JR_FORM_FILLING_COSTS = "jr-form-filling-costs";
    private static final String TRAVEL_COSTS = "travel-costs";
    private static final String WAITING_COSTS = "waiting-costs";

    @GetMapping("{prefix}")
    public String getProfitCosts(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @PathVariable(value = "prefix") String prefix,
        HttpServletResponse response
    ) throws IOException {
        Function<ClaimResponse, BigDecimal> getter = GETTERS.get(prefix);
        if (getter == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        ClaimResponse claim = (ClaimResponse) session.getAttribute(claimId);
        BigDecimal value = claim != null ? getter.apply(claim) : null;
        MonetaryValueForm form = new MonetaryValueForm();
        if (value != null) {
            form.setValue(setScale(value).toString());
        }

        model.addAttribute("prefix", prefix);
        model.addAttribute("form", form);
        String action = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        model.addAttribute("action", action);

        return "change-monetary-value";
    }

    @PostMapping("{prefix}")
    public String postProfitCosts(
        HttpSession session,
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @PathVariable(value = "prefix") String prefix,
        HttpServletResponse response,
        @Valid @ModelAttribute("form") MonetaryValueForm form,
        BindingResult bindingResult
    ) throws IOException {
        BiConsumer<ClaimResponse, BigDecimal> setter = SETTERS.get(prefix);
        if (setter == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("prefix", prefix);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "change-monetary-value";
        }

        ClaimResponse claim = (ClaimResponse) session.getAttribute(claimId);
        BigDecimal value = setScale(new BigDecimal(form.getValue()));
        setter.accept(claim, value);
        session.setAttribute(claimId, claim);

        String redirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
        return "redirect:" + redirectUrl;
    }

    private static final Map<String, Function<ClaimResponse, BigDecimal>> GETTERS = Map.of(
        PROFIT_COSTS, ClaimResponse::getNetProfitCostsAmount,
        DISBURSEMENTS, ClaimResponse::getNetDisbursementAmount,
        DISBURSEMENTS_VAT, ClaimResponse::getDisbursementsVatAmount,
        COUNSEL_COSTS, ClaimResponse::getNetCounselCostsAmount,
        DETENTION_TRAVEL_AND_WAITING_COSTS, ClaimResponse::getDetentionTravelWaitingCostsAmount,
        JR_FORM_FILLING_COSTS, ClaimResponse::getJrFormFillingAmount,
        TRAVEL_COSTS, claim -> claim.getFeeCalculationResponse() != null ? claim.getFeeCalculationResponse().getNetTravelCostsAmount() : null,
        WAITING_COSTS, claim -> claim.getFeeCalculationResponse() != null ? claim.getFeeCalculationResponse().getNetWaitingCostsAmount() : null
    );

    private static final Map<String, BiConsumer<ClaimResponse, BigDecimal>> SETTERS = Map.of(
        PROFIT_COSTS, ClaimResponse::setNetProfitCostsAmount,
        DISBURSEMENTS, ClaimResponse::setNetDisbursementAmount,
        DISBURSEMENTS_VAT, ClaimResponse::setDisbursementsVatAmount,
        COUNSEL_COSTS, ClaimResponse::setNetCounselCostsAmount,
        DETENTION_TRAVEL_AND_WAITING_COSTS, ClaimResponse::setDetentionTravelWaitingCostsAmount,
        JR_FORM_FILLING_COSTS, ClaimResponse::setJrFormFillingAmount,
        TRAVEL_COSTS, (claim, value) -> {
            if (claim.getFeeCalculationResponse() != null) {
                claim.getFeeCalculationResponse().setNetTravelCostsAmount(value);
            }
        },
        WAITING_COSTS, (claim, value) -> {
            if (claim.getFeeCalculationResponse() != null) {
                claim.getFeeCalculationResponse().setNetWaitingCostsAmount(value);
            }
        }
    );

    private BigDecimal setScale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
