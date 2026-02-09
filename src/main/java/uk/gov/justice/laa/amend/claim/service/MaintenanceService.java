package uk.gov.justice.laa.amend.claim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.CreateAssessment201Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for handling maintenance page data and defaults.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MaintenanceService {

      private static final Path maintenanceMessage = Paths.get("/config/maintenance/message");
      private static final  Path maintenanceTitle = Paths.get("/config/maintenance/title");

      private final MessageSource messageSource;

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
          return messageSource.getMessage(
                  messageKey,
                  new Object[0],
                  LocaleContextHolder.getLocale()
          );
      }

}
