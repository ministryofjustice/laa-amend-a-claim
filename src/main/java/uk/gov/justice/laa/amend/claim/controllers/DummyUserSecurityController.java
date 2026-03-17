package uk.gov.justice.laa.amend.claim.controllers;

import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.justice.laa.amend.claim.models.Role;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;

@AllArgsConstructor
@Profile({"local", "ephemeral", "e2e"})
@RequestMapping("/dummy-user-security")
@Controller
public class DummyUserSecurityController {

    private final DummyUserSecurityService service;

    @GetMapping()
    public String onPageLoad(Model model) {
        model.addAttribute("roles", service.getRoles());
        return "dummy-user-security";
    }

    @PostMapping()
    public String submit(@RequestParam(value = "roles", required = false) Set<Role> roles) {
        service.setRoles(Optional.ofNullable(roles).orElse(Set.of()));
        return "redirect:/dummy-user-security";
    }
}
