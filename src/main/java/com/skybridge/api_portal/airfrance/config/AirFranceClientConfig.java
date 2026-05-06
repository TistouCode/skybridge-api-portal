package com.skybridge.api_portal.airfrance.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AirFranceProperties.class)
public class AirFranceClientConfig {

    @Bean
    public RestClient airFranceRestClient(AirFranceProperties props) {
        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("API-Key", props.key())
                .defaultHeader("Accept", "application/hal+json")
                .build();
    }
}
