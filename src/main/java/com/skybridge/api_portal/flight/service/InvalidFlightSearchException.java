package com.skybridge.api_portal.flight.service;

public class InvalidFlightSearchException extends RuntimeException {

    public InvalidFlightSearchException(String message) {
        super(message);
    }
}
