package uk.gov.justice.laa.amend.claim.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.justice.laa.amend.claim.interceptors.ClaimInterceptor;
import uk.gov.justice.laa.amend.claim.interceptors.MaintenanceInterceptor;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ClaimInterceptor())
            .addPathPatterns("/submissions/*/claims/*/*")
            .excludePathPatterns("/submissions/*/claims/*/assessments/*");

        registry.addInterceptor(new MaintenanceInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/**", "/health");
    }
}
