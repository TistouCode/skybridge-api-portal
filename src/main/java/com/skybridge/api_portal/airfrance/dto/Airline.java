package com.skybridge.api_portal.airfrance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Airline(String code, String name) {}
