package uk.gov.justice.laa.amend.claim;

import java.util.UUID;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class AbstractPactTest {
    public static final String CONSUMER = "laa-amend-a-claim";
    public static final String PROVIDER = "laa-data-claims-api";

    protected static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    protected static final UUID CLAIM_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
    protected static final String EXAMPLE_AUTH_TOKEN = "e37da3d4-b8bc-4204-9528-deb6fa3bb39d";

    @MockitoBean
    OAuth2AuthorizedClientManager authorizedClientManager;
}
