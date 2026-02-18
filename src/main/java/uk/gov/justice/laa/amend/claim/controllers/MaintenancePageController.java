package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@Controller
@AllArgsConstructor
public class MaintenancePageController {

    private final MaintenanceService maintenanceService;

    @GetMapping("/maintenance")
    public String onPageLoad(Model model, HttpServletRequest request) throws IOException {

        if (!maintenanceService.maintenanceApplies(request)) {
            return "redirect:/";
        }

        model.addAttribute("maintenanceMessage", maintenanceService.getMessage());
        model.addAttribute("maintenanceTitle", maintenanceService.getTitle());
        return "maintenance";
    }
}
