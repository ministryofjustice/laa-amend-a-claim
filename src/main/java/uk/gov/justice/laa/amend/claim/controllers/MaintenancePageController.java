package uk.gov.justice.laa.amend.claim.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

import java.io.IOException;

@Controller
@AllArgsConstructor
public class MaintenancePageController {
    private final MaintenanceService maintenanceService;

    @GetMapping("/maintenance")
    public String handleMaintenance(Model model) throws IOException {
        model.addAttribute("maintenanceMessage", maintenanceService.getMessage());
        model.addAttribute("maintenanceTitle", maintenanceService.getTitle());
        return "maintenance";
    }
}
