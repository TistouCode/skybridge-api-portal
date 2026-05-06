package com.skybridge.api_portal.airfrance.service;

import com.skybridge.api_portal.airfrance.dto.FlightStatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;

@Service
public class FlightStatusService {

    private final RestClient airFranceRestClient;

    public FlightStatusService(RestClient airFranceRestClient) {
        this.airFranceRestClient = airFranceRestClient;
    }

    public FlightStatusResponse getFlights(OffsetDateTime startRange, OffsetDateTime endRange) {
        return airFranceRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/opendata/flightstatus")
                        .queryParam("startRange", startRange)
                        .queryParam("endRange", endRange)
                        .build())
                .retrieve()
                .body(FlightStatusResponse.class);
    }
}
