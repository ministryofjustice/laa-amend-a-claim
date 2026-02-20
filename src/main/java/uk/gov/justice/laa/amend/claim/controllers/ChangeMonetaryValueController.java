package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.utils.CurrencyUtils.setScale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.IOException;
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
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;
import uk.gov.justice.laa.amend.claim.forms.MonetaryValueForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/")
@Slf4j
public class ChangeMonetaryValueController {

    @GetMapping("{cost}")
    public String getMonetaryValue(
            Model model,
            @PathVariable(value = "submissionId") UUID submissionId,
            @PathVariable(value = "claimId") UUID claimId,
            @PathVariable(value = "cost") Cost cost,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        try {
            ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId.toString());
            ClaimField claimField = cost.getAccessor().get(claim);

            if (claimField == null) {
                log.warn("Could not find claim field {} in claim {}. Returning 404.", cost.getPath(), claimId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            if (claimField.isNotAssessable()) {
                log.warn("This claim field is not modifiable for claim {}. Returning 404.", claimId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            BigDecimal value = (BigDecimal) claimField.getAssessed();

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
            @PathVariable(value = "submissionId") UUID submissionId,
            @PathVariable(value = "claimId") UUID claimId,
            @PathVariable(value = "cost") Cost cost,
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @ModelAttribute("form") MonetaryValueForm form,
            BindingResult bindingResult)
            throws IOException {
        try {
            ClaimDetails claim = (ClaimDetails) request.getAttribute(claimId.toString());

            if (bindingResult.hasErrors()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return renderView(model, form, cost, submissionId, claimId);
            }

            ClaimField claimField = cost.getAccessor().get(claim);
            BigDecimal value = setScale(form.getValue());
            claimField.setAssessed(value);
            cost.getAccessor().set(claim, claimField);
            session.setAttribute(claimId.toString(), claim);

            return "redirect:" + getRedirectUrl(submissionId, claimId);
        } catch (ClaimMismatchException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    private String renderView(Model model, MonetaryValueForm form, Cost cost, UUID submissionId, UUID claimId) {
        model.addAttribute("cost", cost);
        model.addAttribute("form", form);
        model.addAttribute("action", getAction(submissionId, claimId, cost));
        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        return "change-monetary-value";
    }

    private String getAction(UUID submissionId, UUID claimId, Cost cost) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost.getPath());
    }

    private String getRedirectUrl(UUID submissionId, UUID claimId) {
        return String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }
}
