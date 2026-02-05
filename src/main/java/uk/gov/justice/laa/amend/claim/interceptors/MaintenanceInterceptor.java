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
        log.error("before check for loop");

        if  (request.getRequestURI().equals("/maintenance")) {
            return true;
        }




        log.error("entering maintenance interceptor");




        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.startsWith("/health")) {
            return true;
        }

        if (Files.exists(enabled)
                && Files.readString(enabled).replace("/r", "")
                .trim().equalsIgnoreCase("true")) {

            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 503);

            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());

            request.getRequestDispatcher("/maintenance").forward(request, response);

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
