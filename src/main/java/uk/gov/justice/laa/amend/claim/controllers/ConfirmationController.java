package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_ID;

@Controller
@RequiredArgsConstructor
public class ConfirmationController {

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/assessments/{assessmentId}")
    public String onPageLoad(
        Model model,
        @PathVariable(value = "submissionId") String submissionId,
        @PathVariable(value = "claimId") String claimId,
        @PathVariable(value = "assessmentId") String assessmentId,
        HttpSession session
    ) {
        Object submittedAssessmentId = session.getAttribute(ASSESSMENT_ID);
        if (submittedAssessmentId != null && submittedAssessmentId.toString().equals(assessmentId)) {
            model.addAttribute("submissionId", submissionId);
            model.addAttribute("claimId", claimId);

            return "confirmation";
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
