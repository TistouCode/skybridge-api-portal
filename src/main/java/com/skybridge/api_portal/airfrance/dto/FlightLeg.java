package com.skybridge.api_portal.airfrance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Duration;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FlightLeg(
        LegEndpoint departureInformation,
        LegEndpoint arrivalInformation,
        String legStatusPublic,
        String legStatusPublicLangTransl,
        Duration scheduledFlightDuration,
        Duration departureDateTimeDifference,
        Duration arrivalDateTimeDifference,
        Aircraft aircraft
) {}
