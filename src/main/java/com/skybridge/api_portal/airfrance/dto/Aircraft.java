package com.skybridge.api_portal.airfrance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Aircraft(String typeCode, String typeName, String registration) {}
