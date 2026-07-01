package uk.gov.justice.laa.amend.claim.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.exceptions.FeatureNotEnabledException;

/**
 * Annotation to indicate that a feature flag is required for a controller or method. The {@link
 * uk.gov.justice.laa.amend.claim.config.features.FeatureFlagInterceptor} handles feature flag
 * checks and throws a {@link FeatureNotEnabledException} if not enabled.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresFeatureFlag {

  Feature[] value();
}
