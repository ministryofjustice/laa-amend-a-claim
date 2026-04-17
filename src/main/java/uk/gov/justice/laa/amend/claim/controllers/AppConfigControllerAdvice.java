package uk.gov.justice.laa.amend.claim.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.justice.laa.amend.claim.config.AppConfig;

@RequiredArgsConstructor
@ControllerAdvice
@EnableConfigurationProperties(AppConfig.class)
public class AppConfigControllerAdvice {

    private final AppConfig appConfig;

    @ModelAttribute("app")
    public AppConfig app() {
        return appConfig;
    }
}
