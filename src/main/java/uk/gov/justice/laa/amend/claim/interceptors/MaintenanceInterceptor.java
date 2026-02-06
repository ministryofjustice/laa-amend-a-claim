package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class MaintenanceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException, ServletException {

        String path = request.getRequestURI();
        log.error("MaintenanceInterceptor path: {}", path);

        if (!maintenanceEnabled()) {
            log.error("Maintenance off, allow: {}", path);
            return true;
        }

        if (ALLOWED_URLS.stream().anyMatch(path::startsWith)) {
            log.error("Maintenance on, bypass: {}", path);
            return true;
        }

        log.error("Maintenance on, forward: {} to maintenance page", path);
        request.getRequestDispatcher("/maintenance").forward(request, response);
        return false;
    }

    private static final List<String> ALLOWED_URLS = List.of(
            "/actuator/**", "/health", "/maintenance", "/error", "/assets/**",
            "/css/**", "/static/**", "/public/**", "js/**", "/webjars/**", "images/**");

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
