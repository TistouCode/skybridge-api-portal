package com.skybridge.api_portal.flight.mapper;

import com.skybridge.api_portal.airfrance.dto.Aircraft;
import com.skybridge.api_portal.airfrance.dto.Airline;
import com.skybridge.api_portal.airfrance.dto.Airport;
import com.skybridge.api_portal.airfrance.dto.City;
import com.skybridge.api_portal.airfrance.dto.Country;
import com.skybridge.api_portal.airfrance.dto.EstimatedTime;
import com.skybridge.api_portal.airfrance.dto.FlightLeg;
import com.skybridge.api_portal.airfrance.dto.FlightStatusResponse;
import com.skybridge.api_portal.airfrance.dto.FlightTimes;
import com.skybridge.api_portal.airfrance.dto.LegEndpoint;
import com.skybridge.api_portal.airfrance.dto.OperationalFlight;
import com.skybridge.api_portal.airfrance.dto.PageInfo;
import com.skybridge.api_portal.flight.dto.AircraftView;
import com.skybridge.api_portal.flight.dto.AirlineView;
import com.skybridge.api_portal.flight.dto.AirportView;
import com.skybridge.api_portal.flight.dto.FlightLegView;
import com.skybridge.api_portal.flight.dto.FlightPage;
import com.skybridge.api_portal.flight.dto.FlightStatus;
import com.skybridge.api_portal.flight.dto.FlightView;
import com.skybridge.api_portal.flight.dto.LegEndpointView;
import com.skybridge.api_portal.flight.dto.PageMetadata;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class FlightMapper {

    public FlightPage toFlightPage(FlightStatusResponse source) {
        if (source == null) {
            return new FlightPage(List.of(), new PageMetadata(0, 0, 0, 0));
        }
        List<FlightView> flights = Optional.ofNullable(source.operationalFlights())
                .orElseGet(List::of)
                .stream()
                .map(this::toFlightView)
                .toList();
        return new FlightPage(flights, toPageMetadata(source.page()));
    }

    private FlightView toFlightView(OperationalFlight source) {
        return new FlightView(
                source.id(),
                buildFlightNumber(source.airline(), source.flightNumber()),
                toAirlineView(source.airline()),
                source.flightScheduleDate(),
                mapStatus(source.flightStatusPublic()),
                source.flightStatusPublicLangTransl(),
                source.route(),
                Optional.ofNullable(source.flightLegs())
                        .orElseGet(List::of)
                        .stream()
                        .map(this::toFlightLegView)
                        .toList()
        );
    }

    private FlightLegView toFlightLegView(FlightLeg source) {
        return new FlightLegView(
                toLegEndpointView(source.departureInformation()),
                toLegEndpointView(source.arrivalInformation()),
                mapStatus(source.legStatusPublic()),
                source.legStatusPublicLangTransl(),
                source.scheduledFlightDuration(),
                toAircraftView(source.aircraft()),
                computeDelayMinutes(source.arrivalDateTimeDifference()),
                isCancelled(source.legStatusPublic())
        );
    }

    private LegEndpointView toLegEndpointView(LegEndpoint source) {
        if (source == null) {
            return null;
        }
        FlightTimes times = source.times();
        OffsetDateTime estimatedValue = Optional.ofNullable(times)
                .map(FlightTimes::estimated)
                .map(EstimatedTime::value)
                .orElse(null);
        return new LegEndpointView(
                toAirportView(source.airport()),
                times != null ? times.scheduled() : null,
                estimatedValue,
                times != null ? times.actual() : null
        );
    }

    private AirportView toAirportView(Airport source) {
        if (source == null) {
            return null;
        }
        String displayName = Optional.ofNullable(source.nameLangTranl())
                .filter(s -> !s.isBlank())
                .orElse(source.name());
        String cityName = Optional.ofNullable(source.city())
                .map(c -> Optional.ofNullable(c.nameLangTranl()).filter(s -> !s.isBlank()).orElse(c.name()))
                .orElse(null);
        String countryCode = Optional.ofNullable(source.city())
                .map(City::country)
                .map(Country::code)
                .orElse(null);
        return new AirportView(source.code(), displayName, cityName, countryCode);
    }

    private AirlineView toAirlineView(Airline source) {
        return source == null ? null : new AirlineView(source.code(), source.name());
    }

    private AircraftView toAircraftView(Aircraft source) {
        if (source == null) {
            return null;
        }
        return new AircraftView(source.typeName(), source.registration());
    }

    private PageMetadata toPageMetadata(PageInfo source) {
        if (source == null) {
            return new PageMetadata(0, 0, 0, 0);
        }
        int number = source.pageNumber() != null ? source.pageNumber() : 0;
        int size = source.pageSize() != null ? source.pageSize() : 0;
        long total = source.fullCount() != null ? source.fullCount() : 0L;
        int totalPages = source.totalPages() != null ? source.totalPages() : 0;
        return new PageMetadata(number, size, total, totalPages);
    }

    private static String buildFlightNumber(Airline airline, Integer flightNumber) {
        if (flightNumber == null) {
            return null;
        }
        String code = airline != null ? airline.code() : "";
        return code + flightNumber;
    }

    private static long computeDelayMinutes(Duration delay) {
        return delay == null ? 0L : delay.toMinutes();
    }

    private static boolean isCancelled(String airFranceStatus) {
        return airFranceStatus != null && airFranceStatus.toUpperCase().contains("CANCEL");
    }

    private static FlightStatus mapStatus(String airFranceStatus) {
        if (airFranceStatus == null || airFranceStatus.isBlank()) {
            return FlightStatus.UNKNOWN;
        }
        String upper = airFranceStatus.toUpperCase();
        if (upper.contains("CANCEL")) return FlightStatus.CANCELLED;
        if (upper.contains("DIVERT")) return FlightStatus.DIVERTED;
        if (upper.contains("DELAY")) return FlightStatus.DELAYED;
        if (upper.contains("ARRIV")) return FlightStatus.ARRIVED;
        if (upper.contains("DEPART")) return FlightStatus.DEPARTED;
        if (upper.contains("ON_TIME") || upper.contains("ONTIME")) return FlightStatus.ON_TIME;
        if (upper.contains("SCHEDUL")) return FlightStatus.SCHEDULED;
        return FlightStatus.UNKNOWN;
    }
}
