package uk.gov.justice.laa.amend.claim.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotAuthorisedController {

    @GetMapping("/not-authorised")
    public String onPageLoad() {
        return "not-authorised";
    }
}
