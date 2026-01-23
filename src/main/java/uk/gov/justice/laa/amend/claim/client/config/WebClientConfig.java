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
import uk.gov.justice.laa.amend.claim.client.MicrosoftGraphApiClient;

@Slf4j
@Configuration
@EnableConfigurationProperties({ClaimsApiProperties.class, MicrosoftGraphApiProperties.class})
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ClaimsApiClient claimsApiClient(WebClient.Builder webClientBuilder, ClaimsApiProperties properties) {
        ExchangeStrategies strategies = ExchangeStrategies
            .builder()
            .codecs(ClientCodecConfigurer::defaultCodecs)
            .build();

        WebClient webClient = webClientBuilder
            .baseUrl(properties.getUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, properties.getAccessToken())
            .exchangeStrategies(strategies)
            .build();

        WebClientAdapter webClientAdapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();

        return factory.createClient(ClaimsApiClient.class);
    }

    @Bean
    public MicrosoftGraphApiClient microsoftGraphApiClient(WebClient.Builder webClientBuilder, MicrosoftGraphApiProperties properties) {
        WebClient webClient = webClientBuilder
            .baseUrl(properties.getUrl())
            .build();
        WebClientAdapter webClientAdapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();
        return factory.createClient(MicrosoftGraphApiClient.class);
    }
}