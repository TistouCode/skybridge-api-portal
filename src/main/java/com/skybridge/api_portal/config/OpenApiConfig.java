package com.skybridge.api_portal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiPortalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("api-portal")
                        .description("Portal API exposing flight status data sourced from the Air France/KLM open data API.")
                        .version("v1")
                        .contact(new Contact().name("api-portal")));
    }
}
