package uk.gov.justice.laa.amend.claim.controllers;

import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;

@ControllerAdvice
public class OutageBannerAdvice {

  private final ChronoLocalDateTime<?> disableAtTime;
  private final String outageBannerMessage;

  private final DateWrapperUtil dateWrapperUtil;

  public OutageBannerAdvice(
      @Value("${maintenance.disable-at-time}") ZonedDateTime disableAtTime,
      @Value("${maintenance.outage-banner-message}") String outageBannerMessage,
      DateWrapperUtil dateWrapperUtil) {
    this.disableAtTime = ChronoLocalDateTime.from(disableAtTime);
    this.outageBannerMessage = outageBannerMessage;
    this.dateWrapperUtil = dateWrapperUtil;
  }

  @ModelAttribute("outageBannerEnabled")
  public boolean getOutageBannerEnabled() {
    var currentTime = dateWrapperUtil.timeNow();
    return currentTime.isBefore(disableAtTime);
  }

  @ModelAttribute("outageBannerMessage")
  public String getOutageBannerMessage() {
    return outageBannerMessage;
  }
}
