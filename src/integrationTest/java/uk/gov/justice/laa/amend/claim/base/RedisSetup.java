package uk.gov.justice.laa.amend.claim.base;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.embedded.RedisServer;

public class RedisSetup {

    private static RedisServer redisServer;

    @BeforeAll
    public static void setUp() throws IOException {
        redisServer = new RedisServer();
        redisServer.start();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        redisServer.stop();
    }
}
