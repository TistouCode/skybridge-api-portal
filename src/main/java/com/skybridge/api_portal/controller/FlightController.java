package com.skybridge.api_portal.controller;

import com.skybridge.api_portal.airfrance.dto.FlightStatusResponse;
import com.skybridge.api_portal.airfrance.service.FlightStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/flights")
@Tag(name = "Flights", description = "Flight status data sourced from the Air France/KLM open data API")
public class FlightController {

    private final FlightStatusService flightStatusService;

    public FlightController(FlightStatusService flightStatusService) {
        this.flightStatusService = flightStatusService;
    }

    @GetMapping
    @Operation(
            summary = "List flights within a time range",
            description = "Returns operational flights whose scheduled times fall within the given range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flights returned successfully")
    })
    public FlightStatusResponse getFlights(
            @Parameter(
                    description = "Inclusive start of the time range (ISO-8601 with offset)",
                    example = "2026-05-06T00:00:00+02:00")
            @RequestParam OffsetDateTime startRange,
            @Parameter(
                    description = "Exclusive end of the time range (ISO-8601 with offset)",
                    example = "2026-05-06T04:00:00+02:00")
            @RequestParam OffsetDateTime endRange) {
        return flightStatusService.getFlights(startRange, endRange);
    }
}
