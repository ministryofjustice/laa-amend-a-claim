package uk.gov.justice.laa.amend.claim.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LogoutController {


    @GetMapping("/logout-success")
    public String logoutSuccess(@RequestParam(required = false) String message, Model model) {
        if (message != null && message.contains("expired")) {
            model.addAttribute("timeout", true);
        }
        return "logout";
    }
}
