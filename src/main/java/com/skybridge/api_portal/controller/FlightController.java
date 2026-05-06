package com.skybridge.api_portal.controller;

import com.skybridge.api_portal.airfrance.dto.FlightStatusResponse;
import com.skybridge.api_portal.airfrance.service.FlightStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightStatusService flightStatusService;

    public FlightController(FlightStatusService flightStatusService) {
        this.flightStatusService = flightStatusService;
    }

    @GetMapping
    public FlightStatusResponse getFlights(
            @RequestParam OffsetDateTime startRange,
            @RequestParam OffsetDateTime endRange) {
        return flightStatusService.getFlights(startRange, endRange);
    }
}
