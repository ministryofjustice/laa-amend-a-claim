package uk.gov.justice.laa.payments.amend.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.justice.laa.payments.amend.config.features.FeatureFlagInterceptor;
import uk.gov.justice.laa.payments.amend.interceptors.MaintenanceInterceptor;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private MaintenanceInterceptor maintenanceInterceptor;

  private final FeatureFlagInterceptor featureFlagInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(maintenanceInterceptor)
        .order(Ordered.HIGHEST_PRECEDENCE)
        .addPathPatterns("/**")
        .excludePathPatterns(ALLOWED_URLS);

    registry
        .addInterceptor(featureFlagInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(ALLOWED_URLS);
  }

  private static final String[] ALLOWED_URLS = {
    "/actuator/**",
    "/health",
    "/ping",
    "/maintenance",
    "/error",
    "/assets/**",
    "/css/**",
    "/static/**",
    "/public/**",
    "/js/**",
    "images/**"
  };
}
