package com.skybridge.api_portal.flight.service;

import com.skybridge.api_portal.airfrance.client.AirFranceFlightStatusClient;
import com.skybridge.api_portal.airfrance.dto.FlightStatusResponse;
import com.skybridge.api_portal.flight.dto.FlightPage;
import com.skybridge.api_portal.flight.mapper.FlightMapper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class FlightService {

    private final AirFranceFlightStatusClient client;
    private final FlightMapper mapper;

    public FlightService(AirFranceFlightStatusClient client, FlightMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public FlightPage getFlights(OffsetDateTime startRange, OffsetDateTime endRange, int page, int size) {
        FlightStatusResponse upstream = client.fetchFlights(startRange, endRange, page, size);
        return mapper.toFlightPage(upstream);
    }
}
