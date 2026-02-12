package uk.gov.justice.laa.amend.claim.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.justice.laa.amend.claim.interceptors.ClaimInterceptor;
import uk.gov.justice.laa.amend.claim.interceptors.MaintenanceInterceptor;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private ClaimInterceptor claimInterceptor;
    private MaintenanceInterceptor maintenanceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(claimInterceptor)
                .addPathPatterns("/submissions/*/claims/*/*")
                .excludePathPatterns("/submissions/*/claims/*/assessments/*");

        registry.addInterceptor(maintenanceInterceptor)
                .order(Ordered.HIGHEST_PRECEDENCE)
                .addPathPatterns("/**")
                .excludePathPatterns(ALLOWED_URLS);
    }

    private static final String[] ALLOWED_URLS = {
        "/actuator/**",
        "/health",
        "/maintenance",
        "/error",
        "/assets/**",
        "/css/**",
        "/static/**",
        "/public/**",
        "/js/**",
        "/webjars/**",
        "images/**"
    };
}
