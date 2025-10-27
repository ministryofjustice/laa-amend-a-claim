package uk.gov.justice.laa.amend.claim.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ConfirmationController {

    @GetMapping("/confirmation")
    public String onPageLoad() {
        return "confirmation";
    }
}
