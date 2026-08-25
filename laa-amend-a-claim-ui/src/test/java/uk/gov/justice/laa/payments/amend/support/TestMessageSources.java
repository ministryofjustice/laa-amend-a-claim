package uk.gov.justice.laa.payments.amend.support;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

/** Builds a {@link MessageSource} backed by the real {@code messages.properties} on classpath. */
public final class TestMessageSources {

  private TestMessageSources() {}

  public static MessageSource real() {
    var messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    return messageSource;
  }
}
