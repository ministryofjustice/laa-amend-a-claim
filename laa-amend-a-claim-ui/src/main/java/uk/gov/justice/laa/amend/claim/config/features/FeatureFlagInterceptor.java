package uk.gov.justice.laa.amend.claim.config.features;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;

/**
 * Interceptor to check if a feature flag is enabled before allowing access to a handler method. The
 * {@link RequiresFeatureFlag} annotation can be placed either at the top of a Controller, or at the
 * top of a method within a Controller.
 */
@Component
@RequiredArgsConstructor
public class FeatureFlagInterceptor implements HandlerInterceptor {

  private final FeatureFlagsConfig featureFlagsConfig;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {

    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }

    RequiresFeatureFlag annotation = handlerMethod.getMethodAnnotation(RequiresFeatureFlag.class);
    if (annotation == null) {
      annotation = handlerMethod.getBeanType().getAnnotation(RequiresFeatureFlag.class);
    }

    if (annotation != null) {
      featureFlagsConfig.checkEnabled(annotation.value());
    }

    return true;
  }
}
