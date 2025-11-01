package com.nice.avishkar;

import java.util.Objects;

public final class Route {

    private final String source;
    private final String destination;
    private final String mode;
    private final String departureTime;
    private final String arrivalTime;

    public Route(String source, String destination, String mode,
            String departureTime, String arrivalTime) {
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.destination = Objects.requireNonNull(destination, "Destination cannot be null");
        this.mode = Objects.requireNonNull(mode, "Mode cannot be null");
        this.departureTime = Objects.requireNonNull(departureTime, "Departure time cannot be null");
        this.arrivalTime = Objects.requireNonNull(arrivalTime, "Arrival time cannot be null");
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getMode() {
        return mode;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Route route = (Route) o;
        return Objects.equals(source, route.source)
                && Objects.equals(destination, route.destination)
                && Objects.equals(mode, route.mode)
                && Objects.equals(departureTime, route.departureTime)
                && Objects.equals(arrivalTime, route.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, mode, departureTime, arrivalTime);
    }

    @Override
    public String toString() {
        return String.format("Route[%s -> %s via %s, Dep: %s, Arr: %s]",
                source, destination, mode, departureTime, arrivalTime);
    }
}
