package uk.gov.justice.laa.amend.claim.controllers;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import uk.gov.justice.laa.amend.claim.base.RedisSetup;
import uk.gov.justice.laa.amend.claim.base.WireMockSetup;

public abstract class ControllerIntegrationTest {

    @BeforeAll
    static void beforeAll() throws IOException {
        RedisSetup.setUpRedis();
        WireMockSetup.setupWireMock();
    }

    @AfterAll
    static void afterAll() throws IOException {
        RedisSetup.tearDown();
        WireMockSetup.stopWireMock();
    }
}
