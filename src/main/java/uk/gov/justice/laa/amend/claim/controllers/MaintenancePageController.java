package uk.gov.justice.laa.amend.claim.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@Controller
@AllArgsConstructor
public class MaintenancePageController {

    private final MaintenanceService maintenanceService;

    @GetMapping("/maintenance")
    public String onPageLoad(Model model) {
        if (maintenanceService.maintenanceEnabled()) {
            model.addAttribute("maintenanceMessage", maintenanceService.getMessage());
            model.addAttribute("maintenanceTitle", maintenanceService.getTitle());
            return "maintenance";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
