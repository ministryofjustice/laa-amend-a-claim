package uk.gov.justice.laa.amend.claim.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.justice.laa.amend.claim.config.AppProperties;

@RequiredArgsConstructor
@ControllerAdvice
public class AppPropertiesControllerAdvice {

    private final AppProperties appProperties;

    @ModelAttribute("app")
    public AppProperties app() {
        return appProperties;
    }
}
