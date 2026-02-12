package uk.gov.justice.laa.amend.claim.service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafLiteralString;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafString;

/**
 * Service for handling maintenance page data and defaults.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MaintenanceService {
    private static final Path ROOT = Paths.get("/config/maintenance");
    private static final Path MESSAGE = ROOT.resolve("message");
    private static final Path TITLE = ROOT.resolve("title");
    private static final Path ENABLED = ROOT.resolve("enabled");
    private static final Path PASSWORD = ROOT.resolve("bypassPassword");

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
        if (!Files.exists(ENABLED)) {
            return false;
        }
        return readEnabledValue();
    }

    boolean readEnabledValue() {
        try {
            return Boolean.parseBoolean(Files.readString(ENABLED).trim());
        } catch (IOException e) {
            log.info("Failed to read config map", e);
            return true;
        }
    }

    String readBypassValue() {
        try {
            return Files.readString(PASSWORD).trim();
        } catch (IOException e) {
            log.info("Failed to read maintenance bypass cookie", e);
            return "";
        }
    }

    public ThymeleafString getMessage() {
        return readConfigMap(MESSAGE, "maintenance.default.message");
    }

    public ThymeleafString getTitle() {
        return readConfigMap(TITLE, "maintenance.default.title");
    }

    private ThymeleafString readConfigMap(Path path, String messageKey) {
        try {
            if (Files.exists(path)) {
                String message = Files.readString(path).trim();
                if (!message.isBlank()) {
                    return new ThymeleafLiteralString(message);
                }
            }
        } catch (IOException e) {
            log.warn("Could not read config map from file {}", path, e);
        }
        return new ThymeleafMessage(messageKey);
    }
}
