package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.forms.AllowedTotalForm;


@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/allowed-totals")
public class ChangeAllowedTotalsController {

    @GetMapping()
    public String loadPage(@PathVariable String claimId,
                           @PathVariable String submissionId,
                           Model model,
                           HttpSession session) {

        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        AllowedTotalForm allowedTotalForm = new AllowedTotalForm();

        return renderView(model, allowedTotalForm, submissionId, claimId);
    }


    private String renderView(Model model, AllowedTotalForm form, String submissionId, String claimId) {
        model.addAttribute("allowedTotalForm", form);
        model.addAttribute("redirectUrl", getRedirectUrl(submissionId, claimId));
        return "allowed-totals";
    }

    private String getRedirectUrl(String submissionId, String claimId) {
        return String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }

}
