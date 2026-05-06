package com.skybridge.api_portal.airfrance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "airfrance.api")
public record AirFranceProperties(String baseUrl, String key) {}
