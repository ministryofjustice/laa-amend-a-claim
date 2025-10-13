package uk.gov.justice.laa.amend.claim.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;

@Slf4j
@Configuration
@EnableConfigurationProperties({ClaimsApiProperties.class})
public class WebClientConfig {


    @Bean
    public ClaimsApiClient claimsApiClient(final ClaimsApiProperties properties) {
        final WebClient webClient = createWebClient(properties);
        final WebClientAdapter webClientAdapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();

        return factory.createClient(ClaimsApiClient.class);
    }

    /**
     * Creates a WebClient instance using the provided configuration properties.
     *
     * @param apiProperties The configuration properties for the API.
     * @return A WebClient instance.
     */
    public static WebClient createWebClient(final ClaimsApiProperties apiProperties) {
        final ExchangeStrategies strategies =
                ExchangeStrategies.builder().codecs(ClientCodecConfigurer::defaultCodecs).build();
        return WebClient.builder()
                .baseUrl(apiProperties.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, apiProperties.getAccessToken())
                .exchangeStrategies(strategies)
                .build();
    }
}