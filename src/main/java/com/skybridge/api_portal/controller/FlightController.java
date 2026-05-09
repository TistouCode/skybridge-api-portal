package com.skybridge.api_portal.controller;

import com.skybridge.api_portal.flight.dto.FlightPage;
import com.skybridge.api_portal.flight.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/flights")
@Validated
@Tag(name = "Flights", description = "Flight status data sourced from the Air France/KLM open data API")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    @Operation(
            summary = "List flights within a time range",
            description = "Returns a paginated list of flights whose scheduled times fall within the given range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flights returned successfully")
    })
    public FlightPage getFlights(
            @Parameter(
                    description = "Inclusive start of the time range (ISO-8601 with offset)",
                    example = "2026-05-06T00:00:00+02:00")
            @RequestParam OffsetDateTime startRange,
            @Parameter(
                    description = "Exclusive end of the time range (ISO-8601 with offset)",
                    example = "2026-05-06T04:00:00+02:00")
            @RequestParam OffsetDateTime endRange,
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (Air France caps at 100)", example = "50")
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size) {
        return flightService.getFlights(startRange, endRange, page, size);
    }
}
