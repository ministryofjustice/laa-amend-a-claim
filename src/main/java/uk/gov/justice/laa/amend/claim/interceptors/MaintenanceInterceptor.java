package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class MaintenanceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {

        String path = request.getRequestURI();
        log.info("MaintenanceInterceptor path: {}", path);

        if (!maintenanceEnabled()) {
            log.info("Maintenance off, allow: {}", path);
            return true;
        }

        log.info("Maintenance on, forward: {} to maintenance page", path);
        response.sendRedirect(request.getContextPath() + "/maintenance");
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
