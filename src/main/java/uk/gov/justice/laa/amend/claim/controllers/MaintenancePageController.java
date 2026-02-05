package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Controller
public class MaintenancePageController {

    @GetMapping("/maintenance")
    public String handleError(HttpServletRequest request, Model model) throws IOException {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Path message = Paths.get("/config/maintenance/message");
        Path title = Paths.get("/config/maintenance/title");

        Path enabled = Paths.get("/config/maintenance/enabled");

        if (Files.exists(enabled)
                && Files.readString(enabled).replace("/r", "")
                .trim().equalsIgnoreCase("true")) {


            model.addAttribute("message", Files.readString(message));
            model.addAttribute("title", Files.readString(title));

            log.error("============= in error controller");
            log.error("============= in error controller");
            log.error("============= in error controller");


            log.error("MESSAGE: " + Files.readString(message).trim());
            log.error("TITLE: " + Files.readString(title).trim());

            log.error("============= in error controller");
            log.error("============= in error controller");
            log.error("============= in error controller");

        }

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("statusCode", statusCode);
        }
        return "maintenance";
    }
}
