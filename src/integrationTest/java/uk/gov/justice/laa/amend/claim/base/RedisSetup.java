package uk.gov.justice.laa.amend.claim.base;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.embedded.RedisServer;

import java.io.IOException;

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