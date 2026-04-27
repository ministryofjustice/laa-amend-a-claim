package uk.gov.justice.laa.amend.claim.session;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import uk.gov.justice.laa.amend.claim.base.RedisSetup;
import uk.gov.justice.laa.amend.claim.service.SessionService;

@SpringBootTest
public class SessionIntegrationTest {

  private static final String SESSION_PATTERN = "spring:session:sessions:*";
  @Autowired private StringRedisTemplate redisTemplate;

  @Autowired private SessionService sessionService;

  @BeforeEach
  void cleanRedis() {
    var options = ScanOptions.scanOptions().match(SESSION_PATTERN).count(100).build();
    Set<String> keys = new HashSet<>();

    try (Cursor<String> cursor = redisTemplate.scan(options)) {
      while (cursor.hasNext()) {
        keys.add(cursor.next());
      }
    }
    if (!keys.isEmpty()) {
      redisTemplate.delete(keys);
    }
  }

  @Test
  void shouldReturnZeroWhenNoSessions() {
    int result = sessionService.getActiveSessionCount();
    assertThat(result).isEqualTo(0);
  }

  @Test
  void shouldCountActiveSessions() {
    redisTemplate.opsForValue().set("spring:session:sessions:1", "data", Duration.ofMinutes(30));
    redisTemplate.opsForValue().set("spring:session:sessions:2", "data", Duration.ofMinutes(30));

    int result = sessionService.getActiveSessionCount();
    assertThat(result).isEqualTo(2);
  }

  @Test
  void shouldRemoveExpiredSessions() {
    redisTemplate.opsForValue().set("spring:session:sessions:1", "data");

    // simulate expiry
    redisTemplate.delete("spring:session:sessions:1");

    int result = sessionService.getActiveSessionCount();
    assertThat(result).isEqualTo(0);
  }
}
