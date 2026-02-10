package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@RequiredArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {

    private static final Path enabled = Paths.get("/config/maintenance/enabled");
    private static final Path bypassPath = Paths.get("/config/maintenance/bypassPassword");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
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

    public boolean maintenanceApplies(HttpServletRequest request) throws IOException {
        return maintenanceEnabled() && !hasBypassCookie(request);
    }

    private boolean hasBypassCookie(HttpServletRequest request) throws IOException {
        if (request.getCookies() == null) {
            return false;
        }

        String bypassPassword = Files.readString(bypassPath).trim();

        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getValue().equals(bypassPassword));
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
