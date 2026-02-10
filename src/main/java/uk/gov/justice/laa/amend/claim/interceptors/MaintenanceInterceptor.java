package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@Component
@Slf4j
@RequiredArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {

    private final MaintenanceService maintenanceService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String path = request.getRequestURI();
        log.info("MaintenanceInterceptor path: {}", path);

        if (!maintenanceService.maintenanceApplies(request)) {
            log.info("Maintenance off, allow: {}", path);
            return true;
        }

        log.info("Maintenance on, forward: {} to maintenance page", path);
        response.sendRedirect(request.getContextPath() + "/maintenance");
        return false;
    }
}
