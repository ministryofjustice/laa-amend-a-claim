package uk.gov.justice.laa.amend.claim.repositories;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;
import uk.gov.justice.laa.amend.claim.config.CacheProperties;
import uk.gov.justice.laa.amend.claim.models.Claim;

import java.io.IOException;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("local")
public class CacheRepositoryIntegrationTest {

    private static RedisServer redisServer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private CacheProperties cacheProperties;

    @BeforeAll
    public static void setUp() throws IOException {
        redisServer = new RedisServer();
        redisServer.start();
    }

    @Test
    public void testGetReturnsNullWhenNoMatchFound() {
        String key = UUID.randomUUID().toString();
        Claim result = cacheRepository.get(key, Claim.class);
        Assertions.assertNull(result);
    }

    @Test
    public void testGetReturnsValueWhenMatchFound() {
        String key = UUID.randomUUID().toString();
        Claim claim = new Claim();
        claim.setClaimId("123");
        redisTemplate.opsForValue().set(key, claim);
        Claim result = cacheRepository.get(key, Claim.class);
        Assertions.assertEquals(claim, result);
    }

    @Test
    public void testGetThrowsExceptionWhenMatchFoundButMappingFails() {
        String key = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(key, "foo");
        Assertions.assertThrows(RuntimeException.class, () -> cacheRepository.get(key, Claim.class));
    }

    @Test
    public void testSetStoresValueInRedis() {
        String key = UUID.randomUUID().toString();
        Claim claim = new Claim();
        claim.setClaimId("123");
        cacheRepository.set(key, claim);

        Object result = redisTemplate.opsForValue().get(key);
        Assertions.assertNotNull(result);
    }

    @Test
    public void testSetAppliesATtl() {
        String key = UUID.randomUUID().toString();
        Claim claim = new Claim();
        claim.setClaimId("123");
        cacheRepository.set(key, claim);

        Long timeToLiveInSeconds = redisTemplate.getExpire(key);
        Assertions.assertNotNull(timeToLiveInSeconds);
        Assertions.assertEquals(cacheProperties.getTimeToLiveInSeconds(), timeToLiveInSeconds);
    }

    @Test
    public void testDeleteRemovesValue() {
        String key = UUID.randomUUID().toString();
        Claim claim = new Claim();
        claim.setClaimId("123");
        redisTemplate.opsForValue().set(key, claim);

        Assertions.assertNotNull(redisTemplate.opsForValue().get(key));
        cacheRepository.delete(key);
        Assertions.assertNull(redisTemplate.opsForValue().get(key));
    }

    @AfterAll
    public static void tearDown() throws IOException {
        redisServer.stop();
    }
}
