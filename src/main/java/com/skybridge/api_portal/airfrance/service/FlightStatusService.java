package com.skybridge.api_portal.airfrance.service;

import com.skybridge.api_portal.airfrance.config.AirFranceProperties;
import com.skybridge.api_portal.airfrance.dto.FlightStatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FlightStatusService {

    private static final DateTimeFormatter REQUEST_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final RestClient airFranceRestClient;
    private final String baseUrl;

    public FlightStatusService(RestClient airFranceRestClient, AirFranceProperties props) {
        this.airFranceRestClient = airFranceRestClient;
        this.baseUrl = props.baseUrl();
    }

    public FlightStatusResponse getFlights(OffsetDateTime startRange, OffsetDateTime endRange) {
        URI uri = URI.create(baseUrl
                + "/opendata/flightstatus"
                + "?startRange=" + encode(startRange)
                + "&endRange=" + encode(endRange));
        return airFranceRestClient.get()
                .uri(uri)
                .retrieve()
                .body(FlightStatusResponse.class);
    }

    private static String encode(OffsetDateTime value) {
        return URLEncoder.encode(REQUEST_TIMESTAMP_FORMAT.format(value), StandardCharsets.UTF_8);
    }
}
