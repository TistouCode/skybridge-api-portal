package com.skybridge.api_portal.flight.dto;

import java.time.LocalDate;
import java.util.List;

public record FlightView(
        String id,
        String flightNumber,
        AirlineView airline,
        LocalDate scheduleDate,
        FlightStatus status,
        String statusLabel,
        List<String> route,
        List<FlightLegView> legs
) {}
