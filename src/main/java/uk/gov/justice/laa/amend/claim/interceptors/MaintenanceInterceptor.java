package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class MaintenanceInterceptor implements HandlerInterceptor {

    Path enabled = Paths.get("/config/maintenance/enabled");
    Path message = Paths.get("/config/maintenance/message");
    Path title = Paths.get("/config/maintenance/title");

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException, ServletException {
        log.error("============1");
        log.error("============1");
        log.error("entering maintenance interceptor");
        log.error("============2");
        log.error("============2");

        String enabledValue = new String(
                Files.readAllBytes(enabled), StandardCharsets.UTF_8
        ).trim();

        log.error("RAW ENABLED = {}", enabledValue);

        boolean enabledBoolean = Boolean.parseBoolean(enabledValue);

        log.error("parsed ENABLED = {}", enabledBoolean);


        log.error("============PATH");
        log.error("============PATH");
        log.error("Path String: {}", enabled);
        log.error("File exists?: {}", Files.exists(enabled));
        log.error("Absolute path: {}", enabled.toAbsolutePath());
        log.error("Readable: {}", Files.isReadable(enabled));
        log.error("============PATHEND");
        log.error("============PATHEND");

        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.startsWith("/health")) {

            log.error("============3");
            log.error("============3");
            log.error("inside actuator or health");
            log.error("============4");
            log.error("============4");
            return true;

        }

        if (Files.exists(enabled)) {
            log.error("============EC");
            log.error("============EC");
            log.error("enabled found");
            log.error(Files.readString(enabled));
            log.error("============End");
            log.error("============End");
        }

        if (Files.exists(enabled)
                && Files.readString(enabled).replace("/r", "")
                .trim().equalsIgnoreCase("true")) {

            log.error("============5");
            log.error("============5");
            log.error("if file exists hit");
            log.error("============6");
            log.error("============6");

            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 503);
            request.setAttribute(RequestDispatcher.ERROR_MESSAGE, Files.readString(message).trim());

            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());

            request.getRequestDispatcher("/error").forward(request, response);

            return false;
        }

        return true;
    }

    private boolean error(
            HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
        log.warn("{}: {}", request.getRequestURI(), message);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return false;
    }
}
