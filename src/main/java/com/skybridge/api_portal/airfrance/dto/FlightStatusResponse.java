package com.skybridge.api_portal.airfrance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FlightStatusResponse(
        List<OperationalFlight> operationalFlights,
        PageInfo page
) {}
