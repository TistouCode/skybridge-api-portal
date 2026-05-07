package com.skybridge.api_portal.airfrance.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AirFranceProperties.class)
public class AirFranceClientConfig {

    private static final Logger log = LoggerFactory.getLogger(AirFranceClientConfig.class);

    @Bean
    public RestClient airFranceRestClient(AirFranceProperties props) {
        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("API-Key", props.key())
                .defaultHeader("Accept", "application/hal+json")
                .defaultHeader("Accept-Encoding", "identity")
                .requestInterceptor((request, body, execution) -> {
                    long startNanos = System.nanoTime();
                    log.info("Air France → {} {}", request.getMethod(), request.getURI());
                    var response = execution.execute(request, body);
                    long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
                    log.info("Air France ← {} ({} ms)", response.getStatusCode(), durationMs);
                    return response;
                })
                .build();
    }
}
