package uk.gov.justice.laa.amend.claim.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class MaintenancePageController {

    @GetMapping("/maintenance")
    public String handleMaintenance(Model model) throws IOException {
        Path maintenanceMessage = Paths.get("/config/maintenance/message");
        Path maintenanceTitle = Paths.get("/config/maintenance/title");

        model.addAttribute("maintenanceMessage", Files.readString(maintenanceMessage));
        model.addAttribute("maintenanceTitle", Files.readString(maintenanceTitle));

        return "maintenance";
    }
}
