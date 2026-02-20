package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_ID;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class ConfirmationController {

    @GetMapping("/submissions/{submissionId}/claims/{claimId}/assessments/{assessmentId}")
    public String onPageLoad(
            Model model,
            @PathVariable(value = "submissionId") UUID submissionId,
            @PathVariable(value = "claimId") UUID claimId,
            @PathVariable(value = "assessmentId") UUID assessmentId,
            HttpSession session) {
        UUID submittedAssessmentId = (UUID) session.getAttribute(ASSESSMENT_ID);
        if (submittedAssessmentId != null && submittedAssessmentId.equals(assessmentId)) {
            model.addAttribute("submissionId", submissionId);
            model.addAttribute("claimId", claimId);

            return "confirmation";
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
