package com.skybridge.api_portal.airfrance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OperationalFlight(
        String id,
        Integer flightNumber,
        LocalDate flightScheduleDate,
        Airline airline,
        String flightStatusPublic,
        List<String> route,
        List<FlightLeg> flightLegs
) {}
