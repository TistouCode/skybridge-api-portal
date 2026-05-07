package com.skybridge.api_portal.flight.dto;

import java.util.List;

public record FlightPage(List<FlightView> flights, PageMetadata page) {}
