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

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException, ServletException {
        if (!maintenanceEnabled()) {
            return true;
        }

        String path = request.getRequestURI();

        if (path.equals("/maintenance") || path.startsWith("/actuator") || path.startsWith("/health")) {
            return true;
        }

        request.getRequestDispatcher("/maintenance").forward(request, response);
        return false;
    }

    private boolean maintenanceEnabled() {
        Path enabled = Paths.get("/config/maintenance/enabled");
        try {
            if (!Files.exists(enabled)) {
                return false;
            }
            return Boolean.parseBoolean(Files.readString(enabled).trim());
        } catch (IOException e) {
            log.error("Failed to read maintenance flag", e);
            return false;
        }
    }
}
