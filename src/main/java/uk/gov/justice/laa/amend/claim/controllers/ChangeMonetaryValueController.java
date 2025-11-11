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
        "profit-costs", ClaimResponse::getNetProfitCostsAmount,
        "disbursements", ClaimResponse::getNetDisbursementAmount,
        "disbursements-vat", ClaimResponse::getDisbursementsVatAmount,
        "counsel-costs", ClaimResponse::getNetCounselCostsAmount,
        "detention-travel-and-waiting-costs", ClaimResponse::getDetentionTravelWaitingCostsAmount,
        "jr-form-filling-costs", ClaimResponse::getJrFormFillingAmount,
        "travel-costs", claim -> claim.getFeeCalculationResponse() != null ? claim.getFeeCalculationResponse().getNetTravelCostsAmount() : null,
        "waiting-costs", claim -> claim.getFeeCalculationResponse() != null ? claim.getFeeCalculationResponse().getNetWaitingCostsAmount() : null
    );

    private static final Map<String, BiConsumer<ClaimResponse, BigDecimal>> SETTERS = Map.of(
        "profit-costs", ClaimResponse::setNetProfitCostsAmount,
        "disbursements", ClaimResponse::setNetDisbursementAmount,
        "disbursements-vat", ClaimResponse::setDisbursementsVatAmount,
        "counsel-costs", ClaimResponse::setNetCounselCostsAmount,
        "detention-travel-and-waiting-costs", ClaimResponse::setDetentionTravelWaitingCostsAmount,
        "jr-form-filling-costs", ClaimResponse::setJrFormFillingAmount,
        "travel-costs", (claim, value) -> {
            if (claim.getFeeCalculationResponse() != null) {
                claim.getFeeCalculationResponse().setNetTravelCostsAmount(value);
            }
        },
        "waiting-costs", (claim, value) -> {
            if (claim.getFeeCalculationResponse() != null) {
                claim.getFeeCalculationResponse().setNetWaitingCostsAmount(value);
            }
        }
    );

    private BigDecimal setScale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
