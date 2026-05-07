package com.skybridge.api_portal.flight.dto;

import java.time.Duration;

public record FlightLegView(
        LegEndpointView departure,
        LegEndpointView arrival,
        FlightStatus status,
        String statusLabel,
        Duration scheduledDuration,
        AircraftView aircraft,
        long delayMinutes,
        boolean cancelled
) {}
