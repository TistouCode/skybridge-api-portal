package com.skybridge.api_portal.airfrance.client;

import com.skybridge.api_portal.airfrance.config.AirFranceProperties;
import com.skybridge.api_portal.airfrance.dto.FlightStatusResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AirFranceFlightStatusClient {

    private static final DateTimeFormatter REQUEST_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final RestClient airFranceRestClient;
    private final String baseUrl;

    public AirFranceFlightStatusClient(RestClient airFranceRestClient, AirFranceProperties props) {
        this.airFranceRestClient = airFranceRestClient;
        this.baseUrl = props.baseUrl();
    }

    public FlightStatusResponse fetchFlights(OffsetDateTime startRange, OffsetDateTime endRange,
                                             int pageNumber, int pageSize) {
        URI uri = URI.create(baseUrl
                + "/opendata/flightstatus"
                + "?startRange=" + encode(startRange)
                + "&endRange=" + encode(endRange)
                + "&pageNumber=" + pageNumber
                + "&pageSize=" + pageSize);
        return airFranceRestClient.get()
                .uri(uri)
                .retrieve()
                .body(FlightStatusResponse.class);
    }

    private static String encode(OffsetDateTime value) {
        return URLEncoder.encode(REQUEST_TIMESTAMP_FORMAT.format(value), StandardCharsets.UTF_8);
    }
}
