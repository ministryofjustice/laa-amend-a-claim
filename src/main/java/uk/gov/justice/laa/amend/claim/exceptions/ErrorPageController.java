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
import uk.gov.justice.laa.amend.claim.models.ReferenceNumber;

@Controller
@AllArgsConstructor
@Slf4j
public class ErrorPageController implements ErrorController {

    private final ReferenceNumberFactory referenceNumberFactory;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        int status = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .map(x -> Integer.parseInt(x.toString()))
                .orElse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setStatus(status);
        if (status == HttpServletResponse.SC_NOT_FOUND) {
            return "not-found";
        }
        ReferenceNumber referenceNumber = referenceNumberFactory.create();
        model.addAttribute("referenceNumber", referenceNumber);
        log.error(
                "Something went wrong. Reference: {}. Status: {}. Session ID: {}",
                referenceNumber,
                status,
                request.getSession().getId());
        return "error";
    }
}
