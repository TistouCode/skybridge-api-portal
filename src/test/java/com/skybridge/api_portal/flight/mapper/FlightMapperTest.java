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
import com.skybridge.api_portal.flight.dto.FlightLegView;
import com.skybridge.api_portal.flight.dto.FlightPage;
import com.skybridge.api_portal.flight.dto.FlightStatus;
import com.skybridge.api_portal.flight.dto.FlightView;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlightMapperTest {

    private final FlightMapper mapper = new FlightMapper();

    @Test
    void nullResponseProducesEmptyPage() {
        FlightPage page = mapper.toFlightPage(null);

        assertThat(page.flights()).isEmpty();
        assertThat(page.page().totalElements()).isZero();
    }

    @Test
    void responseWithoutFlightsProducesEmptyList() {
        FlightStatusResponse source = new FlightStatusResponse(null,
                new PageInfo(0, 50, 0, 0));

        FlightPage page = mapper.toFlightPage(source);

        assertThat(page.flights()).isEmpty();
        assertThat(page.page().size()).isEqualTo(50);
    }

    @Test
    void mapsCoreFlightFieldsAndCombinesAirlineCodeWithFlightNumber() {
        FlightStatusResponse source = new FlightStatusResponse(
                List.of(operationalFlight("CDG", "AMS", "Scheduled", "Scheduled")),
                new PageInfo(0, 50, 1, 1));

        FlightPage page = mapper.toFlightPage(source);

        assertThat(page.flights()).hasSize(1);
        FlightView flight = page.flights().getFirst();
        assertThat(flight.flightNumber()).isEqualTo("AF1234");
        assertThat(flight.airline().code()).isEqualTo("AF");
        assertThat(flight.scheduleDate()).isEqualTo(LocalDate.of(2026, 5, 9));
        assertThat(flight.route()).containsExactly("CDG", "AMS");
        assertThat(flight.status()).isEqualTo(FlightStatus.SCHEDULED);
    }

    @Test
    void prefersTranslatedAirportNameWhenAvailable() {
        FlightStatusResponse source = new FlightStatusResponse(
                List.of(operationalFlight("CDG", "AMS", "Scheduled", "Scheduled")),
                new PageInfo(0, 50, 1, 1));

        FlightView flight = mapper.toFlightPage(source).flights().getFirst();
        FlightLegView leg = flight.legs().getFirst();

        assertThat(leg.departure().airport().name()).isEqualTo("Paris-Charles de Gaulle");
        assertThat(leg.departure().airport().city()).isEqualTo("Paris");
        assertThat(leg.departure().airport().country()).isEqualTo("FR");
    }

    @Test
    void mapsCancelledStatusAndExposesFlag() {
        FlightStatusResponse source = new FlightStatusResponse(
                List.of(operationalFlight("CDG", "AMS", "Cancelled", "Cancelled")),
                new PageInfo(0, 50, 1, 1));

        FlightLegView leg = mapper.toFlightPage(source).flights().getFirst().legs().getFirst();

        assertThat(leg.status()).isEqualTo(FlightStatus.CANCELLED);
        assertThat(leg.cancelled()).isTrue();
    }

    @Test
    void mapsDelayedStatusAndConvertsDelayDurationToMinutes() {
        OperationalFlight delayedFlight = new OperationalFlight(
                "id-1", 1234, LocalDate.of(2026, 5, 9),
                new Airline("AF", "Air France"),
                "Delayed", "Delayed",
                List.of("CDG", "AMS"),
                List.of(new FlightLeg(
                        legEndpoint("CDG", "Paris-Charles de Gaulle", "Paris", "FR"),
                        legEndpoint("AMS", "Amsterdam-Schiphol", "Amsterdam", "NL"),
                        "Delayed", "Delayed",
                        Duration.ofMinutes(80),
                        Duration.ZERO,
                        Duration.ofMinutes(45),
                        new Aircraft("773", "Boeing 777-300", "F-GSQA"))));

        FlightStatusResponse source = new FlightStatusResponse(
                List.of(delayedFlight),
                new PageInfo(0, 50, 1, 1));

        FlightLegView leg = mapper.toFlightPage(source).flights().getFirst().legs().getFirst();

        assertThat(leg.status()).isEqualTo(FlightStatus.DELAYED);
        assertThat(leg.delayMinutes()).isEqualTo(45);
        assertThat(leg.scheduledDuration()).isEqualTo(Duration.ofMinutes(80));
        assertThat(leg.aircraft().type()).isEqualTo("Boeing 777-300");
    }

    @Test
    void unknownStatusFallsBackToUnknownEnum() {
        FlightStatusResponse source = new FlightStatusResponse(
                List.of(operationalFlight("CDG", "AMS", "  ", "  ")),
                new PageInfo(0, 50, 1, 1));

        FlightView flight = mapper.toFlightPage(source).flights().getFirst();

        assertThat(flight.status()).isEqualTo(FlightStatus.UNKNOWN);
    }

    private static OperationalFlight operationalFlight(String from, String to,
                                                       String status, String statusLabel) {
        return new OperationalFlight(
                "id-" + from + "-" + to,
                1234,
                LocalDate.of(2026, 5, 9),
                new Airline("AF", "Air France"),
                status,
                statusLabel,
                List.of(from, to),
                List.of(new FlightLeg(
                        legEndpoint(from, "Paris-Charles de Gaulle", "Paris", "FR"),
                        legEndpoint(to, "Amsterdam-Schiphol", "Amsterdam", "NL"),
                        status,
                        statusLabel,
                        Duration.ofMinutes(80),
                        Duration.ZERO,
                        Duration.ZERO,
                        new Aircraft("773", "Boeing 777-300", "F-GSQA"))));
    }

    private static LegEndpoint legEndpoint(String code, String name, String cityName, String countryCode) {
        OffsetDateTime scheduled = OffsetDateTime.of(2026, 5, 9, 8, 30, 0, 0, ZoneOffset.UTC);
        return new LegEndpoint(
                new Airport(code, "Generic", name,
                        new City("PAR", "Generic", cityName, new Country(countryCode, "France", "France"))),
                new FlightTimes(scheduled, scheduled, null, new EstimatedTime(scheduled.plusMinutes(15))));
    }
}
