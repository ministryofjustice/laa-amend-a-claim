package uk.gov.justice.laa.amend.claim;

import java.util.UUID;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class AbstractPactTest {
  public static final String CONSUMER = "laa-amend-a-claim";
  public static final String CLAIMS_API_PROVIDER = "laa-data-claims-api";
  public static final String FSP_API_PROVIDER = "laa-fee-scheme-platform-api";
  public static final String PROVIDER_API_PROVIDER = "provider-data-api";

  protected static final String UUID_REGEX =
      "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
  protected static final String OFFICE_CODE_REGEX = "[0-9A-Z]{6}";

  protected static final UUID CLAIM_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  protected static final String EXAMPLE_AUTH_TOKEN = "e37da3d4-b8bc-4204-9528-deb6fa3bb39d";

  @MockitoBean OAuth2AuthorizedClientManager authorizedClientManager;
}
