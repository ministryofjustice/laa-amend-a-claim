package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component
@Slf4j
public class MaintenanceInterceptor implements HandlerInterceptor {

    private static final Path enabled = Paths.get("config/maintenance/enabled");
    private static final Path message = Paths.get("config/maintenance/message");
    private static final Path title = Paths.get("config/maintenance/title");


    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException, ServletException {
        String path = request.getRequestURI();

        if (path.startsWith("/actuator") || path.startsWith("/health")) {
            return true;
        }

        if (Files.exists(enabled)
                && Files.readString(enabled).trim().equalsIgnoreCase("true")) {

            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 503);
            request.setAttribute(RequestDispatcher.ERROR_MESSAGE, Files.readString(message).trim());

            request.getRequestDispatcher("/error").forward(request, response);

            return false;
        }

        return true;
    }
    private boolean error(HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
        log.warn("{}: {}", request.getRequestURI(), message);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return false;
    }
}
