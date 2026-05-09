package com.skybridge.api_portal.flight.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFlightSearchException extends RuntimeException {

    public InvalidFlightSearchException(String message) {
        super(message);
    }
}
