package com.skybridge.api_portal.flight.dto;

import java.time.OffsetDateTime;

public record LegEndpointView(
        AirportView airport,
        OffsetDateTime scheduledTime,
        OffsetDateTime estimatedTime,
        OffsetDateTime actualTime
) {}
