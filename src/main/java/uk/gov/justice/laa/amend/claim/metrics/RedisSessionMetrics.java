package uk.gov.justice.laa.amend.claim.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSessionMetrics implements MeterBinder {

  private static final String SESSION_KEY_PATTERN = "spring:session:sessions:*";

  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public void bindTo(@NonNull MeterRegistry registry) {
    Gauge.builder("redis.sessions.active", this, RedisSessionMetrics::activeSessionCount)
        .description("Number of active Redis sessions")
        .register(registry);
  }

  double activeSessionCount() {
    Set<String> keys = stringRedisTemplate.keys(SESSION_KEY_PATTERN);
    return keys == null ? 0 : keys.size();
  }
}
