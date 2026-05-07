package com.skybridge.api_portal.airfrance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EstimatedTime(OffsetDateTime value) {}
