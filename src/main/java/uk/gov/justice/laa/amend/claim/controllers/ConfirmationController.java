package uk.gov.justice.laa.amend.claim.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ConfirmationController {

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/assessments/{assessmentId}")
    public String onPageLoad(
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @PathVariable(value = "assessmentId") String assessmentId
    ) {
        // TODO check if assessment exists, otherwise return 404?

        model.addAttribute("submissionId", submissionId);
        model.addAttribute("claimId", claimId);

        return "confirmation";
    }
}
