package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Controller
public class MaintenancePageController {

    @GetMapping("/maintenance")
    public String handleError(HttpServletRequest request, Model model) throws IOException {

        Path message = Paths.get("/config/maintenance/message");
        Path title = Paths.get("/config/maintenance/title");

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

        return "maintenance";
    }
}
