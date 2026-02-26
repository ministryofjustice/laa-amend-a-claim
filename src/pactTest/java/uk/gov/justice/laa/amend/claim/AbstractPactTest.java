package uk.gov.justice.laa.amend.claim;

import java.util.UUID;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class AbstractPactTest {
    public static final String CONSUMER = "laa-amend-a-claim";
    public static final String PROVIDER = "laa-data-claims-api";

    protected static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    protected static final UUID CLAIM_ID = UUID.randomUUID();

    @MockitoBean
    OAuth2AuthorizedClientManager authorizedClientManager;
}
