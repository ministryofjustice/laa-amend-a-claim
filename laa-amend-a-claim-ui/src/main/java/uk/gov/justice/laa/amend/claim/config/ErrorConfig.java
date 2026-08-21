package uk.gov.justice.laa.amend.claim.config;

import org.springframework.boot.web.error.ErrorPage;
import org.springframework.boot.web.error.ErrorPageRegistrar;
import org.springframework.boot.web.error.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorConfig {

  @Bean
  public ErrorPageRegistrar errorPageRegistrar() {
    return (ErrorPageRegistry registry) -> {
      registry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/not-found"));
      registry.addErrorPages(new ErrorPage(HttpStatus.CONTENT_TOO_LARGE, "/error"));
    };
  }
}
