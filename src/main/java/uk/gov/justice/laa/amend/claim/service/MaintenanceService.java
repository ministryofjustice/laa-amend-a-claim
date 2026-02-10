package uk.gov.justice.laa.amend.claim.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for handling maintenance page data and defaults.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MaintenanceService {
    private static final Path maintenanceMessage = Paths.get("/config/maintenance/message");
    private static final Path maintenanceTitle = Paths.get("/config/maintenance/title");
    private static final Path enabled = Paths.get("/config/maintenance/enabled");
    private static final Path bypassPath = Paths.get("/config/maintenance/bypassPassword");

    private final MessageSource messageSource;

    public boolean maintenanceApplies(HttpServletRequest request) throws IOException {
        return maintenanceEnabled() && !hasBypassCookie(request);
    }

     boolean hasBypassCookie(HttpServletRequest request) throws IOException {
        log.info("Maintenance on, checking for cookie");

        if (request.getCookies() == null) {
            log.info("Maintenance on, no cookies");
            return false;
        }

        String bypassPassword = readBypassValue();
        log.info("Maintenance on, bypassPassword: {}", bypassPassword);

        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getValue().equals(bypassPassword));
    }

     boolean maintenanceEnabled() {
            if (!Files.exists(enabled)) {
                return false;
            }
            return readEnabledValue();

    }

    boolean readEnabledValue() {
        try {
            return Boolean.parseBoolean(Files.readString(bypassPath).trim());
        }
        catch (IOException e) {
            log.info("Failed to read config map", e);
            return true;
        }
    }

    String readBypassValue() {
        try {
            return Files.readString(bypassPath).trim();
        }
        catch (IOException e) {
            log.info("Failed to read maintenance bypass cookie", e);
            return "";
        }
    }

    public String getMessage() {
        return readConfigMap(maintenanceMessage, "maintenance.default.message");
    }

    public String getTitle() {
        return readConfigMap(maintenanceTitle, "maintenance.default.title");
    }

    private String readConfigMap(Path path, String messageKey) {
        try {
            if (Files.exists(path)) {
                String message = Files.readString(path).trim();
                if (!message.isBlank()) {
                    return message;
                }
            }
        } catch (IOException e) {
            log.warn("Could not read config map from file {}", path, e);
        }
        return messageSource.getMessage(messageKey, new Object[0], LocaleContextHolder.getLocale());
    }
}
