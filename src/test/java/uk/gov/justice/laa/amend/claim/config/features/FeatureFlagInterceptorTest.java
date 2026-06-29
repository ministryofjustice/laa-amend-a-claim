package uk.gov.justice.laa.amend.claim.config.features;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.exceptions.FeatureNotEnabledException;

@ExtendWith(MockitoExtension.class)
class FeatureFlagInterceptorTest {

  @Mock private FeatureFlagsConfig featureFlagsConfig;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  private FeatureFlagInterceptor interceptor;

  @BeforeEach
  void beforeEach() {
    this.interceptor = new FeatureFlagInterceptor(featureFlagsConfig);
  }

  @Test
  void shouldReturnTrueWhenNotHandlerMethod() {
    boolean result = interceptor.preHandle(request, response, new Object());

    assertThat(result).isTrue();

    verifyNoInteractions(featureFlagsConfig);
  }

  @Test
  void shouldCheckClassLevelFlagWhenMethodHasNoAnnotation() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(new ClassLevelController(), "noAnnotation");

    boolean result = interceptor.preHandle(request, response, handlerMethod);

    assertThat(result).isTrue();
    verify(featureFlagsConfig).checkEnabled(Feature.CLAIM_AMENDMENT);
  }

  @Test
  void shouldPreferMethodLevelFlagOverClassLevel() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(new ClassLevelController(), "methodOverride");

    interceptor.preHandle(request, response, handlerMethod);

    verify(featureFlagsConfig).checkEnabled(Feature.BULK_UPLOAD);
    verify(featureFlagsConfig, times(0)).checkEnabled(Feature.CLAIM_AMENDMENT);
  }

  @Test
  void shouldUseMethodLevelFlagWhenNoClassLevelAnnotation() throws Exception {
    HandlerMethod handlerMethod =
        handlerMethod(new NoClassAnnotationController(), "methodAnnotated");

    interceptor.preHandle(request, response, handlerMethod);

    verify(featureFlagsConfig).checkEnabled(Feature.CLAIM_AMENDMENT);
  }

  @Test
  void shouldDoNothingWhenNoAnnotationPresent() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(new NoClassAnnotationController(), "noAnnotation");

    boolean result = interceptor.preHandle(request, response, handlerMethod);

    assertThat(result).isTrue();
    verifyNoInteractions(featureFlagsConfig);
  }

  @Test
  void shouldPropagateExceptionWhenFeatureDisabled() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(new ClassLevelController(), "noAnnotation");
    doThrowOnCheck(Feature.CLAIM_AMENDMENT);

    assertThrows(
        FeatureNotEnabledException.class,
        () -> interceptor.preHandle(request, response, handlerMethod));
  }

  // --- helpers ---

  private void doThrowOnCheck(Feature feature) {
    org.mockito.Mockito.doThrow(new FeatureNotEnabledException("Not enabled"))
        .when(featureFlagsConfig)
        .checkEnabled(feature);
  }

  private HandlerMethod handlerMethod(Object bean, String methodName) throws NoSuchMethodException {
    Method method = bean.getClass().getMethod(methodName);
    return new HandlerMethod(bean, method);
  }

  @RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
  static class ClassLevelController {
    public void noAnnotation() {}

    @RequiresFeatureFlag(Feature.BULK_UPLOAD)
    public void methodOverride() {}
  }

  static class NoClassAnnotationController {
    public void noAnnotation() {}

    @RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
    public void methodAnnotated() {}
  }
}
