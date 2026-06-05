package uk.gov.justice.laa.amend.claim.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.utils.DateWrapperUtil;

@ExtendWith(MockitoExtension.class)
class OutageBannerAdviceTest {

  @Mock DateWrapperUtil dateWrapperUtil;

  ZonedDateTime rootTime =
      ZonedDateTime.of(LocalDate.of(2026, 1, 1), LocalTime.of(0, 0), ZoneId.systemDefault());

  @Nested
  class GetMaintenanceBannerEnabledTests {

    @Test
    void shouldBeDisabledIfCurrentTimePassed() {
      ZonedDateTime disableAtTime = rootTime.minusDays(1);
      ZonedDateTime currentTime = rootTime;
      when(dateWrapperUtil.timeNow()).thenReturn(LocalDateTime.from(currentTime));
      OutageBannerAdvice outageBannerAdvice =
          new OutageBannerAdvice(disableAtTime, "Banner text", dateWrapperUtil);

      var result = outageBannerAdvice.getOutageBannerEnabled();

      assertThat(result).isFalse();
    }

    @Test
    void shouldBeDisabledIfCurrentTimeNotPassed() {
      ZonedDateTime disableAtTime = rootTime.plusDays(1);
      ZonedDateTime currentTime = rootTime;
      when(dateWrapperUtil.timeNow()).thenReturn(LocalDateTime.from(currentTime));
      OutageBannerAdvice outageBannerAdvice =
          new OutageBannerAdvice(disableAtTime, "Banner text", dateWrapperUtil);

      var result = outageBannerAdvice.getOutageBannerEnabled();

      assertThat(result).isTrue();
    }
  }

  @Nested
  class GetOutageBannerMessageTests {

    @Test
    void shouldReturnMessage() {
      String message = "Outage message";
      OutageBannerAdvice outageBannerAdvice =
          new OutageBannerAdvice(rootTime, message, dateWrapperUtil);

      var result = outageBannerAdvice.getOutageBannerMessage();

      assertThat(result).isEqualTo(message);
    }
  }
}
