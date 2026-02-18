package uk.gov.justice.laa.amend.claim.exceptions;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.factories.ReferenceNumberFactory;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@Controller
@AllArgsConstructor
@Slf4j
public class ErrorPageController implements ErrorController {

    private final ReferenceNumberFactory referenceNumberFactory;

    private final MaintenanceService maintenanceService;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        int status = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .map(x -> Integer.parseInt(x.toString()))
                .orElse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        if (status == HttpServletResponse.SC_NOT_FOUND) {
            response.setStatus(status);
            return "not-found";
        } else if (status == HttpServletResponse.SC_SERVICE_UNAVAILABLE) {
            response.setStatus(status);
            model.addAttribute("maintenanceMessage", maintenanceService.getMessage());
            model.addAttribute("maintenanceTitle", maintenanceService.getTitle());
            return "maintenance";
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String referenceNumber = referenceNumberFactory.create();
            model.addAttribute("referenceNumber", referenceNumber);
            log.error(
                    "Something went wrong. Reference: {}. Status: {}. Session ID: {}",
                    referenceNumber,
                    status,
                    request.getSession().getId(),
                    exception);
            return "error";
        }
    }
}
