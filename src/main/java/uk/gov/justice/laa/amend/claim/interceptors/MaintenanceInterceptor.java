package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {

    private static final Path enabled = Paths.get("/config/maintenance/enabled");

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {
        String path = request.getRequestURI();
        log.info("MaintenanceInterceptor path: {}", path);

        if (!maintenanceApplies(request)) {
            log.info("Maintenance off, allow: {}", path);
            return true;
        }

        log.info("Maintenance on, forward: {} to maintenance page", path);
        response.sendRedirect(request.getContextPath() + "/maintenance");
        return false;
    }

    public boolean maintenanceApplies(HttpServletRequest request) {
        return maintenanceEnabled() && !hasBypassCookie(request);
    }

    private boolean hasBypassCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return false;
        }

        return Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().equals("maintenance_bypass"));
    }

    private boolean maintenanceEnabled() {
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
