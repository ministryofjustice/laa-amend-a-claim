package uk.gov.justice.laa.amend.claim.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

@ExtendWith(MockitoExtension.class)
class RedisSessionMetricsTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @InjectMocks
    private RedisSessionMetrics redisSessionMetrics;

    @Test
    void bindTo_registersGaugeWithCorrectNameAndDescription() {
        when(stringRedisTemplate.keys(any())).thenReturn(Set.of());
        MeterRegistry registry = new SimpleMeterRegistry();

        redisSessionMetrics.bindTo(registry);

        Gauge gauge = registry.get("redis.sessions.active").gauge();
        assertThat(gauge.getId().getDescription()).isEqualTo("Number of active Redis sessions");
    }

    @Test
    void activeSessionCount_returnsCountOfSessionKeys() {
        when(stringRedisTemplate.keys(any()))
                .thenReturn(Set.of("spring:session:sessions:abc123", "spring:session:sessions:def456"));

        assertThat(redisSessionMetrics.activeSessionCount()).isEqualTo(2.0);
    }

    @Test
    void activeSessionCount_returnsZeroWhenNoSessionsExist() {
        when(stringRedisTemplate.keys(any())).thenReturn(Set.of());

        assertThat(redisSessionMetrics.activeSessionCount()).isEqualTo(0.0);
    }

    @Test
    void activeSessionCount_returnsZeroWhenKeysReturnsNull() {
        when(stringRedisTemplate.keys(any())).thenReturn(null);

        assertThat(redisSessionMetrics.activeSessionCount()).isEqualTo(0.0);
    }
}
