package uk.gov.justice.laa.amend.claim.utils;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * Utility class for working with various date classes, whilst being able to mock the wrapper
 * instead of needing to mock the LocalDateTime classes static methods directly. This helps with
 * testing and flexibility in unit tests by decoupling the date logic from the application.
 */
@Component
public class DateWrapperUtil {

  public LocalDateTime timeNow() {
    return LocalDateTime.now();
  }
}
