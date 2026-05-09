package com.skybridge.api_portal.controller;

import com.skybridge.api_portal.flight.service.InvalidFlightSearchException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidFlightSearchException.class)
    public ProblemDetail handleInvalidSearch(InvalidFlightSearchException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid flight search", ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .map(this::formatViolation)
                .collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, "Invalid request parameters", detail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String detail = "Parameter '" + ex.getName() + "' has invalid value '" + ex.getValue() + "'";
        return problem(HttpStatus.BAD_REQUEST, "Invalid request parameters", detail);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParam(MissingServletRequestParameterException ex) {
        String detail = "Required parameter '" + ex.getParameterName() + "' is missing";
        return problem(HttpStatus.BAD_REQUEST, "Missing request parameters", detail);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ProblemDetail handleUpstreamFailure(RestClientResponseException ex) {
        log.warn("Upstream Air France API error: {} {}", ex.getStatusCode(), ex.getMessage());
        return problem(HttpStatus.BAD_GATEWAY, "Upstream API error",
                "The Air France flight status API returned " + ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error handling request", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "An unexpected error occurred");
    }

    private String formatViolation(ConstraintViolation<?> v) {
        String path = v.getPropertyPath().toString();
        String name = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
        return name + ": " + v.getMessage();
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        return pd;
    }
}
