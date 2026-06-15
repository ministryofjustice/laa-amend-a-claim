package uk.gov.justice.laa.amend.claim.factories;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.FeeSchemePlatformApiClient;
import uk.gov.justice.laa.amend.claim.client.FeeSchemePlatformApiClient.FeeCode;
import uk.gov.justice.laa.amend.claim.client.FeeSchemePlatformApiClient.FeeCodes;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;

@ExtendWith(MockitoExtension.class)
class AvailableFeeCodesFactoryTest {

  @Mock FeeSchemePlatformApiClient feeSchemePlatformApiClient;

  AvailableFeeCodesFactory availableFeeCodesFactory;

  @BeforeEach
  void beforeEach() {
    availableFeeCodesFactory = new AvailableFeeCodesFactory(feeSchemePlatformApiClient);
  }

  @Test
  void shouldMapFeeCodesToMap() {
    var one = new FeeCode("ONE", "First code");
    var two = new FeeCode("TWO", "Second code");
    when(feeSchemePlatformApiClient.getFeeCodes(anyString()))
        .thenReturn(Mono.just(new FeeCodes(List.of(one, two))));

    var result = availableFeeCodesFactory.getAvailableFeeCodes(AreaOfLaw.LEGAL_HELP);

    assertThat(result).hasSize(2);
    assertThat(result).containsKey("ONE");
    assertThat(result).containsKey("TWO");
    assertThat(result.get("ONE")).isEqualTo("ONE - First code");
    assertThat(result.get("TWO")).isEqualTo("TWO - Second code");
  }
}
