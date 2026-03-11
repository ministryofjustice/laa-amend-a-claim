package uk.gov.justice.laa.amend.claim.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.justice.laa.amend.claim.base.WireMockSetup;

@SpringBootTest
public class ProviderApiClientIntegrationTest extends WireMockSetup {

    @Autowired
    private ProviderApiClient providerApiClient;

    @Test
    void testPingWhenOkResponse() {
        setupGetProviderDetailsApiHealthStub();

        Assertions.assertDoesNotThrow(() -> providerApiClient.ping().block());
    }

    // TODO add integration test with status: DOWN response
}
