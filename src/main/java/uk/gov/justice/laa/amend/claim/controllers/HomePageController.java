package uk.gov.justice.laa.amend.claim.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    @GetMapping("/")
    public String onPageLoad() {
        return "index";
    }

    @GetMapping("/throw-error")
    public String sentryTest() {
        throw new RuntimeException("This is a test exception");
    }
}
