package uk.gov.justice.laa.amend.claim.exceptions;

import static jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.factories.ReferenceNumberFactory;

@Controller
@AllArgsConstructor
@Slf4j
public class ErrorPageController implements ErrorController {

    private static final Set<Integer> NOT_FOUND_STATUS_CODES = Set.of(SC_NOT_FOUND, SC_FORBIDDEN);

    private final ReferenceNumberFactory referenceNumberFactory;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        int status = Optional.ofNullable(request.getAttribute(ERROR_STATUS_CODE))
                .map(x -> Integer.parseInt(x.toString()))
                .orElse(SC_INTERNAL_SERVER_ERROR);
        if (NOT_FOUND_STATUS_CODES.contains(status)) {
            return handleNotFound(response);
        } else {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            String referenceNumber = referenceNumberFactory.create();
            model.addAttribute("referenceNumber", referenceNumber);
            log.error(
                    "Something went wrong. Reference: {}. Status: {}. Session ID: {}",
                    referenceNumber,
                    status,
                    request.getSession().getId());
            return "error";
        }
    }

    @RequestMapping("not-found")
    public String handleNotFound(HttpServletResponse response) {
        response.setStatus(SC_NOT_FOUND);
        return "not-found";
    }
}
