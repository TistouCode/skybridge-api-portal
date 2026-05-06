package com.skybridge.api_portal.airfrance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FlightLeg(
        LegEndpoint departureInformation,
        LegEndpoint arrivalInformation,
        String legStatusPublic
) {}
